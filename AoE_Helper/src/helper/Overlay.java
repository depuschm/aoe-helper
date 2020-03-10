package helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

/**
 * From: https://stackoverflow.com/questions/11217660/java-making-a-window-click-through-including-text-images
 */
public class Overlay implements NativeKeyListener {
	
	private String textToDisplay, textBO;
	private JComponent paintComponent;
	
	private BufferedImage imageHouse;
	private boolean houseNeeded;
	private static JSONObject darkAge;
	
	public Overlay() {
		textToDisplay = "Age of Empires";
		textBO = "";
		InitJNativeHook();
		Window w = new Window(null);
		
		// Load images
		File resource = new File("data/images/house.png");
        try {
            imageHouse = ImageIO.read(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Load Text
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("data/text/bo_fastcastle-boom.json")) {
        	//Read JSON file
        	Object obj = jsonParser.parse(reader);
        	
        	JSONArray list = (JSONArray) obj;
        	//System.out.println(list);
        	
        	//Get json object within list
        	list.forEach(txt -> parseJSONObject((JSONObject) txt));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	    
	    /**
	     * This sets the background of the window to be transparent.
	     */
	    //AWTUtilities.setWindowOpaque(w, false);
        //w.setBackground(new Color(255, 255, 255, 60)); 
        w.setBackground(new Color(0, 0, 0, 0)); 
        
        paintComponent = (JComponent) w.add(new JComponent() {
	        /**
	         * This will draw a black cross on screen.
	         */
	        protected void paintComponent(Graphics g) {
	        	super.paintComponent(g);
	        	Graphics2D g2 = (Graphics2D) g;
	        	
	            //g.fillRect(0, getHeight() / 2 - 10, getWidth(), 20);
	            //g.fillRect(getWidth() / 2 - 10, 0, 20, getHeight());
	            //g.drawString("Hello World", 100, 100);
	            
	            // Draw text
	            int x = getWidth() / 2;
	            int y = 20;
	            
	            drawTextWithBackground(g2, textToDisplay, x, y);
	            drawTextWithBackground(g2, textBO, x, y + 20);
	            
	            // Draw image of house if almost housed
	            if (houseNeeded)
	            	g2.drawImage(imageHouse, 405, 0, 100, 100, this);
	            //repaint();
	        }

	        public Dimension getPreferredSize() {
	        	//return new Dimension(300, 100);
	        	return new Dimension(1920, 840);
	            //return Toolkit.getDefaultToolkit().getScreenSize();
	        }
	    });
        
	    w.pack();
	    //w.setLocationRelativeTo(null);
	    w.setLocation(new Point(0, 58));
	    w.setVisible(true);
	    w.setAlwaysOnTop(true);
	    
	    setTransparent(w);
	}
	
	 private static void parseJSONObject(JSONObject json) {
		 // Get triggers object within list
		 JSONObject triggers = (JSONObject) json.get("triggers");
		 JSONObject population = (JSONObject) triggers.get("population");
		 darkAge = (JSONObject) population.get("dark_age");
		 
		// Print first trigger
	    //String trigger = getTextFromJSONObject(darkAge, "3");
	    //System.out.println(trigger);
	 }
	 
	 private static String getTextFromJSONObject(JSONObject jsonObject, String key) {
		 if (jsonObject.containsKey(key)) {
			 return (String) jsonObject.get(key);
		 }
		 return "";
	 }
	
	/**
	 * Draws a black text with a white background behind to highlight the text
	 */
	private void drawTextWithBackground(Graphics2D g2, String text, int x, int y) {
		// Draw text background
		Rectangle bounds = getStringBounds(g2, text, x, y);
        extendRectangle(bounds, 3, 3);
        
        g2.setColor(Color.WHITE);
        g2.fill(bounds);
        
        // TODO: Fix flickering, text causes flickering, maybe just draw if text really changed (use textPrevious)
        g2.setColor(Color.BLACK);
        g2.drawString(text, x, y);
	}
	
	/**
	 * From: https://stackoverflow.com/questions/368295/how-to-get-real-string-height-in-java/12495108
	 */
	private Rectangle getStringBounds(Graphics2D g2, String str, float x, float y) {
		FontRenderContext frc = g2.getFontRenderContext();
		GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}
	
	/**
	 * From: https://stackoverflow.com/questions/20899390/how-to-make-boundscollision-rectangle-smaller-size-than-the-sprite-in-libgdx
	 */
	private void extendRectangle(Rectangle bounds, int horizontalPixels, int verticalPixels) {
		Dimension newSize = new Dimension(bounds.width + horizontalPixels*2 + 1, bounds.height + verticalPixels*2 + 1);
		bounds.setLocation(bounds.x-horizontalPixels, bounds.y-verticalPixels);
		bounds.setSize(newSize);
	}

	private static void setTransparent(Component w) {
	    WinDef.HWND hwnd = getHWnd(w);
	    int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
	    wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
	    User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
	}

	/**
	 * Get the window handle from the OS
	 */
	private static HWND getHWnd(Component w) {
	    HWND hwnd = new HWND();
	    hwnd.setPointer(Native.getComponentPointer(w));
	    return hwnd;
	}
	
	/**
	 * Setter for textToDisplay
	 */
	public void SetTextToDisplay(String text) {
		//textToDisplay = text;;
		textToDisplay = analyseText(text); // TODO: analyseText doesn't work with textVillagers!
		paintComponent.repaint();
	}
	
	private String analyseText(String text) {
		String[] textSplit = text.split("/");
		
		// Reset variables
		houseNeeded = false;
		textBO = "";
		
		// Verify that split was successful
		try {
			// Get pop and max pop
			int pop = Integer.parseInt(textSplit[0].trim());
			int pop_max = Integer.parseInt(textSplit[1].trim());
			
			// Add house warning
			if (pop + 2 >= pop_max) {
				houseNeeded = true;
			}
			
			// Add build order text
			textBO = getTextFromJSONObject(darkAge, "" + (pop - 1));
		}
		catch (Exception e) {
			// Exception might happen either if / was not found or parsing was not successful
			//System.err.println(e.getMessage());
			text = "Not ingame";
		}
		
		return text;
	}
	
	/**
	 * Initializes JNativeHook for keyboard interaction
	 * 
	 * Registering hook from:
	 * https://github.com/kwhat/jnativehook/wiki/Usage
	 * 
	 * Logging removal from:
	 * https://stackoverflow.com/questions/30560212/how-to-remove-the-logging-data-from-jnativehook-library
	 */
	private void InitJNativeHook() {
		// 1. Get rid of logging
		// Clear previous logging configurations.
		LogManager.getLogManager().reset();

		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		
		// 2. Register native hook
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(this);
	}
	
	/**
	 * Clear exit:
	 * https://stackoverflow.com/questions/46280150/close-program-with-keypress-during-a-loop
	 */
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		// Exit program if exit key was pressed.
		if (e.getKeyCode() == NativeKeyEvent.VC_PERIOD) {
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e1) {
				e1.printStackTrace();
			}
			
			AoEHelper.quitApplication = true;
			return;
		}
		
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}