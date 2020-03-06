package helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

/**
 * From: https://stackoverflow.com/questions/11217660/java-making-a-window-click-through-including-text-images
 */
public class Overlay implements NativeKeyListener {
	
	private String textToDisplay;
	private JComponent paintComponent;
	
	public Overlay() {
		textToDisplay = "Age of Empires";
		InitJNativeHook();
		Window w = new Window(null);
	    
	    /**
	     * This sets the background of the window to be transparent.
	     */
	    //AWTUtilities.setWindowOpaque(w, false);
        w.setBackground(new Color(0, 0, 0, 0)); 
        
        paintComponent = (JComponent) w.add(new JComponent() {
	        /**
	         * This will draw a black cross on screen.
	         */
	        protected void paintComponent(Graphics g) {
	        	//super.paintComponent(g);
	            g.setColor(Color.BLACK);
	            //g.fillRect(0, getHeight() / 2 - 10, getWidth(), 20);
	            //g.fillRect(getWidth() / 2 - 10, 0, 20, getHeight());
	            //g.drawString("Hello World", 100, 100);
	            
	            g.drawString(textToDisplay, getWidth() / 2, getHeight() / 2);
	            //repaint();
	        }

	        public Dimension getPreferredSize() {
	        	return new Dimension(300, 100);
	            //return Toolkit.getDefaultToolkit().getScreenSize();
	        }
	    });
        
	    w.pack();
	    w.setLocationRelativeTo(null);
	    w.setVisible(true);
	    w.setAlwaysOnTop(true);
	    
	    setTransparent(w);
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
		textToDisplay = analyseText(text);
		paintComponent.repaint();
	}
	
	private String analyseText(String text) {
		String[] textSplit = text.split("/");
		
		// Verify that split was successful
		try {
			// Get pop and max pop
			int pop = Integer.parseInt(textSplit[0].trim());
			int pop_max = Integer.parseInt(textSplit[1].trim());
			
			// Add house warning
			if (pop + 2 >= pop_max) {
				text += " (Make house!)";
			}
		} catch (Exception e) {
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