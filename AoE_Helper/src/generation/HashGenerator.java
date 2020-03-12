package generation;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import helper.AoEHelper;
import helper.Overlay;
import helper.PartialScreenCapture;

/**
 * Use this class to capture and save images automatically.
 * After that, hashes are generated for images which are used for image recognition.
 * 
 * Presets: Choose pop 500. Make sure your hotkey for town center is "D" and Villager "A". Type "ninjalui" and "aegis" as cheat.
 *          Use huns to not make houses and delete all but your town center and then call this script.
 */
public class HashGenerator {
	private static Overlay overlay;
	private static PartialScreenCapture screenCapture;
	
	private static final int MILLISECONDS = 1000;
	
	public static void main(String[] args) {
		// Create overlay and screen capture
		overlay = new Overlay();
		screenCapture = new PartialScreenCapture(false);
		
		// Simulate key press to select town center
		overlay.SimulateKeyPress(KeyEvent.VK_D);
		overlay.SetTextToDisplay("" + screenCapture.currentImage);
		
		// This is the core part of the program (main loop)
		runMainLoop();
	}
	
	public static void runMainLoop() {
		Timer timer = new Timer();
		TimerTask myTask = new TimerTask() {
			@Override
			public void run() {
				// Capture and save image
				BufferedImage image = screenCapture.captureAndSaveImage(PartialScreenCapture.villagersRectangle);
				
				// Generate and add hash from captured image and add it to hashmap
				int hash = screenCapture.generateHash(image);
				screenCapture.addHashToHashmap(hash);
				
				// Simulate key press
				overlay.SimulateKeyPress(KeyEvent.VK_A);
				
				// Change text in overlay
				overlay.SetTextToDisplay("" + screenCapture.currentImage);
				
				// Quit application if "quitApplication" is set to true or we if we captured 500 images.
				// Debug stop: We also stop if currentImage+1 != hashmap.size() because there was a hash collision.
				boolean collision = screenCapture.currentImage + 1 != screenCapture.hashmap.size();
				if (collision) System.out.println("Collision, make threshold higher in generateHash() or change hash function!");
				
				if (AoEHelper.quitApplication || screenCapture.currentImage == 500 || collision) {
					// Save hashmap into a file before quitting
					screenCapture.saveHashMap("villagers");
					System.exit(0);
				}
				
				// Increase current image value
				screenCapture.currentImage++;
			}
		};
		timer.schedule(myTask, MILLISECONDS, MILLISECONDS);
	}
}