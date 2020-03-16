package helper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.GrayFilter;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

import static marvin.MarvinPluginCollection.*;

/**
 * This class is responsible for image processing.
 * It uses the Marvin framework and own methods to process the image so that it is recognized better.
 */
public class ImageProcessing {

	public static Rectangle popRectangle, villagersRectangle;
	private static int counter; // used for floodfill as pixel counter
	
	public MarvinImage imagePostProcessing(MarvinImage image, Rectangle captureRect) {
		if (captureRect == PartialScreenCapture.popRectangle) {
			return imagePostProcessingPop(image);
		}
		else if (captureRect == PartialScreenCapture.villagersRectangle) {
			// Not used right now
			return imagePostProcessingVillagers(image);
		}
		else if (captureRect == PartialScreenCapture.pointsRectangle) {
			return imagePostProcessingPoints(image);
		}
		return null;
	}

	/**
	 * This method applies some post processing methods to the image for better
	 * recognition later
	 */
	private static MarvinImage imagePostProcessingPop(MarvinImage image) {
		//image = Binarization.GetBmp(image);
		//image = grayScaleImage(image, 95); // making the image darker seems to help differentiating between 0 and 9
		//image = grayScaleImage(image, 50);
		
		//image = Binarization.GetBmp(image);
		//image = equalize(image);
		//image = scaleImage(image, 2.0f, AffineTransformOp.TYPE_BILINEAR);
		//MarvinImagePlugin pluginImage = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.grayScale.jar");
		
		image = removeColorfulPixels(image, 95f / 255, Color.black);
		brightnessAndContrast(image, 50, 100);
		//thresholding(image, 40);
		image = scaleImage(image, 2.0f, 3.0f, AffineTransformOp.TYPE_BILINEAR);
		//grayScale(image); // grayscale image (needed if pop is blinking yellow)
		
		//scale(image.clone(), image, (int) (image.getWidth() * 2.0f), (int) (image.getHeight() * 4.0f));
		
		//sobel(image.clone(), image);
		//brightnessAndContrast(image, 0, 64);
		//blackAndWhite(image, 30);
		//thresholding(image, 30);
		//thresholding(image, 100);
		//invertColors(image);
		//scale(image.clone(), image, (int) (image.getWidth() * scaleFactor), (int) (image.getHeight() * scaleFactor));
		
		/*MarvinImage image2 = new MarvinImage(image);*/
		/*boundaryFill(image.clone(), image, 0, 0, Color.white, 150);
		image.setAlphaByColor(0, 0xFFFFFFFF);
		alphaBoundary(image, 5);*/
		//image = Binarization.GetBmp(image);
		
		//MarvinImageIO.saveImage(image, "CapturedImage.png");
		return image;
	}
	
	private static MarvinImage imagePostProcessingVillagers(MarvinImage image) {
		//brightnessAndContrast(image, -100, 100);
		//thresholding(image, 40);
		//image = scaleImage(image, 4.0f, 8.0f, AffineTransformOp.TYPE_BILINEAR);
		//grayScale(image);
		//morphologicalBoundary(image.clone(), image);
		//int pixels = 10;
		//crop(image.clone(), image, -pixels, -pixels, image.getWidth() + pixels, image.getHeight() + pixels);
		
		//Check: 1,2,9,10,11,12,19,40,90,91,99,100,101,102
		// avoid to change 90 threshold in removeSmall regions before scaling, it's good to recognize 1
		
		image = borderImage(image, 3);
		image = removeColorfulPixels(image, 90f / 255, Color.black);
		//brightnessAndContrast(image, 50, 0);
		//image = removeSmallRegions(image, 2, 90, Color.black);
		//image = removeColorfulPixels(image, 100f / 255, Color.black);
		
		//image = removeColorfulPixels(image, 244f / 255, Color.black);
		//brightnessAndContrast(image, 240, 0);
		//brightnessAndContrast(image, 50, 100);
		//brightnessAndContrast(image, 50, 200);
		//thresholding(image, 200);
		//thresholding(image, 180);
		//thresholding(image, 50);
		//thresholding(image, 3);
		image = scaleImage(image, 4.0f, 4.0f, AffineTransformOp.TYPE_BILINEAR);
		//brightnessAndContrast(image, 240, 0);
		
		thresholding(image, 100); // makes sourrounding bigger/white and helps to make regions
		image = removeSmallRegions(image, 90, 150, Color.black);
		
		//image = removeSmallRegions(image, 90, 150, Color.black);
		
		//image = removeColorfulPixels(image, 90f / 255, Color.black);
		//image = removeSmallRegions(image, 80, 90, Color.black);
		//brightnessAndContrast(image, 200, 0);

		return image;
	}
	
