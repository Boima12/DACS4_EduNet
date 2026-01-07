package org.example.common.utils.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Code UI/logic cửa sổ modal nhập thông báo
 *
 */
@SuppressWarnings("serial")
public class NotificationInputModal extends JDialog {

    private JTextArea taInput;
    private String result = null;

    public NotificationInputModal(Frame parent) {
        super(parent, "Gửi thông báo", true); // true = MODAL
        initialize();
    }

    private void initialize() {
        setSize(580, 330);
        setLocationRelativeTo(getParent());
        setLayout(null);
        getContentPane().setBackground(new Color(251, 251, 251));

        taInput = new JTextArea();
        taInput.setWrapStyleWord(true);
        taInput.setLineWrap(true);
        taInput.setFont(new Font("Tahoma", Font.PLAIN, 13));
        taInput.setBackground(new Color(240, 240, 240));
        taInput.setBounds(10, 10, 546, 199);
        add(taInput);

        JButton btnSend = new JButton("Gửi");
        btnSend.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnSend.setBounds(170, 240, 100, 30);
        add(btnSend);

        JButton btnCancel = new JButton("Huỷ");
        btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnCancel.setBounds(310, 240, 100, 30);
        add(btnCancel);

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
