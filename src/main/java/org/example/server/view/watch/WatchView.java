package org.example.server.view.watch;

import javax.swing.*;
import java.awt.*;
import org.example.common.utils.gui.WrapLayout;

//public class WatchView extends JFrame {
//
//    private JPanel videoContainer;
//
//    public WatchView() {
//        initUI();
//    }
//
//    private void initUI() {
//        this.setTitle("Watch Multiple Clients");
//        this.setSize(1300, 700);
//        this.setLocationRelativeTo(null);
//        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//        this.getContentPane().setLayout(null);
//
//        // Header
//        JPanel headerPanel = new JPanel();
//        headerPanel.setBackground(new Color(128, 128, 128));
//        headerPanel.setBounds(0, 0, 1000, 50);
//        headerPanel.setLayout(null);
//        getContentPane().add(headerPanel);
//
//        JButton button_quaylai = new JButton("Quay lại");
//        button_quaylai.setBounds(10, 10, 85, 30);
//        headerPanel.add(button_quaylai);
//        button_quaylai.addActionListener(e -> this.setVisible(false));
//
//        JLabel label_topic = new JLabel("QUAN SÁT MÁY TRẠM");
//        label_topic.setHorizontalAlignment(SwingConstants.CENTER);
//        label_topic.setFont(new Font("Tahoma", Font.PLAIN, 20));
//        label_topic.setBounds(103, 10, 887, 30);
//        headerPanel.add(label_topic);
//
//        // Video container
//        videoContainer = new JPanel();
//        videoContainer.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
//        videoContainer.setBackground(new Color(240, 240, 240));
//
//        JScrollPane scrollPane = new JScrollPane(videoContainer);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPane.setBounds(0, 50, 1000, 500);
//        getContentPane().add(scrollPane);
//
//        // Right panel
//        JPanel rightPanel = new JPanel();
//        rightPanel.setBackground(new Color(192, 192, 192));
//        rightPanel.setBounds(1010, 0, 300, 700);
//        getContentPane().add(rightPanel);
//    }
//
//    // Callback để thêm client panel
//    public void addClientPanel(WatchPanel panel) {
//        SwingUtilities.invokeLater(() -> {
//            videoContainer.add(panel);
//            videoContainer.revalidate();
//            videoContainer.repaint();
//        });
//    }
//
//    // Callback để cập nhật ảnh cho panel
//    public void updateClientImage(WatchPanel panel, Image img) {
//        SwingUtilities.invokeLater(() -> panel.updateImage(img));
//    }
//
//    // Callback để xóa panel khi client ngắt kết nối
//    public void removeClientPanel(WatchPanel panel) {
//        SwingUtilities.invokeLater(() -> {
//            videoContainer.remove(panel);
//            videoContainer.revalidate();
//            videoContainer.repaint();
//        });
//    }
//}

public class WatchView extends JFrame {

    private JPanel videoContainer;

    public WatchView() {
        initUI();
    }

    private void initUI() {
        this.setTitle("Watch Multiple Clients");
        this.setSize(1300, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Sử dụng BorderLayout chính
        this.getContentPane().setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(128, 128, 128));
        headerPanel.setPreferredSize(new Dimension(0, 50));

        JButton button_quaylai = new JButton("Quay lại");
        button_quaylai.setPreferredSize(new Dimension(85, 30));
        button_quaylai.addActionListener(e -> this.setVisible(false));

        JLabel label_topic = new JLabel("QUAN SÁT MÁY TRẠM", SwingConstants.CENTER);
        label_topic.setFont(new Font("Tahoma", Font.PLAIN, 20));

        headerPanel.add(button_quaylai, BorderLayout.WEST);
        headerPanel.add(label_topic, BorderLayout.CENTER);
        this.getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Video container
        videoContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        videoContainer.setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(videoContainer);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Right panel
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(192, 192, 192));
        rightPanel.setPreferredSize(new Dimension(300, 0));
        this.getContentPane().add(rightPanel, BorderLayout.EAST);
    }

    public void addClientPanel(WatchPanel panel) {
        SwingUtilities.invokeLater(() -> {
            videoContainer.add(panel);
            videoContainer.revalidate();
            videoContainer.repaint();
        });
    }

    public void updateClientImage(WatchPanel panel, Image img) {
        SwingUtilities.invokeLater(() -> panel.updateImage(img));
    }

    public void removeClientPanel(WatchPanel panel) {
        SwingUtilities.invokeLater(() -> {
            videoContainer.remove(panel);
            videoContainer.revalidate();
            videoContainer.repaint();
        });
    }
}

