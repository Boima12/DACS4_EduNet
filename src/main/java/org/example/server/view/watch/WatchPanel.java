package org.example.server.view.watch;


import java.awt.*;
import javax.swing.*;

public class WatchPanel extends JPanel {

    private JLabel videoLabel;

    public WatchPanel() {
        setPreferredSize(new Dimension(320, 250));
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        videoLabel = new JLabel();
        videoLabel.setHorizontalAlignment(JLabel.CENTER);
        videoLabel.setOpaque(true);
        videoLabel.setBackground(Color.BLACK);

        add(videoLabel, BorderLayout.CENTER);
    }

    public void updateImage(Image img) {
        SwingUtilities.invokeLater(() -> {
            int w = getWidth();
            int h = getHeight();

            if (w <= 0 || h <= 0) return;

            Image scaled = img.getScaledInstance(
                    w, h, Image.SCALE_SMOOTH);

            videoLabel.setIcon(new ImageIcon(scaled));
        });
    }
}