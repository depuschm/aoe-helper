package helper;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import marvin.image.MarvinImage;

/**
 * This program captures a screenshot of a portion of screen.
 * From: https://www.codejava.net/java-se/graphics/how-to-capture-screenshot-programmatically-in-java
 */
public class PartialScreenCapture {

	public static Rectangle popRectangle, civilizationRectangle, ageRectangle, ageAdvancingRectangle, pointsRectangle, mapRectangle;
	public static Rectangle villagersRectangle, foodRectangle, woodRectangle, goldRectangle, stoneRectangle;
	private ImageProcessing imageProcessing;
	private Dimension screenSize;
	private Robot robot;
	public int currentImage;
	public HashMap<Integer, Integer> hashmapVillagers, hashmapCivilizations, hashmapAges;

	public PartialScreenCapture(boolean loadHashmaps) {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		if (loadHashmaps) {
			hashmapVillagers = loadHashMap("villagers");
			hashmapCivilizations = loadHashMap("civilizations");
			hashmapAges = loadHashMap("ages");
		}
		else {
			hashmapVillagers = new HashMap<>();
			hashmapCivilizations = new HashMap<>();
			hashmapAges = new HashMap<>();
		}
		
		imageProcessing = new ImageProcessing();
		
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		// Initialize rectangles (part of screen to capture)
		popRectangle = new Rectangle(455, 24, 58, 11);
		civilizationRectangle = new Rectangle(1624, 20, 2, 26);
		ageRectangle = new Rectangle(600, 20, 2, 22);
		ageAdvancingRectangle = new Rectangle(626, 38, 1, 1);
		pointsRectangle = new Rectangle(1750, 863, 110, 18);
		mapRectangle = new Rectangle(1557, 892, 345, 186);
		
		villagersRectangle = new Rectangle(423, 39, 22, 8);
		foodRectangle = new Rectangle(124, 39, 22, 8);
		woodRectangle = new Rectangle(24, 39, 22, 8);
		goldRectangle = new Rectangle(224, 39, 22, 8);
		stoneRectangle = new Rectangle(324, 39, 22, 8);
	}

	/**
	 * This is the method that should be used to capture images
	 */
	public BufferedImage captureImage(Rectangle captureRect) {
		return robot.createScreenCapture(captureRect);
	}
	
	public BufferedImage captureAndProcessImage(Rectangle captureRect) {
		BufferedImage capturedImage = robot.createScreenCapture(captureRect);
		MarvinImage image = new MarvinImage(capturedImage);
		image = imageProcessing.imagePostProcessing(image, captureRect);
		//MarvinImageIO.saveImage(image, "CapturedImage.png");
		
		return image.getBufferedImageNoAlpha();
	}
	
	/**
	 * Debug method to generate images from resource panel
	 */
	public BufferedImage captureAndSaveImage(Rectangle captureRect, String folder) {
		// Capture and save image
		BufferedImage capturedImage = robot.createScreenCapture(captureRect);
		saveImage(capturedImage, folder);
		
		return capturedImage;
	}
	
	private void saveImage(BufferedImage image, String folder) {
		String format = "png";
		String fileName = "data/numbers/images/" + folder + "/" + currentImage + "." + format;
        
		try {
			ImageIO.write(image, format, new File(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates a hash for an image and saves it
	 * Threshold should be as low as possible so that only really white parts are considered for the hash
	 * but it should still be high enough to have no hash collisions
	 */
	public int generateHashWhite(BufferedImage image) {
		// Simple hash: Only use white pixels and add the together according to the formula y*w + x
		int hash = 0;
		Color currentColor;
		int w = image.getWidth();
		int h = image.getHeight();
		int threshold = 30;
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				currentColor = new Color(image.getRGB(x, y));
				
				// If color is almost white
				if (currentColor.getRed() >= 255-threshold &&
					currentColor.getGreen() >= 255-threshold &&
					currentColor.getBlue() >= 255-threshold) {
					// Add it to hash
					//hash += 300*(255-currentColor.getRed()) + (y*w + x);
					hash += 1000*(255-currentColor.getRed()) + (y*w + x); // TODO: y*w+x is just a constant at the end (sum of all pixels, so needed??)
				}
			}
		}
		
		return hash;
	}
	
	public int generateHash(BufferedImage image) {
		// Simple hash: Only use white pixels and add the together according to the formula y*w + x
		// image.getRGB(x, y) might have collisions since the order of colors is not important but very rarely
		int hash = 0;
		int w = image.getWidth();
		int h = image.getHeight();
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				hash += image.getRGB(x, y);
				//hash += currentColor.getRed() + currentColor.getGreen() + currentColor.getBlue() * (y*w + x);
			}
		}
		
		return hash;
	}
	
	/**
	 * From: https://stackoverflow.com/questions/2808277/can-we-write-a-hashtable-to-a-file
	 *       https://beginnersbook.com/2013/12/how-to-serialize-hashmap-in-java/
	 * @throws FileNotFoundException 
	 */
	public void saveHashMap(HashMap<Integer, Integer> hashmap, String name) {
		String format = "txt";
		String fileName = "data/numbers/key-value/" + name + "." + format;
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(hashmap);
			oos.close();
			fos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Debug: Print Hashmap size
		System.out.println("HashMap size: " + hashmap.size());
	}
	
	public HashMap<Integer, Integer> loadHashMap(String name) {
		String format = "txt";
		String fileName = "data/numbers/key-value/" + name + "." + format;
		HashMap<Integer, Integer> hashmap = new HashMap<>();
		
		try {
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			hashmap = (HashMap<Integer, Integer>) ois.readObject();
			ois.close();
			fis.close();
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// Debug: Display content using Iterator
		/*Set set = hashmap.entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry)iterator.next();
			System.out.print("key: "+ mentry.getKey() + " & Value: ");
			System.out.println(mentry.getValue());
		}
		System.out.println();*/
		
		return hashmap;
	}
	
	/**
	 * Generates a hash from an image which serves as a key to lookup the corresponding
	 * value in the hashmap
	 */
	public String hashImageAndLookUpValue(BufferedImage image, HashMap<Integer, Integer> hashmap, int hashMethod) {
		int hash;
		if (hashMethod == 1) {
			hash = generateHashWhite(image);
		}
		else {
			hash = generateHash(image);
		}
		
		Integer value = hashmap.get(hash);
		//System.out.println("-> key: " + hash + " & Value: " + value);
		
		if (value == null) {
			return "-1";
		}
		
		return "" + value;
	}
	
	
	/**
	 * Expects an 1x1 image. Checks if the single pixel of the image is red enough.
	 * @param threshold
	 */
	public boolean checkIfRed(BufferedImage image, int threshold) {
		Color color = new Color(image.getRGB(0, 0));
		if (color.getRed() >= threshold) {
			return true;
		}
		return false;
	}
}