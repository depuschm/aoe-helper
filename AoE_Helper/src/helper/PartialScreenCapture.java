package helper;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import marvin.image.MarvinImage;

/**
 * This program captures a screenshot of a portion of screen.
 * From: https://www.codejava.net/java-se/graphics/how-to-capture-screenshot-programmatically-in-java
 */
public class PartialScreenCapture {

	public static Rectangle popRectangle, villagersRectangle;
	private ImageProcessing imageProcessing;
	private Dimension screenSize;
	private Robot robot;

	public PartialScreenCapture() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		imageProcessing = new ImageProcessing();
		
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		popRectangle = new Rectangle(453, 22, 61, 16);
		//villagersRectangle = new Rectangle(433, 39, 11, 8);
		//villagersRectangle = new Rectangle(433, 38, 14, 10);
		//villagersRectangle = new Rectangle(437, 39, 8, 8);
		
		//villagersRectangle = new Rectangle(433, 39, 12, 8);
		
		//villagersRectangle = new Rectangle(424, 39, 20, 8); // exact 100
		villagersRectangle = new Rectangle(423, 39, 22, 8); // correctly recognized 100
	}

	/**
	 * This is the method that should be used to capture images
	 */
	public BufferedImage captureImage(Rectangle captureRect) {
		BufferedImage capturedImage = robot.createScreenCapture(captureRect);
		MarvinImage image = new MarvinImage(capturedImage);
		image = imageProcessing.imagePostProcessing(image, captureRect);
		//MarvinImageIO.saveImage(image, "CapturedImage.png");
		//saveImage(capturedImage);
		//System.out.println("A partial screenshot captured!");
		
		return image.getBufferedImageNoAlpha();
	}
	
	private void saveImage(BufferedImage image) {
		String format = "jpg";
		String fileName = "CapturedImage." + format;
        
		try {
			ImageIO.write(image, format, new File(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}