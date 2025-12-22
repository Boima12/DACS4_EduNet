package org.example.client.view;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.example.common.utils.network.NetworkUtils;

import java.awt.Font;

/**
 * UI chính của client
 *
 */
@SuppressWarnings("FieldMayBeFinal")
public class EClient {

	private boolean isStatusConnected;
	private String localIP;
	private String serverIP;
    private String serverPort;
	
	private JFrame frame;
	private JLabel lbl_status2;
    private JLabel lbl_serverIP;

	/**
	 * Create the application.
	 */
	public EClient() {
		isStatusConnected = true;	// TODO remove later when implement 15s check functionality
		localIP = NetworkUtils.getLocalIPAddress();

		initialize();
		updateStatus();
	}
	
	public void display(String serverIP, String serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        // Update label text after values are set to avoid nulls
        if (lbl_serverIP != null) {
            lbl_serverIP.setText("Server IP: " + (this.serverIP != null ? this.serverIP : "-") + ":" + (this.serverPort != null ? this.serverPort : "-"));
            lbl_serverIP.revalidate();
            lbl_serverIP.repaint();
        }
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
		frame.getContentPane().setBackground(new Color(251, 251, 251));
		frame.setBounds(100, 100, 400, 240);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("EClient - " + localIP);
		frame.getContentPane().setLayout(null);
		
		JLabel lbl_EduNet = new JLabel("EduNet client");
		lbl_EduNet.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lbl_EduNet.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_EduNet.setBounds(70, 17, 236, 30);
		frame.getContentPane().add(lbl_EduNet);
		
		JLabel lbl_status1 = new JLabel("Tình trạng: ");
		lbl_status1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_status1.setBounds(119, 62, 65, 22);
		frame.getContentPane().add(lbl_status1);
		
		lbl_status2 = new JLabel("Đã liên kết");
		lbl_status2.setForeground(new Color(19, 166, 19));	
		lbl_status2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_status2.setBounds(186, 62, 151, 22);
		frame.getContentPane().add(lbl_status2);
		
		// Initialize with placeholder; actual text set in display()
		lbl_serverIP = new JLabel("Server IP: -:-");
		lbl_serverIP.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_serverIP.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_serverIP.setBounds(53, 94, 287, 22);
		frame.getContentPane().add(lbl_serverIP);
		
		JLabel lbl_serverIP_1 = new JLabel("<html><div style='text-align: center;'>Kể cả khi cửa sổ EduNet client này được đóng, chương trình vẫn sẽ chạy ngầm để giữ liên kết với server</div></html>");
		lbl_serverIP_1.setForeground(new Color(176, 176, 176));
		lbl_serverIP_1.setVerticalAlignment(SwingConstants.TOP);
		lbl_serverIP_1.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_serverIP_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_serverIP_1.setBounds(53, 126, 287, 67);
		frame.getContentPane().add(lbl_serverIP_1);
	}
	
	private void updateStatus() {
		// every 15 seconds, update lbl_status2
		
		// TODO every 15 seconds check connection between server and client
		if (isStatusConnected) {
			lbl_status2.setText("Đã liên kết");
			lbl_status2.setForeground(new Color(19, 166, 19));	
		} else {
			lbl_status2.setText("Mất kết nối");
			lbl_status2.setForeground(new Color(238, 59, 62));			
		}
	}
}
