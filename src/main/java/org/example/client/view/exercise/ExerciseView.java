package org.example.client.view.exercise;

import org.example.common.objects.services.exercise.Assignment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ExerciseView extends JPanel {

    public DefaultListModel<Assignment> assignmentModel = new DefaultListModel<>();
    public JList<Assignment> assignmentList = new JList<>(assignmentModel);

    public JTextArea detailArea = new JTextArea();
    public JLabel statusLabel = new JLabel("Danh sách bài tập được giao");
    public JButton submitBtn = new JButton("Chọn file và Nộp bài");

    public ExerciseView() {
        // 1. QUAN TRỌNG: Phải set BorderLayout thì các vị trí NORTH, CENTER, SOUTH mới chạy
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(800, 500));

        // 2. Cấu hình các thành phần hiển thị
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        assignmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 3. Tạo tiêu đề cho các cột
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel(" Bài tập hiện có:"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(assignmentList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel(" Chi tiết yêu cầu:"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);

        // 4. Chia đôi màn hình
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                rightPanel
        );
        split.setDividerLocation(300);

        // 5. Thêm vào Main Panel theo Layout chuẩn
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(new Color(41, 128, 185));

        submitBtn.setPreferredSize(new Dimension(0, 40));
        submitBtn.setBackground(new Color(52, 152, 219));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        add(statusLabel, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
    }
}