	private static MarvinImage imagePostProcessingPoints(MarvinImage image) {
		//image = removeColorfulPixels(image, 95f / 255, Color.black);
		brightnessAndContrast(image, 50, 100);
		//grayScale(image); // grayscale image (needed if pop is blinking yellow)
		//thresholding(image, 40);
		//image = scaleImage(image, 2.0f, 3.0f, AffineTransformOp.TYPE_BILINEAR);
		//MarvinImageIO.saveImage(image, "CapturedImage.png");
		//image = removeSmallRegions(image, 20, 150, Color.black);
		
		int offset = getOffsetForDoublePoint(image, 40, 1);
		crop(image.clone(), image, offset, 0, image.getWidth() - offset, image.getHeight());
		
		image = scaleImage(image, 2.0f, 3.0f, AffineTransformOp.TYPE_BILINEAR);
		
		//MarvinImageIO.saveImage(image, "CapturedImage.png");
		return image;
	}
	
	private static int getOffsetForDoublePoint(MarvinImage image, int startOffset, int decreaseByPixels) {
		//grayScale(image);
		thresholding(image, 80);
		
		// Calculate when ":" comes in text (check 4 pixels: white/black/white/black until we reached ":" to get offset)
		// After that, crop the image after ":".
		// The image doesn't have to be in grayscale nessesarily, but the red value of the pixel must be at least bigger than
		// the threshold, so colors as green might be difficult to analyze but with thresholding before it works.
		// The threshold should be so small as possible but so big that it still works with the transparent black background
		int offset = startOffset;
		int color1, color2, color3, color4;
		int red1, red2, red3, red4; // only need to check one channel in grayscale image since r=g=b
		int threshold = 30;
		final int DEFAULT_OFFSET = 4; // used to avoid that ":" is on the picture, shifts the offset to the right a bit
		
		for (int x = offset; x > 0; x -= decreaseByPixels) {
			color1 = image.getIntColor(x, 3);
			color2 = image.getIntColor(x, 7);
			color3 = image.getIntColor(x, 11);
			color4 = image.getIntColor(x, 15);
			red1 = (color1 >> 16) & 0xff;
			red2 = (color2 >> 16) & 0xff;
			red3 = (color3 >> 16) & 0xff;
			red4 = (color4 >> 16) & 0xff;
			
			if (red1 < threshold && red2 >= threshold && red3 < threshold && red4 >= threshold) {
				break;
			}
			offset--;
		}
		return offset + DEFAULT_OFFSET;
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
	
	private static MarvinImage scaleImage(MarvinImage before, float scaleFactorX, float scaleFactorY, int type) {
		BufferedImage bufferedImage = before.getBufferedImageNoAlpha();
		bufferedImage = scaleImage(bufferedImage, scaleFactorX, scaleFactorY, type);
		return new MarvinImage(bufferedImage);
	}

	/**
	 * From:
	 * https://stackoverflow.com/questions/4216123/how-to-scale-a-bufferedimage
	 */
	private static BufferedImage scaleImage(BufferedImage before, float scaleFactorX, float scaleFactorY, int type) {
		int w = (int) (before.getWidth() * scaleFactorX);
		int h = (int) (before.getHeight() * scaleFactorY);
		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactorX, scaleFactorY);
		AffineTransformOp scaleOp = new AffineTransformOp(at, type);
		return scaleOp.filter(before, after);
	}
	
	private static MarvinImage borderImage(MarvinImage before, int pixels) {
		BufferedImage bufferedImage = before.getBufferedImageNoAlpha();
		bufferedImage = borderImage(bufferedImage, pixels);
		return new MarvinImage(bufferedImage);
	}
	
	private static BufferedImage borderImage(BufferedImage before, int pixels) {
		int w_old = before.getWidth();
		int h_old = before.getHeight();
		int w = w_old + 2*pixels;
		int h = h_old + 2*pixels;
		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		for (int y = 0; y < h_old; y++) {
			for (int x = 0; x < w_old; x++) {
				after.setRGB(x + pixels, y + pixels, before.getRGB(x, y));
			}
		}
		
		return after;
	}
	
	private static MarvinImage removeColorfulPixels(MarvinImage before, float threshold, Color color) {
		BufferedImage bufferedImage = before.getBufferedImageNoAlpha();
		bufferedImage = removeColorfulPixels(bufferedImage, threshold, color);
		return new MarvinImage(bufferedImage);
	}
	
