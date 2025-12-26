package org.example.server.view.login;

import org.example.common.utils.gui.Alert;
import org.example.common.utils.gui.RoundedBorder;
import org.example.server.model.database.JDBCUtil;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Code UI cho cửa sổ đăng nhập của admin
 *
 */
public class Login {

	private OnLoginListenter onLoginListener;
	
	private JFrame frame;
	private JPasswordField tf_password;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();  
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
	}

	public void display() {
		frame.setVisible(true);
	}

	public void undisplay() {
		frame.setVisible(false);
	}
	
	public void setOnLoginListenerCallback(OnLoginListenter callback) {
		this.onLoginListener = callback;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(251, 251, 251));
		frame.setBounds(100, 100, 533, 331);
		frame.setTitle("EduNet - Admin login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lbl_EduNet = new JLabel("EduNet");
		lbl_EduNet.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_EduNet.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl_EduNet.setBounds(198, 23, 108, 51);
		frame.getContentPane().add(lbl_EduNet);
		
		JLabel lbl_password = new JLabel("Password: ");
		lbl_password.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_password.setBounds(53, 116, 73, 35);
		frame.getContentPane().add(lbl_password);
		
		tf_password = new JPasswordField();
		tf_password.setBorder(new RoundedBorder(8));
		tf_password.setBackground(new Color(240, 240, 240));
		tf_password.setBounds(124, 118, 335, 35);
		tf_password.setColumns(10);
		tf_password.addActionListener(new ActionListener() { // Enter is pressed in the password field
			@Override
			public void actionPerformed(ActionEvent e) {
				onLogin();
			}
		});
		frame.getContentPane().add(tf_password);

		JButton btn_login = new JButton("Login");
		btn_login.setBorderPainted(false);
		btn_login.setForeground(new Color(255, 255, 255));
		btn_login.setBackground(new Color(38, 139, 255));
		btn_login.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_login.setBounds(206, 216, 100, 35);
		btn_login.addActionListener(new ActionListener() { // Button is clicked
			@Override
			public void actionPerformed(ActionEvent e) {
				onLogin();
			}
		});
		frame.getContentPane().add(btn_login);
	}
	
	private void onLogin() {
		// retrieve server password from database
		AtomicReference<String> correctPassword = new AtomicReference<>();
        String sql = "SELECT server_password FROM server_config";
        JDBCUtil.runQuery(sql, rs -> {
            while (rs.next()) {
                correctPassword.set(rs.getString("server_password"));
            }
        });
	    
		String passwordData = new String(tf_password.getPassword()).trim();

	    if (passwordData.isEmpty()) {
	        Alert.showError("Vui lòng nhập mật khẩu.");
	        return;
	    }
	    
	    if (!passwordData.equals(correctPassword.get())) {
	    	Alert.showError("Mật khẩu sai.");
	    } else {
	    	if (onLoginListener != null) onLoginListener.onLogin(); 
	    }
	}

}
