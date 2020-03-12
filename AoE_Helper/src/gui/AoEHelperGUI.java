package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.ini4j.Wini;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import helper.AoEHelper;
import helper.Overlay;
 
/**
 * From: https://www.java-tutorial.org/actionlistener.html
 */
public class AoEHelperGUI extends JFrame implements ActionListener, ItemListener {
	
	private static final long serialVersionUID = -1201330636287721585L;
    private JButton buttonActive, buttonHide, buttonQuit, buttonSettings;
    private JTextArea textArea;
    private JComboBox<String> listBO;
    public static JRadioButton rdbtnShowDebugText, rdbtnShowBuildOrder, rdbtnShowHouseImage;
    
    private Wini ini;
    private SettingsGUI settingsGUI;
    private Overlay overlay;
    
    public static boolean active = true;
 
    public AoEHelperGUI(Overlay overlay) {
    	// Initialize references
    	this.overlay = overlay;
    	this.overlay.gui = this;
    	
    	// Initialize INI-reader
    	String iniPath = "data/settings.ini";
    	
    	try {
			ini = new Wini(new File(iniPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	// Clear ini file if version is different than current version
    	float version = ini.get("general", "version", float.class);
    	if (version != AoEHelper.version) {
			try {
				PrintWriter pw = new PrintWriter(iniPath);
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	
    	// Initialize child GUI
    	settingsGUI = new SettingsGUI(this);
    	
    	// Initialize GUI
        setTitle("AoE Helper");
        setAlwaysOnTop(true);
        setFocusableWindowState(false);
        setResizable(false);
        overrideCloseMethod();
        
        // Set icon
        ImageIcon img = new ImageIcon("data/images/icon.png");
        setIconImage(img.getImage());
        
        // Set look and feel
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
        
        // Load build orders
        String[] stringsBO = loadBuildOrders();
        
        // The following code was created with WindowBuilder
        JPanel contentPane = new JPanel();
        
		setBounds(100, 100, 413, 246);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		buttonActive = new JButton("Stop");
		buttonActive.setBounds(137, 11, 175, 57);
		contentPane.add(buttonActive);
		
		buttonHide = new JButton("Hide");
		buttonHide.setBounds(324, 45, 72, 23);
		contentPane.add(buttonHide);
		
		buttonQuit = new JButton("Quit");
		buttonQuit.setBounds(324, 11, 72, 23);
		contentPane.add(buttonQuit);
		
		JLabel lblBuildOrder = new JLabel("Build order:");
		lblBuildOrder.setBounds(10, 82, 66, 23);
		contentPane.add(lblBuildOrder);
		
		listBO = new JComboBox<String>(stringsBO);
		listBO.setBounds(81, 83, 202, 20);
		contentPane.add(listBO);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 117, 386, 90);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		rdbtnShowDebugText = new JRadioButton("Show debug text");
		rdbtnShowDebugText.setBounds(10, 11, 115, 23);
		contentPane.add(rdbtnShowDebugText);
		
		rdbtnShowBuildOrder = new JRadioButton("Show build order");
		rdbtnShowBuildOrder.setSelected(true);
		rdbtnShowBuildOrder.setBounds(291, 82, 105, 23);
		contentPane.add(rdbtnShowBuildOrder);
		
		rdbtnShowHouseImage = new JRadioButton("Show house image");
		rdbtnShowHouseImage.setSelected(true);
		rdbtnShowHouseImage.setBounds(10, 38, 115, 23);
		contentPane.add(rdbtnShowHouseImage);
		
		// Assign listeners
        listBO.addItemListener(this);
        
        buttonActive.addActionListener(this);
        buttonHide.addActionListener(this);
        buttonQuit.addActionListener(this);
        
        // Load settings and set GUI to accordingly
        int lastSelectedBO = ini.get("gui", "lastSelectedBO", int.class);
        listBO.setSelectedIndex(lastSelectedBO);
        loadBuilderOrderText(listBO.getItemAt(lastSelectedBO));
        
        boolean showDebugText = ini.get("gui", "showDebugText", boolean.class);
        rdbtnShowDebugText.setSelected(showDebugText);
        boolean showBuildOrder = ini.get("gui", "showBuildOrder", boolean.class);
        rdbtnShowBuildOrder.setSelected(showBuildOrder);
        boolean showHouseImage = ini.get("gui", "showHouseImage", boolean.class);
        rdbtnShowHouseImage.setSelected(showHouseImage);
    }
    
    private String[] loadBuildOrders() {
    	List<String> fileNames = new ArrayList<>();
    	
    	// List all files from directory
    	// From: https://mkyong.com/java/java-how-to-list-all-files-in-a-directory/
    	try (Stream<Path> walk = Files.walk(Paths.get("data/text"))) {

    		fileNames = walk.filter(Files::isRegularFile)
    				.map(x -> x.toString()).collect(Collectors.toList());
    		
    		//result.forEach(System.out::println);
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	// Initialize string array
    	int fileNamesSize = fileNames.size();
    	String[] titles = new String[fileNamesSize];
    	
    	// Load title
    	for (int i = 0; i < fileNamesSize; i++) {
    		JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(fileNames.get(i))) {
            	//Read JSON file
            	Object obj = jsonParser.parse(reader);
            	
            	JSONArray list = (JSONArray) obj;
            	
            	//Get json object within list
            	String title = "No title";
            	
            	for (int j = 0; j < list.size(); j++) {
            		JSONObject json = (JSONObject) list.get(j);
            		title = (String) json.get("title");
    			}
            	// Set title
            	titles[i] = title;
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
		}
    	
		return titles;
    }
 
    public void actionPerformed (ActionEvent ae) {
    	if(ae.getSource() == buttonActive) {
    		active = active ? false : true;
        	if (active) {
        		buttonActive.setText("Stop");
        	}
        	else {
        		buttonActive.setText("Start");
        	}
    	}
    	/*else if(ae.getSource() == buttonSettings) {
        	settingsGUI.setVisible(true);
        }*/
        else if(ae.getSource() == buttonHide) {
        	setState(JFrame.ICONIFIED);
        }
        else if (ae.getSource() == buttonQuit) {
        	close();
        }
    }
    
    @Override
	public void itemStateChanged(ItemEvent e) {
    	if (e.getStateChange() == ItemEvent.SELECTED) {
            String item = (String) e.getItem();
            loadBuilderOrderText(item);
         }
	}
    
    public void loadBuilderOrderText(String item) {
    	if (item.equals("Archers")) {
        	overlay.loadBuildOrderText("bo_archers");
        }
        else if (item.equals("Fast Castle - Boom")) {
        	overlay.loadBuildOrderText("bo_fastcastle-boom");
		}
    }
    
    /* ===================================================================== */
    
    private void overrideCloseMethod() {
    	this.addWindowListener(new java.awt.event.WindowAdapter() {
    	    @Override
    	    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
    	    	saveSettings();
    	    	close();
    	    }
    	});
    }
    
    public void saveSettings() {
    	ini.put("general", "version", AoEHelper.version);
		ini.put("gui", "lastSelectedBO", listBO.getSelectedIndex());
		ini.put("gui", "showDebugText", rdbtnShowDebugText.isSelected());
		ini.put("gui", "showBuildOrder", rdbtnShowBuildOrder.isSelected());
		ini.put("gui", "showHouseImage", rdbtnShowHouseImage.isSelected());
		
		try {
			ini.store();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void close() {
    	AoEHelper.quitApplication = true;
    }
}