	/**
	 * Use this method to remove colors where rgb are not the same
	 * (that means colors inequal to white, gray or black)
	 * 
	 * @param threshold should be between 0 and 1
	 * Making threshold near to 1 means that the pixels that have high hue will be removed,
	 * if threshold is near to 0 also pixels are removed that have not so high hue.
	 */
	private static BufferedImage removeColorfulPixels(BufferedImage image, float threshold, Color color) {
		Color currentColor;
		int removeColor = color.getRGB();
		int w = image.getWidth();
		int h = image.getHeight();
		int sum_rgb = 0;
		int r, g, b;
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				currentColor = new Color(image.getRGB(x, y));
				
				r = currentColor.getRed();
				g = currentColor.getGreen();
				b = currentColor.getBlue();
				sum_rgb = r + g + b;
				
				// If there is a (great) hue difference
				if (r >= sum_rgb*threshold || g >= sum_rgb*threshold || b >= sum_rgb*threshold) {
					// Change color
					image.setRGB(x, y, removeColor);
				}
			}
		}
		return image;
	}
	
	private static MarvinImage removeSmallRegions(MarvinImage before, int minimalPixels, int threshold, Color color) {
		BufferedImage bufferedImage = before.getBufferedImageNoAlpha();
		bufferedImage = removeSmallRegions(bufferedImage, minimalPixels, threshold, color);
		return new MarvinImage(bufferedImage);
		
		// todo: return: for each region: boundaryFill(color black)
	}
	
	private static BufferedImage removeSmallRegions(BufferedImage image, int minimalPixels, int threshold, Color color) {
		int w = image.getWidth();
		int h = image.getHeight();
		int removeColor = color.getRGB();
		
		int[][] regions = new int[w][h]; // stores "counter" values (counter indicates how much pixels nearby have the same color)
		// Initialize regions with -1
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				regions[x][y] = -1;
			}
		}
		
		// Apply floodfill on every (not visited) pixel
		// Note: color is not set here, only regions array is filled with values
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (regions[x][y] == -1) {
					Color c = new Color(image.getRGB(x, y));
					floodFillImage(image, x, y, color, regions, threshold);
				}
			}
		}
		
		// Set color to regions (optional: print regions)
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				// If connected with at least "minimalPixels" pixels remove color
				if (regions[x][y] <= minimalPixels) {
					image.setRGB(x, y, removeColor);
				}
				//System.out.print(regions[x][y] + "\t");
			}
			//System.out.println();
		}
		//System.out.println();
		
		return image;
	}
	
	/**
	 * Floodfill method using a queue, slightly changed to fit the task
	 * From: https://stackoverflow.com/questions/2783204/flood-fill-using-a-stack
	 * Threshold idea from: https://www.geeksforgeeks.org/java-applet-implementing-flood-fill-algorithm/
	 * 
	 * Threshold: How different a color should be to be painted (0 = color equal, 85 = total different color)
	 */
	public static void floodFillImage(BufferedImage image,int x, int y, Color color, int[][] array, int threshold) {
	    int srcColor = image.getRGB(x, y);
	    boolean[][] hits = new boolean[image.getHeight()][image.getWidth()];

	    List<Point> pointsVisited = new ArrayList<>();
	    counter = 0;
	    
	    Queue<Point> queue = new LinkedList<Point>();
	    queue.add(new Point(x, y));

	    while (!queue.isEmpty()) 
	    {
	        Point p = queue.remove();

	        if(floodFillImageDo(image,hits,p.x,p.y, srcColor, color.getRGB(), array, pointsVisited, threshold))
	        {     
	            queue.add(new Point(p.x,p.y - 1)); 
	            queue.add(new Point(p.x,p.y + 1)); 
	            queue.add(new Point(p.x - 1,p.y)); 
	            queue.add(new Point(p.x + 1,p.y));
	            
	            queue.add(new Point(p.x - 1,p.y - 1));
	            queue.add(new Point(p.x - 1,p.y + 1));
	            queue.add(new Point(p.x + 1,p.y - 1));
	            queue.add(new Point(p.x + 1,p.y + 1));
	        }
	    }
	    
	    // Mark all visited points with counter value
        for (int i = 0; i < pointsVisited.size(); i++) {
        	Point pV = pointsVisited.get(i);
        	array[pV.x][pV.y] = counter;
		}
	}

	private static boolean floodFillImageDo(BufferedImage image, boolean[][] hits,int x, int y, int srcColor, int tgtColor,
			int[][] array, List<Point> pointsVisited, int threshold) {
		
	    if (y < 0) return false;
	    if (x < 0) return false;
	    if (y > image.getHeight()-1) return false;
	    if (x > image.getWidth()-1) return false;
	    if (array[x][y] != -1) return false;

	    if (hits[y][x]) return false;
	    
	    // if there is no boundary (the color is almost
		// same as the color of the point where
		// floodfill is to be applied
	    Color currentColor = new Color(image.getRGB(x, y));
	    Color startColor = new Color(srcColor);
	    
		if (!(Math.abs(currentColor.getRed() - startColor.getRed()) < threshold
			&& Math.abs(currentColor.getGreen() - startColor.getGreen()) < threshold
			&& Math.abs(currentColor.getBlue() - startColor.getBlue()) < threshold)) {
			return false;
		}
	    
		// If pixel colors are not equal, stop
		//if (image.getRGB(x, y)!=srcColor)
	    //    return false;

	    // valid, paint it
	    //image.setRGB(x, y, tgtColor);
	    counter++;
	    pointsVisited.add(new Point(x, y));
	    hits[y][x] = true;
	    return true;
	}
}