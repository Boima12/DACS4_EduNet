//package org.example.client.view;
//
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.*;
//import javax.swing.border.*;
//
//import org.example.client.controller.WhiteBoardController;
//
//public class Client_Screen extends JFrame {
//
//    private JPanel contentPane;
//
//    public Client_Screen() {
//        setTitle("CLIENT - Quản lý học tập");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(600, 450);
//        setLocationRelativeTo(null);
//
//        contentPane = new JPanel();
//        contentPane.setLayout(new BorderLayout(10, 10));
//        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
//        setContentPane(contentPane);
//
//        // ===== TITLE =====
//        JLabel lblTitle = new JLabel("CLIENT CONTROL PANEL", SwingConstants.CENTER);
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        lblTitle.setForeground(new Color(33, 150, 243));
//        contentPane.add(lblTitle, BorderLayout.NORTH);
//
//        // ===== CENTER PANEL =====
//        JPanel centerPanel = new JPanel();
//        centerPanel.setLayout(new GridLayout(3, 1, 10, 10));
//        contentPane.add(centerPanel, BorderLayout.CENTER);
//
//        // ===== MESSAGE PANEL =====
//        JPanel msgPanel = createPanel("GỬI TIN NHẮN");
//        JButton btnMsgAll = createButton("Gửi tin nhắn cho All Client");
//        JButton btnMsgServer = createButton("Gửi tin nhắn cho Server");
//
//        msgPanel.add(btnMsgAll);
//        msgPanel.add(btnMsgServer);
//
//        // ===== VIDEO PANEL =====
//        JPanel videoPanel = createPanel("CALL VIDEO");
//        JButton btnCallClient = createButton("Call Video Client");
//        JButton btnCallServer = createButton("Call Video Server");
//
//        videoPanel.add(btnCallClient);
//        videoPanel.add(btnCallServer);
//
//        // ===== TOOL PANEL =====
//        JPanel toolPanel = createPanel("CÔNG CỤ");
//        JButton btnWhiteboard = createButton("Bảng trắng");
//        JButton btnSubmit = createButton("Nộp bài");
//        btnWhiteboard.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO
//				try {
//					new WhiteBoardController();
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		});
//        toolPanel.add(btnWhiteboard);
//        toolPanel.add(btnSubmit);
//
//        // ADD TO CENTER
//        centerPanel.add(msgPanel);
//        centerPanel.add(videoPanel);
//        centerPanel.add(toolPanel);
//        
//        //
//        btnWhiteboard.addActionListener(e -> {
//        //    new WhiteBoardView();    
//            // mở giao diện
//            // hoặc nếu dùng MVC:
//            // new ClientController(new ClientView());
//        });
//
//        
//        
//    }
//
//    // ===== PANEL TEMPLATE =====
//    private JPanel createPanel(String title) {
//        JPanel panel = new JPanel();
//        panel.setLayout(new GridLayout(1, 2, 10, 10));
//        panel.setBorder(BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                title,
//                TitledBorder.LEFT,
//                TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 14),
//                new Color(80, 80, 80)
//        ));
//        return panel;
//    }
//
//    // ===== BUTTON TEMPLATE =====
//    private JButton createButton(String text) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        btn.setFocusPainted(false);
//        btn.setBackground(new Color(240, 240, 240));
//        return btn;
//    }
//
//    // ===== MAIN =====
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new Client_Screen().setVisible(true);
//        });
//    }
//}

package org.example.client.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import org.example.client.controller.WhiteBoardController;

public class Client_Screen extends JFrame {

    private JPanel contentPane;

    public Client_Screen(String serverIP, int serverPort) {
        setTitle("CLIENT - Quản lý học tập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("CLIENT CONTROL PANEL", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(33, 150, 243));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        // ===== CENTER PANEL =====
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // ===== MESSAGE PANEL =====
        JPanel msgPanel = createPanel("GỬI TIN NHẮN" + serverIP +"  " + serverPort);
        JButton btnMsgAll = createButton("Gửi tin nhắn cho All Client");
        JButton btnMsgServer = createButton("Gửi tin nhắn cho Server");
        msgPanel.add(btnMsgAll);
        msgPanel.add(btnMsgServer);

        // ===== VIDEO PANEL =====
        JPanel videoPanel = createPanel("CALL VIDEO");
        JButton btnCallClient = createButton("Call Video Client");
        JButton btnCallServer = createButton("Call Video Server");
        videoPanel.add(btnCallClient);
        videoPanel.add(btnCallServer);

        // ===== TOOL PANEL =====
        JPanel toolPanel = createPanel("CÔNG CỤ");
        JButton btnWhiteboard = createButton("Bảng trắng");
        JButton btnSubmit = createButton("Nộp bài");

        // Khi nhấn nút "Bảng trắng", mở WhiteBoard client và kết nối server
        btnWhiteboard.addActionListener(e -> {
            try {
                new WhiteBoardController(serverIP, serverPort); // kết nối tới WhiteBoard server
            
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Không thể kết nối tới server WhiteBoard!",
                        "Lỗi kết nối",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        toolPanel.add(btnWhiteboard);
        toolPanel.add(btnSubmit);

        // ADD TO CENTER
        centerPanel.add(msgPanel);
        centerPanel.add(videoPanel);
        centerPanel.add(toolPanel);
    }

    // ===== PANEL TEMPLATE =====
    private JPanel createPanel(String title) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                title,
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(80, 80, 80)
        ));
        return panel;
    }

    // ===== BUTTON TEMPLATE =====
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(240, 240, 240));
        return btn;
    }

//    // ===== MAIN =====
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new Client_Screen().setVisible(true));
//    }
}
