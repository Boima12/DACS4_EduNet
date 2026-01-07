package org.example.server.view.watch;

import javax.swing.*;
import java.awt.*;
import org.example.common.utils.gui.WrapLayout;

/**
 * File GUI cho chức năng Watch Video của Client
 *
 */
public class WatchView extends JFrame {

    private JPanel videoContainer;
	private Color mau081C15;
	private Color mauD8F3DC;
 
    /**
     * Constructor
     * - Khởi tạo giao diện WatchView
     * - Gọi hàm initUI() để dựng layout
     */
	public WatchView() {
        initUI();
    }

    private void initUI() {
    	this.setBounds(100, 60, 1300, 700);
        this.getContentPane().setLayout(null);
        //this.setUndecorated(true);

		mau081C15 = Color.decode("#081C15");
		mauD8F3DC = Color.decode("#D8F3DC");
        
        // Video container
        videoContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        videoContainer.setBackground(new Color(240, 240, 240));
        videoContainer.setBounds(0, 0, 1300, 700);

        JScrollPane scrollPane = new JScrollPane(videoContainer);   
        scrollPane.setBounds(0, 0, 1300, 650);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel.setBounds(0, 650, 1300, 50);
        panel.setBackground(mau081C15);
        panel.setLayout(null);
        getContentPane().add(panel);
        
        JLabel jLabel = new JLabel("WATCH VIDEO");
        jLabel.setForeground(new Color(255, 255, 255));
        jLabel.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        jLabel.setBounds(726, 0, 239, 50);
        jLabel.setBorder(null);
        panel.add(jLabel);
    }
    
    /**
     * Thêm một WatchPanel (Client mới) vào giao diện
     *
     * @param panel WatchPanel đại diện cho một Client
     *
     * - Được gọi khi Client kết nối hoặc bật chế độ Watch
     * - Chạy trên EDT để đảm bảo an toàn giao diện
     */
    public void addClientPanel(WatchPanel panel) {
        SwingUtilities.invokeLater(() -> {
            videoContainer.add(panel);
            videoContainer.revalidate();
            videoContainer.repaint();
        });
    }
    
    /**
     * Cập nhật hình ảnh (frame) của một Client
     *
     * @param panel WatchPanel cần cập nhật
     * @param img   Ảnh mới nhận từ Client
     *
     * - Thường được gọi liên tục khi Client gửi frame
     */
    public void updateClientImage(WatchPanel panel, Image img) {
        SwingUtilities.invokeLater(() -> panel.updateImage(img));
    }
    
    /**
     * Gỡ bỏ WatchPanel của Client khỏi giao diện
     *
     * @param panel WatchPanel của Client bị ngắt kết nối
     *
     * - Được gọi khi Client disconnect hoặc tắt Watch
     */
    public void removeClientPanel(WatchPanel panel) {
        SwingUtilities.invokeLater(() -> {
            videoContainer.remove(panel);
            videoContainer.revalidate();
            videoContainer.repaint();
        });
    }
}

