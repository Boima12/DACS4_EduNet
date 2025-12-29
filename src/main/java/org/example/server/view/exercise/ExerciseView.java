package org.example.server.view.exercise;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.example.common.objects.exercise.Assignment;
import org.example.common.objects.exercise.Submission;
import org.example.server.controller.services.exercise.ExerciseController;
import org.example.server.model.CoreServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExerciseView extends JPanel {
    // Các model để quản lý dữ liệu hiển thị
    public DefaultListModel<Assignment> assignmentModel = new DefaultListModel<>();
    public JList<Assignment> assignmentList = new JList<>(assignmentModel);
    
    public DefaultListModel<Submission> submissionModel = new DefaultListModel<>();
    public JList<Submission> submissionList = new JList<>(submissionModel);
    
    public JLabel statLabel = new JLabel("Chưa chọn bài tập");
    private JButton addBtn = new JButton("Giao bài tập mới");
    private static final Logger log = LoggerFactory.getLogger(ExerciseController.class);

    private final ExerciseController controller;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

 // TRONG ExerciseView.java
    public ExerciseView() {
        // Lấy instance controller đã được khởi tạo trong start() của CoreServer
        this.controller = CoreServer.getExerciseController();
        
        if (this.controller == null) {
            logError("ExerciseController chưa được khởi tạo! Hãy kiểm tra luồng khởi động Server.");
            return;
        }

        // Kết nối View này vào Controller để nhận dữ liệu
        controller.setView(this);
        
        initUI();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tùy chỉnh hiển thị danh sách
        assignmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        submissionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Panel thống kê phía trên
        statLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statLabel.setForeground(new Color(41, 128, 185));
        add(statLabel, BorderLayout.NORTH);

        // SplitPane chia đôi màn hình: Trái (Bài tập) - Phải (Bài nộp)
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createTitledPanel("Danh sách bài tập", assignmentList),
                createTitledPanel("Sinh viên đã nộp", submissionList)
        );
        split.setDividerLocation(300);
        split.setContinuousLayout(true);
        
        add(split, BorderLayout.CENTER);

        // Nút bấm phía dưới
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(addBtn, BorderLayout.SOUTH);
    }

    private JPanel createTitledPanel(String title, JList<?> list) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private void initEvents() {
        // Sự kiện nút Giao bài
        addBtn.addActionListener(e -> showCreateAssignmentDialog());

        // Khi chọn một bài tập trong danh sách bên trái
        assignmentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Assignment a = assignmentList.getSelectedValue();
                // Cần bọc trong invokeLater để an toàn luồng
                SwingUtilities.invokeLater(() -> {
                    submissionModel.clear();
                    if (a != null) {
                        controller.loadSubmissions(a);
                        statLabel.setText("Đang xem bài: " + a.title + " | Deadline: " + a.deadline.format(formatter));
                    } else {
                        statLabel.setText("Chưa chọn bài tập");
                    }
                });
            }
        });

        // Double click để mở file bài nộp
//        submissionList.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() == 2) {
//                    Assignment a = assignmentList.getSelectedValue();
//                    Submission s = submissionList.getSelectedValue();
//                    if (a != null && s != null) {
//                       // controller.openSubmissionFile(a, s);
//                    	// Chỉ truyền selectedSubmission vào thôi
//                    	controller.openSubmissionFile(s);
//                    }
//                }
//            }
//        });
        submissionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Thêm kiểm tra SwingUtilities.isLeftMouseButton(e)
                if (e.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    Submission s = submissionList.getSelectedValue();
                    if (s != null) {
                        log.info("[UI] Mở bài nộp của sinh viên: " + s.studentName);
                        controller.openSubmissionFile(s);
                    }
                }
            }
        });
    }

    private void showCreateAssignmentDialog() {
        JTextField titleField = new JTextField();
        JTextArea contentArea = new JTextArea(5, 20);
        contentArea.setLineWrap(true);
        
        // Mặc định deadline là 24h sau (Dựa trên thời điểm hiện tại: 2025-12-29)
        String defaultDeadline = LocalDateTime.now().plusDays(1).withNano(0).toString();
        JTextField deadlineField = new JTextField(defaultDeadline);

        Object[] fields = {
                "Tên bài tập:", titleField,
                "Nội dung yêu cầu:", new JScrollPane(contentArea),
                "Hạn chót (ISO format):", deadlineField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Thiết lập bài tập mới", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên bài tập không được để trống!");
                return;
            }

            try {
                LocalDateTime deadline = LocalDateTime.parse(deadlineField.getText());
                if (deadline.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Hạn chót không được ở quá khứ!");
                }
                
                controller.createAssignment(title, content, deadline);
                JOptionPane.showMessageDialog(this, "Đã giao bài tập thành công!");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage());
            }
        }
    }

    private void logError(String msg) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(this, msg, "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE)
        );
    }
}