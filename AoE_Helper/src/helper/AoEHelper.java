package helper;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import gui.AoEHelperGUI;

public class AoEHelper {
	public static boolean quitApplication, showGenerationOverlay;
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
					BufferedImage imageCivilization = screenCapture.captureImage(PartialScreenCapture.civilizationRectangle);
					BufferedImage imageAge = screenCapture.captureImage(PartialScreenCapture.ageRectangle);
					BufferedImage imageAgeAdvancing = screenCapture.captureImage(PartialScreenCapture.ageAdvancingRectangle);
					
					BufferedImage imageVillagers = screenCapture.captureImage(PartialScreenCapture.villagersRectangle);
					BufferedImage imageFood = screenCapture.captureImage(PartialScreenCapture.foodRectangle);
					BufferedImage imageWood = screenCapture.captureImage(PartialScreenCapture.woodRectangle);
					BufferedImage imageGold = screenCapture.captureImage(PartialScreenCapture.goldRectangle);
					BufferedImage imageStone = screenCapture.captureImage(PartialScreenCapture.stoneRectangle);
					
					// Recognize captured image
					String textPop = ocr.recognize(imagePop, ocr.CHARACTERS_NUMBERS_AND_SLASH);
					String civilization = screenCapture.hashImageAndLookUpValue(imageCivilization, screenCapture.hashmapCivilizations, 0);
					String age = screenCapture.hashImageAndLookUpValue(imageAge, screenCapture.hashmapAges, 0);
					boolean ageAdvancing = screenCapture.checkIfRed(imageAgeAdvancing, 100);
					
					String textVillagers = screenCapture.hashImageAndLookUpValue(imageVillagers, screenCapture.hashmapVillagers, 1);
					String textFood = screenCapture.hashImageAndLookUpValue(imageFood, screenCapture.hashmapVillagers, 1);
					String textWood = screenCapture.hashImageAndLookUpValue(imageWood, screenCapture.hashmapVillagers, 1);
					String textGold = screenCapture.hashImageAndLookUpValue(imageGold, screenCapture.hashmapVillagers, 1);
					String textStone = screenCapture.hashImageAndLookUpValue(imageStone, screenCapture.hashmapVillagers, 1);
					
					// Change text in overlay
					overlay.analyzePopText(textPop);
					overlay.analyzeCivilization(civilization);
					overlay.analyzeAge(age, ageAdvancing);
					
					overlay.analyzeVillagersText(textVillagers);
					overlay.analyzeFoodText(textFood);
					overlay.analyzeWoodText(textWood);
					overlay.analyzeGoldText(textGold);
					overlay.analyzeStoneText(textStone);
				}
				
				// Update GUI
				overlay.UpdateGUI();
				
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
