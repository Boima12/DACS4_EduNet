package org.example.server.view;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.example.common.utils.gui.Alert;
import org.example.common.utils.gui.ImageHelper;
import org.example.server.ServerStates;
import org.example.server.view.about.AboutModal;
import org.example.server.view.dashboard.Dashboard;
import org.example.server.view.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;


@SuppressWarnings({"FieldMayBeFinal", "PatternVariableCanBeUsed"})
public class ServerUI {

	private static final Logger log = LoggerFactory.getLogger(ServerUI.class);
	
    private String ServerLocalAddress;
    private boolean isServerStarted = false;
	
	private JFrame frame;
	private CardLayout cardLayout = new CardLayout();
	private JPanel viewMain;
	private JButton btn_power;
	private JButton btn_dashboard;
	private JButton btn_layerHolder;

	/**
	 * Create the application.
	 */
	public ServerUI(String ServerLocalAddress) {
        this.ServerLocalAddress = ServerLocalAddress;
		initialize();
        onClosingEvent();

		switchViewLayer(1);
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
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JPanel sidebar = new JPanel();
		sidebar.setBackground(new Color(38, 139, 255));
		sidebar.setBounds(0, 0, 75, 730);
		sidebar.setLayout(null);
		frame.getContentPane().add(sidebar);
		
		btn_power = new JButton("");
		btn_power.setSize(new Dimension(30, 30));
		btn_power.setBorderPainted(false);
		btn_power.setBackground(new Color(238, 59, 62));
		btn_power.setBounds(0, 0, 75, 75);
		btn_power.setIcon(ImageHelper.getScaledIcon("/images/power_white.png", 30, 30));
		btn_power.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                try {
                    toggleServer();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
		});
		sidebar.add(btn_power);
		
		btn_dashboard = new JButton("");
		btn_dashboard.setSize(new Dimension(30, 30));
		btn_dashboard.setBorderPainted(false);
		btn_dashboard.setBackground(new Color(36, 128, 234));
		btn_dashboard.setBounds(0, 75, 75, 75);
		btn_dashboard.setIcon(ImageHelper.getScaledIcon("/images/dashboard_white.png", 45, 45));
		btn_dashboard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isServerStarted) switchViewLayer(2);					
			}
		});
		sidebar.add(btn_dashboard);
		
		btn_layerHolder = new JButton("");
		btn_layerHolder.setSize(new Dimension(30, 30));
		btn_layerHolder.setBorderPainted(false);
		btn_layerHolder.setBackground(new Color(38, 139, 255));
		btn_layerHolder.setBounds(0, 150, 75, 75);
		btn_layerHolder.setIcon(ImageHelper.getScaledIcon("/images/about_white.png", 27, 27));
		btn_layerHolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isServerStarted) switchViewLayer(3);
			}
		});
		sidebar.add(btn_layerHolder);
		
		JButton btn_about = new JButton("");
		btn_about.setSize(new Dimension(30, 30));
		btn_about.setBorderPainted(false);
		btn_about.setBackground(new Color(38, 139, 255));
		btn_about.setBounds(0, 655, 75, 75);
		btn_about.setIcon(ImageHelper.getScaledIcon("/images/about_white.png", 27, 27));
		btn_about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onAbout();
			}
		});
		sidebar.add(btn_about);
		
		viewMain = new JPanel(cardLayout);
		viewMain.setBounds(75, 0, 1111, 730);
		frame.getContentPane().add(viewMain);
		
		
		// == LOBBY ==
		JPanel lobby = new JPanel();
		lobby.setLayout(null);
		lobby.setBackground(new Color(251, 251, 251));
		lobby.setBounds(75, 0, 1111, 730);
		viewMain.add(lobby, "LOBBY");
		
		JLabel lbl_lobbyText = new JLabel("Vui lòng khởi động server để hiển thị các màn hình quản lý");
		lbl_lobbyText.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_lobbyText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_lobbyText.setBounds(300, 304, 500, 50);
		lobby.add(lbl_lobbyText);
		
		// == DASHBOARD ==
		JPanel dashboardPanel = Dashboard.build();
		viewMain.add(dashboardPanel, "DASHBOARD");
		
		// == SETTINGS ==
		JPanel settings = Settings.build();
		viewMain.add(settings, "SETTINGS");
	}

    private void onClosingEvent() {
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (isServerStarted) {
                    Alert.showError("Bạn phải tắt Server trước!");
                    return;
                }

                frame.dispose();
                System.exit(0);
            }
        });
    }

	private void toggleServer() throws IOException {
		if (!isServerStarted) { // turn on the server
			btn_power.setBackground(new Color(59, 238, 101));
            if (ServerStates.onServerStartListener != null) ServerStates.onServerStartListener.onServerStart();
			isServerStarted = true;
			switchViewLayer(2);
			
		} else {	// turn off the server
			switchViewLayer(1);
			btn_power.setBackground(new Color(238, 59, 62));
            if (ServerStates.onServerCloseListener != null) ServerStates.onServerCloseListener.onServerClose();
			isServerStarted = false;
		}
	}
	
	private void switchViewLayer(Integer layerNum) {
	    // reset sidebar button state
	    btn_dashboard.setBackground(new Color(38, 139, 255));
	    btn_layerHolder.setBackground(new Color(38, 139, 255));

	    switch (layerNum) {
	        case 1 : {
	            cardLayout.show(viewMain, "LOBBY");
	            break;
	        }
	        case 2 : {
	            btn_dashboard.setBackground(new Color(36, 128, 234));
	            cardLayout.show(viewMain, "DASHBOARD");
	            break;
	        }
	        case 3 : {
	            btn_layerHolder.setBackground(new Color(36, 128, 234));
	            cardLayout.show(viewMain, "SETTINGS");
	            break;
	        }
	        default : {
	        	log.warn("Unknown view layer: {}", layerNum);
	        	break;
	        }
	    }
	}
	
	private void onAbout() {
	    JOptionPane.showMessageDialog(
	            this.frame,
	            AboutModal.createContent(),
	            "Về hệ thống EduNet",
	            JOptionPane.PLAIN_MESSAGE
	    );
	}
}
