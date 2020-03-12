package test;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;

public class GUIDesign extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIDesign frame = new GUIDesign();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUIDesign() {
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 413, 246);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnStop = new JButton("Stop");
		btnStop.setBounds(137, 11, 175, 57);
		contentPane.add(btnStop);
		
		JButton btnHide = new JButton("Hide");
		btnHide.setBounds(324, 45, 72, 23);
		contentPane.add(btnHide);
		
		JButton btnQuit = new JButton("Exit");
		btnQuit.setBounds(324, 11, 72, 23);
		contentPane.add(btnQuit);
		
		JLabel lblBuildOrder = new JLabel("Build order:");
		lblBuildOrder.setBounds(10, 82, 66, 23);
		contentPane.add(lblBuildOrder);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(81, 83, 202, 20);
		contentPane.add(comboBox);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 117, 386, 90);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		JRadioButton rdbtnShowDebugText = new JRadioButton("Show debug text");
		rdbtnShowDebugText.setBounds(10, 11, 115, 23);
		contentPane.add(rdbtnShowDebugText);
		
		JRadioButton rdbtnShowBuildOrder = new JRadioButton("Show build order");
		rdbtnShowBuildOrder.setSelected(true);
		rdbtnShowBuildOrder.setBounds(291, 82, 105, 23);
		contentPane.add(rdbtnShowBuildOrder);
		
		JRadioButton rdbtnShowHouseImage = new JRadioButton("Show house image");
		rdbtnShowHouseImage.setSelected(true);
		rdbtnShowHouseImage.setBounds(10, 38, 115, 23);
		contentPane.add(rdbtnShowHouseImage);
	}
}
