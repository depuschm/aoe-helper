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
				// This part is executed every x milliseconds
				
				// Create screen capture
				BufferedImage imagePop = screenCapture.captureImage(PartialScreenCapture.popRectangle);
				//BufferedImage imageVillagers = screenCapture.captureImage(PartialScreenCapture.villagersRectangle);
				
				// Recognize captured image
				String textPop = ocr.recognize(imagePop, ocr.CHARACTERS_NUMBERS_AND_SLASH);
				//String textVillagers = ocr.recognize(imageVillagers, ocr.CHARACTERS_NUMBERS);
				
				// Change text in overlay
				overlay.SetTextToDisplay(textPop);
				//overlay.SetTextToDisplay(textVillagers);
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
