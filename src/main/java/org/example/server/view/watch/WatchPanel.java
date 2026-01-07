package org.example.server.view.watch;

import java.awt.*;
import javax.swing.*;

/**
 * WatchPanel
 * ----------------------------
 * Lớp này đại diện cho **một khung hiển thị video / hình ảnh của 1 Client**
 * trong chức năng Watch Video phía Server.
 *
 * Mỗi WatchPanel tương ứng với một Client đang kết nối,
 * dùng để hiển thị ảnh chụp màn hình hoặc luồng video gửi từ Client.
 *
 * WatchPanel sẽ được đặt bên trong WatchView (videoContainer),
 * và được sắp xếp theo dạng lưới (WrapLayout).
 */
public class WatchPanel extends JPanel {

    private JLabel videoLabel;

    /**
     * Constructor WatchPanel
     * ----------------------------
     * Khởi tạo giao diện cho 1 khung xem video:
     * - Kích thước mặc định 200x150
     * - Layout BorderLayout
     * - Nền màu đen (giả lập màn hình video)
     * - Viền xám để phân biệt các Client
     */
    public WatchPanel() {
        setPreferredSize(new Dimension(200, 150));
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        videoLabel = new JLabel();
        videoLabel.setHorizontalAlignment(JLabel.CENTER);
        videoLabel.setOpaque(true);
        videoLabel.setBackground(Color.BLACK);

        add(videoLabel, BorderLayout.CENTER);
    }
    
    /**
     * updateImage(Image img)
     * ----------------------------
     * Cập nhật hình ảnh (frame video) hiển thị trong WatchPanel.
     *
     * Chức năng:
     * - Nhận ảnh từ Server Network / Controller
     * - Scale ảnh theo kích thước hiện tại của panel
     * - Cập nhật icon cho JLabel
     */
    public void updateImage(Image img) {
        SwingUtilities.invokeLater(() -> {
            int w = getWidth();
            int h = getHeight();

            if (w <= 0 || h <= 0) return;

            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);

            videoLabel.setIcon(new ImageIcon(scaled));
        });
    }
}