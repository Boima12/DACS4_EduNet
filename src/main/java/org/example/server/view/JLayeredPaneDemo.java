package org.example.server.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.GroupLayout.*;
import javax.swing.LayoutStyle.*;

public class JLayeredPaneDemo extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JPanel padMain, pad1, pad2, pad3;
	private JLayeredPane layer1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JLayeredPaneDemo frame = new JLayeredPaneDemo();
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
	public JLayeredPaneDemo() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel(null);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		setTitle("JLayeredPaneDemo");
		
		padMain = new JPanel(null);
		padMain.setBounds(0, 0, 300, 200);
		layer1 = new JLayeredPane();
		layer1.setBounds(0, 0, 300, 200);
		
		pad1 = new JPanel();
		pad1.setBackground(new Color(255, 255, 255));   // white
		pad1.setBounds(0, 0, 50, 50);
		//pad1.setOpaque(true);
		layer1.add(pad1, 12);

		pad2 = new JPanel();
		pad2.setBackground(new Color(200, 200, 200));   // gray
		pad2.setBounds(20, 20, 50, 50);
		layer1.add(pad2, 13);

		pad3 = new JPanel();
		pad3.setBackground(new Color(35, 151, 192));    // blue
		pad3.setBounds(40, 40, 50, 50);
		layer1.add(pad3, 11);	// LAYERER ORDER MATTER, you need to add the layout order from lower to higher first!, in this case pad3 won't show up above pad1!
		
		padMain.add(layer1);
		contentPane.add(padMain);
		
		JButton bt_layer1 = new JButton("layer 1");
		bt_layer1.setBounds(24, 232, 85, 21);
		bt_layer1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchViewLayer(1);
			}
		});
		contentPane.add(bt_layer1);
		
		JButton bt_layer2 = new JButton("layer 2");
		bt_layer2.setBounds(154, 232, 85, 21);
		bt_layer2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchViewLayer(2);
			}
		});
		contentPane.add(bt_layer2);
		
		JButton bt_layer3 = new JButton("layer 3");
		bt_layer3.setBounds(273, 232, 85, 21);
		bt_layer3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchViewLayer(3);
			}
		});
		contentPane.add(bt_layer3);
	}
	
	private void switchViewLayer(Integer layerNum) {
		
		// reset all other layer to their default value?
		// layer 1 to order 11
		// layer 2 to order 12
		// layer 3 to order 13
		
		switch (layerNum) {
			case 1 : {
				// TODO set layer 1 to order 1 so it appear on top?
				break;
			}
			case 2 : {
                // TODO set layer 2 to order 1 so it appear on top?
				break;
			}
			case 3 : {
                // TODO set layer 3 to order 1 so it appear on top?
				break;
			}
			default : {
				System.out.println("non existed view layerNum, check your code!");
				break;
			}
		}
	}
	
}
