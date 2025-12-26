package org.example.server.view.dashboard;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class NotificationInputModal extends JDialog {

    private JTextArea taInput;
    private String result = null;
	private Color mauEE6C4D;
	private Color mauffffff;
	private Color mau98C1D9;
	private Color mau000000;
	private Color mau293240;
	private Color mauE0FBFC;
	private Color mau3D5A80;

    public NotificationInputModal(Frame parent) {
        super(parent, "Gửi thông báo", true); // true = MODAL
        initialize();
    }

    private void initialize() {
    	
	    mauEE6C4D = Color.decode("#EE6C4D");
		mauffffff = Color.decode("#ffffff");
		mau000000 = Color.decode("#000000");
		mau98C1D9 = Color.decode("#98C1D9");
		mau293240 = Color.decode("#293240");
		mauE0FBFC = Color.decode("#E0FBFC");
		mau3D5A80 = Color.decode("#3D5A80");
    	
        setSize(580, 300);
        setLocationRelativeTo(getParent());
        getContentPane().setLayout(null);
        getContentPane().setBackground(mau293240);
//        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        taInput = new JTextArea();
        taInput.setWrapStyleWord(true);
        taInput.setLineWrap(true);
        taInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
        taInput.setBackground(mauE0FBFC);
        taInput.setBounds(10, 10, 560, 230);
        getContentPane().add(taInput);

        JButton btnSend = new JButton("Gửi");
        btnSend.setForeground(mauffffff);
        btnSend.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnSend.setBounds(165, 255, 100, 30);
        btnSend.setBackground(mauEE6C4D);
        getContentPane().add(btnSend);

        JButton btnCancel = new JButton("Huỷ");
        btnCancel.setForeground(mauffffff);
        btnCancel.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnCancel.setBounds(305, 255, 100, 30);
        btnCancel.setBackground(mauEE6C4D);
        getContentPane().add(btnCancel);

        // Events
        btnSend.addActionListener(e -> {
            result = taInput.getText().trim();
            dispose();
        });

        btnCancel.addActionListener(e -> {
            result = null;
            dispose();
        });
    }

    /**
     * Hiển thị modal và trả về String
     */
    public String showDialog() {
        setVisible(true); // BLOCK ở đây cho tới khi dispose()
        return result;
    }
}
