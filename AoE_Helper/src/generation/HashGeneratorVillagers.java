package generation;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import helper.AoEHelper;
import helper.Overlay;
import helper.PartialScreenCapture;

/**
 * Use this class to capture and save villager count images automatically.
 * After that, hashes are generated for images which are used for image recognition.
 * 
 * Presets: Choose pop 500. Make sure your hotkey for town center is "D" and Villager "A". Type "ninjalui" and "aegis" as cheat.
 *          Use huns to not make houses and delete all but your town center and then call this script.
 */
public class HashGeneratorVillagers {
	private static Overlay overlay;
	private static PartialScreenCapture screenCapture;
	
	private static final int MILLISECONDS = 1000;
	
	public static void main(String[] args) {
		// Show generation overlay
		AoEHelper.showGenerationOverlay = true;
		
		// Create overlay and screen capture
		overlay = new Overlay();
		screenCapture = new PartialScreenCapture(false);
		
		// This is the core part of the program (main loop)
		//generateHashmapByCapturingImages("villagers");
		generateHashmapByStoredImages("villagers");
	}
	
	/**
	 * This method captures images automatically.
	 * After that, the hash of the image is calculated and stored into a hashmap.
	 * The method quits if hash collision where found.
	 */
	public static void generateHashmapByCapturingImages(String name) {
		// Simulate key press to select town center
		overlay.SimulateKeyPress(KeyEvent.VK_D);
		
		Timer timer = new Timer();
		TimerTask myTask = new TimerTask() {
			@Override
			public void run() {
				// Capture and save image
				BufferedImage image = screenCapture.captureAndSaveImage(PartialScreenCapture.villagersRectangle, name);
				
				// Generate and add hash from captured image and add it to hashmap
				int hash = screenCapture.generateHashWhite(image);
				screenCapture.hashmapVillagers.put(hash, screenCapture.currentImage);
				
				// Simulate key press
				overlay.SimulateKeyPress(KeyEvent.VK_A);
				
				// Change text in overlay
				overlay.SetTextToDisplay("" + screenCapture.currentImage);
				
				// Quit application if "quitApplication" is set to true or we if we captured 501 images.
				// Debug stop: We also stop if currentImage+1 != hashmap.size() because there was a hash collision.
				boolean collision = screenCapture.currentImage + 1 != screenCapture.hashmapVillagers.size();
				if (collision) System.out.println("Collision, make threshold higher in generateHash() or change hash function!");
				
				if (AoEHelper.quitApplication || screenCapture.currentImage == 500 || collision) {
					// Save hashmap into a file before quitting
					screenCapture.saveHashMap(screenCapture.hashmapVillagers, name);
					System.exit(0);
				}
				
				// Increase current image value
				screenCapture.currentImage++;
				
				// Update GUI
				overlay.UpdateGUI();
			}
		};
		timer.schedule(myTask, MILLISECONDS, MILLISECONDS);
	}
	
	/**
	 * Requires to have the images already stored as files so that they can be loaded.
	 * This method generates hashes for all loaded images and stores them into a hashmap.
	 */
	public static void generateHashmapByStoredImages(String name) {
		BufferedImage[] images = loadImages(name);
		
		for (int i = 0; i < images.length; i++) {
			// Generate and add hash from captured image and add it to hashmap
			int hash = screenCapture.generateHashWhite(images[i]);
			screenCapture.hashmapVillagers.put(hash, screenCapture.currentImage);
			
			// We stop if currentImage+1 != hashmap.size() because there was a hash collision.
			boolean collision = screenCapture.currentImage + 1 != screenCapture.hashmapVillagers.size();
			if (collision) {
				System.out.println("Collision, change hash function!");
				break;
			}
			
			// Increase current image value
			screenCapture.currentImage++;
		}
		
		// Save hashmap into a file before quitting
		screenCapture.saveHashMap(screenCapture.hashmapVillagers, name);
		System.exit(0);
	}
	
	private static BufferedImage[] loadImages(String name) {
    	List<String> fileNames = new ArrayList<>();
    	
    	// List all files from directory
    	// From: https://mkyong.com/java/java-how-to-list-all-files-in-a-directory/
    	try (Stream<Path> walk = Files.walk(Paths.get("data/numbers/images/" + name))) {

    		fileNames = walk.filter(Files::isRegularFile)
    				.map(x -> x.toString()).sorted().collect(Collectors.toList());
    		
    		//result.forEach(System.out::println);
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	// Initialize images array
    	int fileNamesSize = fileNames.size();
    	BufferedImage[] images = new BufferedImage[fileNamesSize];
    	File[] files = new File[fileNamesSize];
    	
    	for (int i = 0; i < files.length; i++) {
			files[i] = new File(fileNames.get(i));
		}
    	sortByNumber(files);
    	
    	// Load title
    	for (int i = 0; i < fileNamesSize; i++) {
    		try {
    			images[i] = ImageIO.read(files[i]);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
		}
    	
		return images;
    }
	
	/**
	 * Numerical sort
	 * From: https://stackoverflow.com/questions/16898029/how-to-sort-file-names-in-ascending-order
	 */
	private static void sortByNumber(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                int i = 0;
                try {
                    int s = name.indexOf('_')+1;
                    int e = name.lastIndexOf('.');
                    String number = name.substring(s, e);
                    i = Integer.parseInt(number);
                } catch(Exception e) {
                    i = 0; // if filename does not match the format
                           // then default to 0
                }
                return i;
            }
        });

        /*for(File f : files) {
            System.out.println(f.getName());
        }*/
    }
}