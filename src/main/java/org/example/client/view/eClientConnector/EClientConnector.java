package org.example.client.view.eClientConnector;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.example.client.controller.ClientNetwork;
import org.example.client.model.CoreClient;
import org.example.common.utils.gui.Alert;
import org.example.common.utils.gui.ImageHelper;
import org.example.common.utils.gui.RoundedBorder;

import javax.swing.UIManager;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EClientConnector {

//	private OnEstablishListener onEstablishListener;
	private boolean isLanModeConnector = false;
    private static final Logger log = LoggerFactory.getLogger(CoreClient.class);
	
	private JFrame frame;
	private JTextField tf_serverIP;
	private JButton btn_connect;
	private JTextField tf_port;
	private JTextField tf_maLienKet;
	private JLabel lbl_lan_icon;
	private JLabel lbl_lan_status;
	private JButton btn_lan_open;
	private JButton btn_lan_close;


	/**
	 * Create the application.
	 */
	public EClientConnector() {
		initialize();
	}
	
	public void display() {
		frame.setVisible(true);
	}

	public void undisplay() {
		frame.setVisible(false);
	}
	
//	public void setOnEstablishListenerCallback(OnEstablishListener callback) {
//		this.onEstablishListener = callback;
//	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(251, 251, 251));
		frame.setBounds(100, 100, 763, 370);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("EClient - Thiết lập kết nối");
		frame.getContentPane().setLayout(null);
		
		JPanel panel_manual = new JPanel();
		panel_manual.setBackground(new Color(251, 251, 251));
		panel_manual.setBounds(0, 0, 519, 333);
		panel_manual.setLayout(null);
		frame.getContentPane().add(panel_manual);
		
		JLabel lbl_title1 = new JLabel("Thiết lập kết nối tới EduNet server");
		lbl_title1.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_title1.setFont(new Font("Tahoma", Font.BOLD, 16));
		lbl_title1.setBounds(20, 20, 361, 25);
		panel_manual.add(lbl_title1);
		
		JLabel lbl_serverIP = new JLabel("Server IP:");
		lbl_serverIP.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_serverIP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbl_serverIP.setBounds(20, 97, 103, 28);
		panel_manual.add(lbl_serverIP);
		
		tf_serverIP = new JTextField();
		tf_serverIP.setBackground(new Color(240, 240, 240));
		tf_serverIP.setBounds(126, 97, 335, 28);
		tf_serverIP.setColumns(10);
		panel_manual.add(tf_serverIP);

		btn_connect = new JButton("Thiết lập kết nối");
		btn_connect.setBorderPainted(false);
		btn_connect.setForeground(new Color(255, 255, 255));
		btn_connect.setBackground(new Color(38, 139, 255));
		btn_connect.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_connect.setBounds(178, 250, 150, 35);
		btn_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                try {
                    onEstablish();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
		});
		panel_manual.add(btn_connect);
		
		JLabel lbl_port = new JLabel("Port:");
		lbl_port.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_port.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbl_port.setBounds(20, 135, 103, 28);
		panel_manual.add(lbl_port);
		
		tf_port = new JTextField();
		tf_port.setColumns(10);
		tf_port.setBackground(UIManager.getColor("Button.background"));
		tf_port.setBounds(126, 135, 335, 28);
		panel_manual.add(tf_port);
		
		JLabel lbl_maLienKet = new JLabel("Mã liên kết:");
		lbl_maLienKet.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_maLienKet.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbl_maLienKet.setBounds(20, 173, 103, 28);
		panel_manual.add(lbl_maLienKet);
		
		tf_maLienKet = new JTextField();
		tf_maLienKet.setColumns(10);
		tf_maLienKet.setBackground(UIManager.getColor("Button.background"));
		tf_maLienKet.setBounds(126, 173, 335, 28);
		panel_manual.add(tf_maLienKet);
		
		JPanel panel_auto = new JPanel();
		panel_auto.setBackground(new Color(251, 251, 251));
		panel_auto.setBounds(519, 0, 230, 333);
		frame.getContentPane().add(panel_auto);
		panel_auto.setLayout(null);
		
		lbl_lan_icon = new JLabel("");
		lbl_lan_icon.setBounds(60, 65, 90, 90);
		lbl_lan_icon.setIcon(ImageHelper.getScaledIcon("/images/lan_connect_gray.png", 90, 90));
		panel_auto.add(lbl_lan_icon);
		
		lbl_lan_status = new JLabel("<html><div style='text-align: center;'>Mở luồng quét mạng Lan để Server quét và tự kết nối</div></html>");
		lbl_lan_status.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_lan_status.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbl_lan_status.setBounds(10, 165, 180, 47);
		panel_auto.add(lbl_lan_status);
		
		btn_lan_open = new JButton("Mở luồng quét");
		btn_lan_open.setForeground(Color.BLACK);
		btn_lan_open.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_lan_open.setBorder(new RoundedBorder(8));
		btn_lan_open.setBackground(new Color(251, 251, 251));
		btn_lan_open.setBounds(27, 220, 150, 35);
		btn_lan_open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleLanConnector();
			}
		});
		panel_auto.add(btn_lan_open);
		
		btn_lan_close = new JButton("Đóng luồng quét");
		btn_lan_open.setForeground(Color.BLACK);
		btn_lan_close.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_lan_close.setBorder(new RoundedBorder(8));
		btn_lan_close.setBackground(new Color(251, 251, 251));
		btn_lan_close.setBounds(27, 263, 150, 35);
		btn_lan_close.setEnabled(false);
		btn_lan_close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleLanConnector();
			}
		});
		panel_auto.add(btn_lan_close);
	}
	
	private void onEstablish() throws IOException {
        // validate inputs
        String serverIP = tf_serverIP.getText().trim();
        String portStr = tf_port.getText().trim();
        String maLienKetStr = tf_maLienKet.getText().trim();

        if (serverIP.isEmpty() || portStr.isEmpty() || maLienKetStr.isEmpty()) {
            Alert.showError("Các trường không được để trống");
            return;
        }

        try {
            int maLienKet = Integer.parseInt(maLienKetStr);
            if (maLienKet < 100000 || maLienKet > 999999) {
                Alert.showError("Mã liên kết phải là số gồm 6 chữ số");;
                return;
            }
        } catch (NumberFormatException e) {
            Alert.showError("Mã liên kết phải là số gồm 6 chữ số");;
            return;
        }

//        if (onEstablishListener != null) onEstablishListener.onEstablish(serverIP, portStr, maLienKetStr);

        askForServerApproval(serverIP, portStr, maLienKetStr);
	}

    private void askForServerApproval(String serverIP, String port, String maLienKet) {
        try {
            ClientNetwork clientNetwork = new ClientNetwork(serverIP, Integer.parseInt(port));
            clientNetwork.send_establishingRequest(Integer.parseInt(maLienKet));
        } catch (NumberFormatException nfe) {
            Alert.showError("Port or code format is invalid.");
            log.error("Invalid number format in askForServerApproval", nfe);
        } catch (IOException ioe) {
            Alert.showError("Không thể kết nối tới server: " + ioe.getMessage());
            log.error("Failed to open socket for establishing request", ioe);
        }
    }

	private void toggleLanConnector() {
		if (!isLanModeConnector) {
			// TODO open LAN network for server to perform a LAN client scan
			
			lbl_lan_icon.setIcon(ImageHelper.getScaledIcon("/images/lan_connect_green.png", 90, 90));
			lbl_lan_status.setText("<html><div style='text-align: center;'>Đang mở luồng mạng Lan chờ server quét</div></html>");
			btn_lan_open.setEnabled(false);
			btn_lan_close.setEnabled(true);
			
			isLanModeConnector = true;
		} else {
			
			// TODO close LAN network, server can no longer found this client when perform a LAN client scan
			
			lbl_lan_icon.setIcon(ImageHelper.getScaledIcon("/images/lan_connect_gray.png", 90, 90));
			lbl_lan_status.setText("<html><div style='text-align: center;'>Mở luồng quét mạng Lan để Server quét và tự kết nối</div></html>");
			btn_lan_open.setEnabled(true);
			btn_lan_close.setEnabled(false);
			
			isLanModeConnector = false;
		}
	}
}
