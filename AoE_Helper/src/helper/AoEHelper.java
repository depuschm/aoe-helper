package helper;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class AoEHelper {
	public static boolean quitApplication;
	
	public static void main(String[] args) {
		// Create overlay, screen capture and OCR (optical character recognition) class to recognize captured images
		Overlay overlay = new Overlay();
		PartialScreenCapture screenCapture = new PartialScreenCapture();
		OCR ocr = new OCR();
		
		// This is the core part of the program (main loop)
		int milliseconds = 1000;
		Timer timer = new Timer();
		TimerTask myTask = new TimerTask() {
		    @Override
		    public void run() {
		        // This part is executed every x millisecondsw
		    	
		    	// Create screen capture
				BufferedImage image = screenCapture.captureImage();
				
				// Recognize captured image
				String text = ocr.recognize(image);
				
				// Change text in overlay
				overlay.SetTextToDisplay(text);
				//System.out.println(text);
				
				// Quit application if "quitApplication" is set to true
				if (quitApplication) {
					System.exit(0);
				}
		    }
		};
		timer.schedule(myTask, milliseconds, milliseconds);
	}
}
