package helper;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
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

import gui.AoEHelperGUI;

import com.sun.jna.platform.win32.WinUser;

/**
 * From: https://stackoverflow.com/questions/11217660/java-making-a-window-click-through-including-text-images
 */
public class Overlay implements NativeKeyListener {
	
	private String textMain, textPop, textCivilization, textAge, textBO, textPoints, textExplored;
	private String textVillagers, textFood, textWood, textGold, textStone;
	private JComponent paintComponent;
	private Robot robot;
	public boolean clearGUI;
	public AoEHelperGUI gui;
	public boolean ingame;
	
	private int points_previous;
	
	private BufferedImage imageHouse, imageMapMask, imageMap;
	private boolean houseNeeded;
	private String[] civilizationNames, ageNames;
	private String lastRecognizedCiv; // TODO: use this variable to remember recognized civ
	private Age lastRecognizedAge; // TODO: use this variable to remember recognized age
	private static JSONObject population_darkAge, population_feudalAge, population_castleAge, population_imperialAge;
	private static JSONObject timer_darkAge, timer_feudalAge, timer_castleAge, timer_imperialAge;
	private static JSONObject timer_darkAgeAdvancing, timer_feudalAgeAdvancing, timer_castleAgeAdvancing;
	
	public enum Age
	{
		Dark, Feudal, Castle, Imperial
	}
	
