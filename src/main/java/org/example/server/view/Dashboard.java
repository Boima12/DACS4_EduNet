package org.example.server.view;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.example.common.utils.gui.ImageHelper;
import org.example.common.utils.gui.RoundedBorder;
import org.example.common.utils.gui.WrapLayout;
import org.example.server.view.dashboard.LienKetModal;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import java.awt.FlowLayout;

// added imports
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Dashboard {

	private String currentSelectedComputer;
    private String ServerLocalAddress;
	
	private JFrame frame;
	private JLabel lbl_info_icon;
	private JLabel lbl_info_name;

	// track the currently selected client panel
	private JPanel selectedClientItem;

	/**
	 * Create the application.
	 */
	public Dashboard(String ServerLocalAddress) {
        this.ServerLocalAddress = ServerLocalAddress;
		initialize();
	}
	
	public void display() {
		frame.setVisible(true);
	}

	public void undisplay() {
		frame.setVisible(false);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(240, 240, 240));
		frame.getContentPane().setLayout(null);
		frame.setBounds(100, 100, 1200, 767);
        frame.setTitle("Dashboard - " + ServerLocalAddress);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel sidebar = new JPanel();
		sidebar.setBackground(new Color(38, 139, 255));
		sidebar.setBounds(0, 0, 75, 730);
		sidebar.setLayout(null);
		frame.getContentPane().add(sidebar);
		
		JButton btn_power = new JButton("");
		btn_power.setSize(new Dimension(30, 30));
		btn_power.setBorderPainted(false);
		btn_power.setBackground(new Color(59, 238, 101));
		btn_power.setBounds(0, 0, 75, 75);
		btn_power.setIcon(ImageHelper.getScaledIcon("/images/power_white.png", 30, 30));
		sidebar.add(btn_power);
		
		JButton btn_dashboard = new JButton("");
		btn_dashboard.setSize(new Dimension(30, 30));
		btn_dashboard.setBorderPainted(false);
		btn_dashboard.setBackground(new Color(36, 128, 234));
		btn_dashboard.setBounds(0, 75, 75, 75);
		btn_dashboard.setIcon(ImageHelper.getScaledIcon("/images/dashboard_white.png", 45, 45));
		sidebar.add(btn_dashboard);
		
		JButton btn_about = new JButton("");
		btn_about.setSize(new Dimension(30, 30));
		btn_about.setBorderPainted(false);
		btn_about.setBackground(new Color(38, 139, 255));
		btn_about.setBounds(0, 655, 75, 75);
		btn_about.setIcon(ImageHelper.getScaledIcon("/images/about_white.png", 27, 27));
		sidebar.add(btn_about);
		
		JPanel infobar = new JPanel();
		infobar.setBackground(new Color(251, 251, 251));
		infobar.setBounds(936, 0, 250, 730);
		infobar.setLayout(null);
		frame.getContentPane().add(infobar);
		
		lbl_info_icon = new JLabel("");
		lbl_info_icon.setBackground(new Color(240, 240, 240));
		lbl_info_icon.setBounds(50, 75, 150, 150);
		infobar.add(lbl_info_icon);
		
		lbl_info_name = new JLabel("Computer name");
		lbl_info_name.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_info_name.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_info_name.setBounds(10, 250, 230, 30);
		infobar.add(lbl_info_name);
		
		JButton btn_info_placeholder1 = new JButton("Button 1");
		btn_info_placeholder1.setBorder(new RoundedBorder(8));
		btn_info_placeholder1.setForeground(new Color(0, 0, 0));
		btn_info_placeholder1.setBackground(new Color(251, 251, 251));
		btn_info_placeholder1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_info_placeholder1.setBounds(50, 350, 150, 35);
		btn_info_placeholder1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		infobar.add(btn_info_placeholder1);
		
		JButton btn_info_placeholder2 = new JButton("Button 2");
		btn_info_placeholder2.setBorder(new RoundedBorder(8));
		btn_info_placeholder2.setForeground(new Color(0, 0, 0));
		btn_info_placeholder2.setBackground(new Color(251, 251, 251));
		btn_info_placeholder2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_info_placeholder2.setBounds(50, 413, 150, 35);
		btn_info_placeholder2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		infobar.add(btn_info_placeholder2);
		
		JButton btn_info_placeholder3 = new JButton("Button 3");
		btn_info_placeholder3.setBorder(new RoundedBorder(8));
		btn_info_placeholder3.setForeground(new Color(0, 0, 0));
		btn_info_placeholder3.setBackground(new Color(251, 251, 251));
		btn_info_placeholder3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_info_placeholder3.setBounds(50, 476, 150, 35);
		btn_info_placeholder3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		infobar.add(btn_info_placeholder3);
		
		JButton btn_info_placeholder4 = new JButton("Button 4");
		btn_info_placeholder4.setBorder(new RoundedBorder(8));
		btn_info_placeholder4.setForeground(new Color(0, 0, 0));
		btn_info_placeholder4.setBackground(new Color(251, 251, 251));
		btn_info_placeholder4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_info_placeholder4.setBounds(50, 539, 150, 35);
		btn_info_placeholder4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		infobar.add(btn_info_placeholder4);
		
		JPanel dashboard_options = new JPanel();
		dashboard_options.setBackground(new Color(251, 251, 251));
		dashboard_options.setBounds(75, 0, 862, 60);
		dashboard_options.setLayout(null);
		frame.getContentPane().add(dashboard_options);
		
		JButton btn_do_lkClient = new JButton("Liên kết Client");
		btn_do_lkClient.setForeground(Color.BLACK);
		btn_do_lkClient.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_do_lkClient.setBorder(new RoundedBorder(8));
		btn_do_lkClient.setBackground(new Color(251, 251, 251));
		btn_do_lkClient.setBounds(15, 10, 150, 35);
		btn_do_lkClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onlkClient();
			}
		});
		dashboard_options.add(btn_do_lkClient);

		JButton btn_do_lanScan = new JButton("Quét mạng tìm Client");
		btn_do_lanScan.setForeground(Color.BLACK);
		btn_do_lanScan.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btn_do_lanScan.setBorder(new RoundedBorder(8));
		btn_do_lanScan.setBackground(new Color(251, 251, 251));
		btn_do_lanScan.setBounds(185, 10, 150, 35);
		btn_do_lanScan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		dashboard_options.add(btn_do_lanScan);
		
		JButton btn_do_placeholder3 = new JButton("Button 3");
		btn_do_placeholder3.setForeground(Color.BLACK);
		btn_do_placeholder3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_do_placeholder3.setBorder(new RoundedBorder(8));
		btn_do_placeholder3.setBackground(new Color(251, 251, 251));
		btn_do_placeholder3.setBounds(355, 10, 150, 35);
		btn_do_placeholder3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		dashboard_options.add(btn_do_placeholder3);
		
		JButton btn_do_placeholder4 = new JButton("Button 4");
		btn_do_placeholder4.setForeground(Color.BLACK);
		btn_do_placeholder4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_do_placeholder4.setBorder(new RoundedBorder(8));
		btn_do_placeholder4.setBackground(new Color(251, 251, 251));
		btn_do_placeholder4.setBounds(525, 10, 150, 35);
		btn_do_placeholder4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		dashboard_options.add(btn_do_placeholder4);
		
		JButton btn_do_placeholder5 = new JButton("Button 5");
		btn_do_placeholder5.setForeground(Color.BLACK);
		btn_do_placeholder5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_do_placeholder5.setBorder(new RoundedBorder(8));
		btn_do_placeholder5.setBackground(new Color(251, 251, 251));
		btn_do_placeholder5.setBounds(695, 10, 150, 35);
		btn_do_placeholder5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		dashboard_options.add(btn_do_placeholder5);
		
		JTextArea ta_log = new JTextArea();
		ta_log.setBackground(new Color(251, 251, 251));
		ta_log.setLineWrap(true);
		ta_log.setEditable(false);
		ta_log.setBounds(75, 495, 862, 235);
		frame.getContentPane().add(ta_log);
		
		JPanel client_dashboard = new JPanel();
		client_dashboard.setBounds(75, 59, 862, 435);
		client_dashboard.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
		JScrollPane scrollPane = new JScrollPane(client_dashboard);
		scrollPane.setBounds(75, 59, 862, 435);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		frame.getContentPane().add(scrollPane);
		
		client_dashboard.add(createClientItem("PC-01", true));
		client_dashboard.add(createClientItem("PC-02", true));
		client_dashboard.add(createClientItem("PC-03", false));
		client_dashboard.add(createClientItem("PC-04", true));
		client_dashboard.add(createClientItem("PC-05", false));
	}
	
	private JPanel createClientItem(String name, boolean isConnected) {
	    JPanel client_item = new JPanel();
	    client_item.setPreferredSize(new Dimension(125, 135));
	    client_item.setLayout(null);
	    // default (unselected) background
	    client_item.setBackground(new Color(251, 251, 251));

	    JLabel lbl_item_icon = new JLabel();
	    lbl_item_icon.setBounds(25, 10, 75, 75);
	    if (isConnected) {
	    	lbl_item_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop.png", 75, 75));	    	
	    } else {
	    	lbl_item_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop_off.png", 75, 75));
	    }
	    
	    client_item.add(lbl_item_icon);

	    JLabel lbl_item_name = new JLabel(name, SwingConstants.CENTER);
	    lbl_item_name.setFont(new Font("Tahoma", Font.PLAIN, 15));
	    lbl_item_name.setBounds(10, 95, 105, 20);
	    client_item.add(lbl_item_name);

	    // selection colors
	    final Color SELECTED_BG = new Color(209, 237, 248);
	    final Color UNSELECTED_BG = new Color(251, 251, 251);

	    // toggle selection on click: select this item (and deselect previous) or deselect if already selected
	    client_item.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	    		if (selectedClientItem == client_item) {
	    			// already selected
	    			client_item.setBackground(UNSELECTED_BG);
	    			selectedClientItem = null;
	    			
	    			lbl_info_icon.setIcon(null);
	    			lbl_info_name.setText("");
	    			currentSelectedComputer = null;	// currently this is extra
	    		} else {
	    			// deselect previous if any
	    			if (selectedClientItem != null) {
	    				selectedClientItem.setBackground(UNSELECTED_BG);
//	    				System.out.println("deselected: " + selectedClientItem.getName());
	    			}
	    			// select this one
	    			client_item.setBackground(SELECTED_BG);
	    			selectedClientItem = client_item;
	    			
	    		    if (isConnected) {
	    		    	lbl_info_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop.png", 150, 150));	    	
	    		    } else {
	    		    	lbl_info_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop_off.png", 150, 150));
	    		    }
	    		    
	    		    lbl_info_name.setText(name);
	    			currentSelectedComputer = name; // currently this is extra
	    		}
//	    		System.out.println("currentSelectedComputer = " + currentSelectedComputer);
	    	}
	    });

	    return client_item;
	}

	public void onlkClient() {
        UIState.lkModal = new LienKetModal(frame);
        UIState.lkModal.setVisible(true);
    }
}
