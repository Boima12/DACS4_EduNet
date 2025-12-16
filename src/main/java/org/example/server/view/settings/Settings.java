package org.example.server.view.settings;

import java.awt.Color;
import java.awt.Font;

import javax.swing.*;

public class Settings {
	
	private JFrame frame;
	
	/**
	 * Create the application.
	 */
	public Settings() {
		initialize();	// line này dùng để bật WindowBuilder, nếu comment line này sẽ tối ưu ứng dụng nhưng không thể sài windowBuilder trong file này
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1111, 730);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(build());
	}
	
	public static JPanel build() {
		JPanel settings = new JPanel();
		settings.setLayout(null);
		settings.setBackground(new Color(251, 251, 251));
		settings.setBounds(75, 0, 1111, 730);
		
		JLabel lbl_settingsText = new JLabel("placeholder text for Settings");
		lbl_settingsText.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_settingsText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_settingsText.setBounds(300, 304, 500, 50);
		settings.add(lbl_settingsText);
		
		return settings;
	}
}