	public Overlay() {
		textMain = "";
		ResetVariables();
		
		points_previous = -1;
		
		InitCivilizationNames();
		InitAgeNames();
		
		InitJNativeHook(this);
		Window w = new Window(null);
		
		// Initialize robot
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		
		// Load images
		File file_house = new File("data/images/house.png");
		File file_map_mask = new File("data/images/map_mask.png");
		
        try {
            imageHouse = ImageIO.read(file_house);
            imageMapMask = ImageIO.read(file_map_mask);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Load build order text (Not used here, now in AoEHelperGUI.loadBuildOrderText)
        //loadBuildOrderText();
	    
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
	        	
	        	if (AoEHelperGUI.active && gui != null) {
	        		//g.fillRect(0, getHeight() / 2 - 10, getWidth(), 20);
		            //g.fillRect(getWidth() / 2 - 10, 0, 20, getHeight());
		            //g.drawString("Hello World", 100, 100);
		            
		            // Draw text
		            int x = getWidth() / 2;
		            int y = 20;
		            
		            drawTextWithBackground(g2, textMain, x, y);
		            
		            if (AoEHelperGUI.rdbtnShowDebugText.isSelected()) {
		            	drawTextWithBackground(g2, textPop, x, y);
			            drawTextWithBackground(g2, textVillagers, x-30, y);
			            drawTextWithBackground(g2, textCivilization, x-150, y);
			            drawTextWithBackground(g2, textAge, x-150, y+20);
			            drawTextWithBackground(g2, textExplored, x-150, y+40);
			            
			            drawTextWithBackground(g2, textFood, x-220, y);
			            drawTextWithBackground(g2, textWood, x-220, y+20);
			            drawTextWithBackground(g2, textGold, x-220, y+40);
			            drawTextWithBackground(g2, textStone, x-220, y+60);
			            
			            drawTextWithBackground(g2, textPoints, x-360, y);
		            }
		            
		            if (AoEHelperGUI.rdbtnShowBuildOrder.isSelected()) {
		            	drawTextWithBackground(g2, textBO, x, y + 20);
		            }
		            
		            // Draw image of house if almost housed
		            if (AoEHelperGUI.rdbtnShowHouseImage.isSelected() && houseNeeded)
		            	g2.drawImage(imageHouse, 405, 0, 100, 100, this);
		            //repaint();
	        	}
	        	else if (AoEHelper.showGenerationOverlay) {
	        		// This part is only executed during hash generation to show the current image
	        		int x = getWidth() / 2;
		            int y = 20;
		            
	        		drawTextWithBackground(g2, textPop, x, y);
	        	}
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
	
	private void ResetVariables() {
		textPop = "";
		textCivilization = "";
		textAge = "";
		textBO = "";
		textPoints = "";
		textExplored = "";
		
		textVillagers = "";
		textFood = "";
		textWood = "";
		textGold = "";
		textStone = "";
		
		houseNeeded = false;
	}
	
	private void InitCivilizationNames() {
		// TODO: Update civs
		civilizationNames = new String[] {
			"Aztecs", "Berbers", "Britons", "Bulgarians", "Burmese", "Byzantines", "Celts", "Chinese",
			"Cumans", "Ethiopians", "Franks", "Goths", "Huns", "Incas", "Indians", "Italians",
			"Japanese", "Khmer", "Koreans", "Lithuanians", "Magyars", "Malay", "Malians", "Mayans",
			"Mongols", "Persians", "Portuguese", "Saracens", "Slavs", "Spanish", "Tatars", "Teutons",
			"Turks", "Vietnamese", "Vikings"
		};
	}
	
	private void InitAgeNames() {
		ageNames = new String[] {
			"Dark", "Feudal", "Castle", "Imperial"
		};
	}
	
	public void loadBuildOrderText(String name) {
		JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("data/text/" + name + ".json")) {
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
	}
	
	private static void parseJSONObject(JSONObject json) {
		// Get triggers object within list
		JSONObject triggers = (JSONObject) json.get("triggers");
		JSONObject population = (JSONObject) triggers.get("population");
		population_darkAge = (JSONObject) population.get("dark_age");
		population_feudalAge = (JSONObject) population.get("feudal_age");
		population_castleAge = (JSONObject) population.get("castle_age");
		population_imperialAge = (JSONObject) population.get("imperial_age");
		
		JSONObject timer = (JSONObject) triggers.get("timer");
		timer_darkAge = (JSONObject) timer.get("dark_age");
		timer_darkAgeAdvancing = (JSONObject) timer.get("dark_age_advancing");
		timer_feudalAge = (JSONObject) timer.get("feudal_age");
		timer_castleAge = (JSONObject) timer.get("castle_age");
		timer_imperialAge = (JSONObject) timer.get("imperial_age");
		
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
		if (!text.isEmpty()) {
			// Draw text background
			Rectangle bounds = getStringBounds(g2, text, x, y);
	        extendRectangle(bounds, 3, 3);
	        
	        g2.setColor(Color.WHITE);
	        g2.fill(bounds);
	        
	        // TODO: Fix flickering, text causes flickering, maybe just draw if text really changed (use textPrevious)
	        g2.setColor(Color.BLACK);
	        g2.drawString(text, x, y);
		}
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
	public void analyzeVillagersText(String text) {
		// Add build order text
		int villagers = Integer.parseInt(text);
		
		// Set main text
		if (villagers == -1) {
			text = "";
			textMain = "Not ingame";
			ingame = false;
			ResetVariables();
		}
		else {
			textMain = "";
			ingame = true;
			
			if (lastRecognizedAge != null) {
				JSONObject jsonObject = null;
				switch(lastRecognizedAge) {
					case Dark: jsonObject = population_darkAge; break;
					case Feudal: jsonObject = population_feudalAge; break;
					case Castle: jsonObject = population_castleAge; break;
					case Imperial: jsonObject = population_imperialAge; break;
				}
				// TODO: Code this better, only execute this if needed.
				try {
					textBO = getTextFromJSONObject(jsonObject, "" + villagers);
				}
				catch (Exception e) {}
			}
		}
		
		// Show text
		textVillagers = text;
	}
	
	public void analyzePopText(String text) {
		String[] textSplit = text.split("/");
		
		// Reset variables
		houseNeeded = false;
		
		// Verify that split was successful
		try {
			// Get pop and max pop
			int pop = Integer.parseInt(textSplit[0].trim());
			int pop_max = Integer.parseInt(textSplit[1].trim());
			
			// Add house warning
			if (pop + 2 >= pop_max) {
				houseNeeded = true;
			}
		}
		catch (Exception e) {
			// Exception might happen either if / was not found or parsing was not successful
			//System.err.println(e.getMessage());
			text = "";
		}
		
		// Show text
		textPop = text;
	}
	
	public void analyzeCivilization(String text) {
		int i = Integer.parseInt(text);
		
		if (i == -1) {
			text = "";
		} else {
			text = civilizationNames[i];
			lastRecognizedCiv = text;
		}
		
		// Show text
		textCivilization = text;
	}
	
	public void analyzeAge(String text, boolean ageAdvancing) {
		int i = Integer.parseInt(text);
		
		if (i == -1) {
			text = "";
		} else {
			String textAdvancing = "";
			if (ageAdvancing) textAdvancing = " (Advancing)";
			
			text = ageNames[i] + " Age" + textAdvancing;
			lastRecognizedAge = Age.values()[i];
		}
		
		// Show text
		textAge = text;
	}
	
	public void analyzeFoodText(String text) {
		int i = Integer.parseInt(text);
		
		if (i == -1) {
			text = "";
		} else {
			text = "Food: " + text;
		}
		
		// Show text
		textFood = text;
	}
	
	public void analyzeWoodText(String text) {
		int i = Integer.parseInt(text);
		
		if (i == -1) {
			text = "";
		} else {
			text = "Wood: " + text;
		}
		
		// Show text
		textWood = text;
	}
	
	public void analyzeGoldText(String text) {
		int i = Integer.parseInt(text);
		
		if (i == -1) {
			text = "";
		} else {
			text = "Gold: " + text;
		}
		
		// Show text
		textGold = text;
	}
	
	public void analyzeStoneText(String text) {
		int i = Integer.parseInt(text);
		
		if (i == -1) {
			text = "";
		} else {
			text = "Stone: " + text;
		}
		
		// Show text
		textStone = text;
	}
	
	public void analyzePoints(String text) {
		String[] textSplit = text.split("/");
		
		// Verify that split was successful
		try {
			// Get points and team points
			int points = Integer.parseInt(textSplit[0].trim());
			int points_team = Integer.parseInt(textSplit[1].trim());
			
			// Add age up warning
			String advancing = "";
			if (points_previous != -1) {
				int point_difference = points - points_previous;
				
				//if (age[current_civ] != imperial && point_difference <= -170)
				if (point_difference <= -170) {
					advancing = " (Advancing Imperial)";
				}
				else if (point_difference <= -90) {
					advancing = " (Advancing Castle)";
				}
				else if (point_difference <= -40) {
					advancing = " (Advancing Feudal)";
				}
			}
			points_previous = points;
			
			text = "Points: " + text + advancing;
		}
		catch (Exception e) {
			// Exception might happen either if / was not found or parsing was not successful
			//System.err.println(e.getMessage());
			text = "";
		}
		
		// Show text
		textPoints = text;
	}
	
	public void analyzeMap(BufferedImage image) {
		// Analyze map, find percentage how much was explored
		// Store map into variable (used for new map hotkeys like going to the next gold etc.)
		imageMap = image;
		
		Color currentColor, currentMaskColor;
		int w = image.getWidth();
		int h = image.getHeight();
		
		// To see the amount of pixels of the map, open map_mask in Photoshop Window -> Histogram.
		int max_pixels = 32761;
		int pixels_unexplored = 0;
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				// Only check pixels where mask is white
				currentMaskColor = new Color(imageMapMask.getRGB(x, y));
				
				if (currentMaskColor.equals(Color.white)) {
					
					// Check if pixel is black (= unexplored)
					currentColor = new Color(image.getRGB(x, y));
					
					if (currentColor.equals(Color.black)) {
						pixels_unexplored++;
					}
				}
			}
		}
		
		// Calculate how much percent the player explored the map
		int pixels_explored = max_pixels - pixels_unexplored;
		
		//int percentage = (int) (((float) pixels_explored / max_pixels) * 100);
		float percentage = ((float) pixels_explored / max_pixels) * 100;
		float roundedPercentage = Math.round(percentage * 100f) / 100f;
		
		// Show text
		textExplored = "Explored: " + roundedPercentage + "%";
	}
	
	/**
	 * Default text panel is textMain. This method is also used to display debug information during hash generation.
	 */
	public void SetTextToDisplay(String text) {
		textMain = text;
	}
	
	/**
	 * Method to repaint the GUI/frame
	 */
	public void UpdateGUI() {
		paintComponent.repaint();
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
	public void InitJNativeHook(NativeKeyListener listener) {
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

		GlobalScreen.addNativeKeyListener(listener);
	}
	
	/**
	 * This method simulates any key press for example "KeyEvent.VK_A".
	 * Also simulate a key release, else AoE won't start the next key press.
	 */
	public void SimulateKeyPress(int ev) {
		 robot.keyPress(ev);
		 robot.keyRelease(ev);
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