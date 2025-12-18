package org.example.server.view.about;

import javax.swing.*;
import java.awt.*;

/**
 * Code UI cho cửa sổ hiển thị giới thiệu
 *
 */
public class AboutModal {

    private AboutModal() {
        // utility class
    }

    public static JPanel createContent() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(251, 251, 251));
        panel.setPreferredSize(new Dimension(520, 260));

        JTextArea taText = new JTextArea();
        taText.setBackground(new Color(251, 251, 251));
        taText.setFont(new Font("Tahoma", Font.PLAIN, 12));
        taText.setLineWrap(true);
        taText.setWrapStyleWord(true);
        taText.setEditable(false);
        taText.setText(
                "Lập trình viên:\n" +
                "- Cao Hoàng Phước Bảo\n" +
                "- Nguyễn Quốc Hoàng\n\n" +
                "EduNet là một hệ thống quản lý mạng LAN cho phòng học máy tính " +
                "dựa trên mô hình Client–Server, cho phép giảng viên hoặc quản trị viên " +
                "theo dõi và điều khiển các máy trạm trong thời gian thực.\n\n" +
                "Dự án dùng để báo cáo học phần Đồ án cơ sở 4 tại VKU."
        );
        taText.setBounds(20, 20, 480, 150);
        panel.add(taText);

        JLabel lblFooter = new JLabel("@2025 EduNet");
        lblFooter.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(180, 180, 180));
        lblFooter.setBounds(20, 180, 200, 20);
        panel.add(lblFooter);

        return panel;
    }
}
