package org.example.server.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.GroupLayout.*;
import javax.swing.LayoutStyle.*;

public class CardLayoutDemo extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JPanel padMain, pad1, pad2, pad2_1, pad2_2;
	private JButton bt1, bt2;
	private JTextField tf1, tf2;
	
	private CardLayout cl = new CardLayout();		// FOR associating later (!important) 
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CardLayoutDemo frame = new CardLayoutDemo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public CardLayoutDemo() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		setTitle("CardLayoutDemo");
		
		padMain = new JPanel(new GridLayout(2,1));
		add(padMain);
		
		pad1 = new JPanel();
		
		bt1 = new JButton("show card 1");
		pad1.add(bt1);
		bt2 = new JButton("show card 2");
		pad1.add(bt2);
		
		
		pad2 = new JPanel(cl);		// set pad2 JPanel to THE PARENT of CardLayout
		
		pad2_1 = new JPanel();
		tf1 = new JTextField("this is card 1", 20);
		tf1.setEditable(false);
		pad2_1.add(tf1);
		
		pad2_2 = new JPanel();
		tf2 = new JTextField("this is card 2", 20);
		tf2.setEditable(false);
		pad2_2.add(tf2);
		
		pad2.add(pad2_1, "1");		// add card "pad2_1" named "1"
		pad2.add(pad2_2, "2");		// add card "pad2_2" named "2"
		
		
		padMain.add(pad1);
		padMain.add(pad2);
		
		
		MyActionListener ActionListenerList = new MyActionListener();
		bt1.addActionListener(ActionListenerList);
		bt2.addActionListener(ActionListenerList);
	}
	
	
	private class MyActionListener implements ActionListener {
		@Override public void actionPerformed(ActionEvent evt) {
			if (evt.getSource() == bt1) {
				cl.show(pad2, "1");		// show card named "1" , from parent pad2
			} else if (evt.getSource() == bt2) {
				cl.show(pad2, "2");		// show card named "2" , from parent pad2
			}
		}
	}
	
}

