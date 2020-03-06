package helper;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;

/**
 * This program demonstrates how to capture screenshot of a portion of screen.
 * From:
 * https://www.codejava.net/java-se/graphics/how-to-capture-screenshot-programmatically-in-java
 * 
 * @author www.codejava.net
 *
 */
public class PartialScreenCapture {

	private Rectangle villagerPopRectangle;
	private Dimension screenSize;

	public PartialScreenCapture() {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		villagerPopRectangle = new Rectangle(450, 20, 135, 40);
	}

	/**
	 * This is the method that should be used to capture images
	 */
	public BufferedImage captureImage() {
		try {
			Robot robot = new Robot();
			Rectangle captureRect = villagerPopRectangle;
			BufferedImage capturedImage = robot.createScreenCapture(captureRect);
			capturedImage = imagePostProcessing(capturedImage);
			//saveImage(capturedImage);
			//System.out.println("A partial screenshot captured!");
			
			return capturedImage;

		} catch (AWTException ex) {
			System.err.println(ex);
			return null;
		}
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

	/**
	 * This method applies some post processing methods to the image for better
	 * recognition later
	 */
	private static BufferedImage imagePostProcessing(BufferedImage image) {
		//image = Binarization.GetBmp(image);
		image = grayScaleImage(image, 96); // making the image darker seems to help differentiating between 0 and 9
		
		image = Binarization.GetBmp(image);
		image = scaleImage(image, 2.0f, AffineTransformOp.TYPE_BILINEAR);
		image = Binarization.GetBmp(image);

		return image;
	}

	/**
	 * https://stackoverflow.com/questions/9131678/convert-a-rgb-image-to-grayscale-image-reducing-the-memory-in-java
	 * grayness is between 0 (white) and 100 (black)
	 */
	public static BufferedImage grayScaleImage(BufferedImage image, int grayness) {
		ImageFilter filter = new GrayFilter(true, grayness);
		ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
		return ImageToBufferedImage(Toolkit.getDefaultToolkit().createImage(producer));
	}

	/**
	 * https://stackoverflow.com/questions/665406/how-to-make-a-color-transparent-in-a-bufferedimage-and-save-as-png
	 */
	private static BufferedImage ImageToBufferedImage(Image image) {
		BufferedImage dest = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return dest;
	}

	/**
	 * From:
	 * https://stackoverflow.com/questions/4216123/how-to-scale-a-bufferedimage
	 */
	private static BufferedImage scaleImage(BufferedImage before, float scaleFactor, int type) {
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactor, scaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, type);
		return scaleOp.filter(before, after);
	}
}