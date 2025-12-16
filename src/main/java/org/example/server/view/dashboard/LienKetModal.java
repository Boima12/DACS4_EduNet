package org.example.server.view.dashboard;

import javax.swing.*;
import java.awt.*;

import org.example.server.ServerStates;

public class LienKetModal extends JDialog {

    private int maLienKet;

    public LienKetModal(JFrame parent) {
        super(parent, "Mã liên kết", true); // true = modal
        this.maLienKet = generateMaLienKet();
        ServerStates.MA_LIEN_KET = maLienKet;

        initUI();
        setupEvents();
    }

    private void initUI() {
        setSize(420, 240);
        setLocationRelativeTo(getParent());
        setLayout(null);
        getContentPane().setBackground(new Color(251, 251, 251));

        JLabel lblMa = new JLabel(String.valueOf(maLienKet), SwingConstants.CENTER);
        lblMa.setFont(new Font("Tahoma", Font.BOLD, 32));
        lblMa.setBounds(40, 40, 340, 40);
        add(lblMa);

        JLabel lblGuide = new JLabel(
                "<html><div style='text-align: center;'>Client nhập 6 số trên để liên kết lần đầu</div></html>",
                SwingConstants.CENTER
        );
        lblGuide.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblGuide.setBounds(40, 100, 340, 40);
        add(lblGuide);

        JButton btnClose = new JButton("Hủy");
        btnClose.setBounds(160, 160, 100, 35);
        btnClose.addActionListener(e -> turnOffLinkMode());
        add(btnClose);
    }

    private void setupEvents() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                turnOffLinkMode();
            }
        });
    }

    private int generateMaLienKet() {
        return (int) (Math.random() * 900000) + 100000;
    }

    public void turnOffLinkMode() {
        ServerStates.MA_LIEN_KET = 1;
        dispose();
    }
}
