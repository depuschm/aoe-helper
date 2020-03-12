package helper;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import gui.AoEHelperGUI;

public class AoEHelper {
	public static boolean quitApplication;
	public static float version = 1.0f;
	
	private static Overlay overlay;
	private static PartialScreenCapture screenCapture;
	private static OCR ocr;
	private static AoEHelperGUI gui;
	
	private static final int MILLISECONDS = 1000;
	
	public static void main(String[] args) {
		// Create overlay, screen capture and OCR (optical character recognition) class to recognize captured images
		overlay = new Overlay();
		screenCapture = new PartialScreenCapture(true);
		ocr = new OCR();
		
		// Create GUI
		gui = new AoEHelperGUI(overlay);
		gui.setVisible(true);
		
		// This is the core part of the program (main loop)
		runMainLoop();
	}
	
	public static void runMainLoop() {
		Timer timer = new Timer();
		TimerTask myTask = new TimerTask() {
			@Override
			public void run() {
				// This part is executed every x milliseconds
				if (AoEHelperGUI.active) {
					
					// Create screen capture
					BufferedImage imagePop = screenCapture.captureAndProcessImage(PartialScreenCapture.popRectangle);
					BufferedImage imageVillagers = screenCapture.captureImage(PartialScreenCapture.villagersRectangle);
					
					// Recognize captured image
					String textPop = ocr.recognize(imagePop, ocr.CHARACTERS_NUMBERS_AND_SLASH);
					//String textVillagers = ocr.recognize(imageVillagers, ocr.CHARACTERS_NUMBERS);
					String textVillagers = screenCapture.hashImageAndLookUpValue(imageVillagers);
					
					// Change text in overlay
					overlay.analyzePopText(textPop);
					overlay.analyzeVillagersText(textVillagers);
				}
				else {
					overlay.clearGUI();
				}
				
				// Quit application if "quitApplication" is set to true
				if (quitApplication) {
					gui.saveSettings();
					System.exit(0);
				}
			}
		};
		timer.schedule(myTask, MILLISECONDS, MILLISECONDS);
	}
}
