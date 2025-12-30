package org.example.server.controller.services.exercise;

import org.example.common.objects.services.exercise.Assignment;
import org.example.common.objects.services.exercise.Submission;
import org.example.server.ServerStates;
import org.example.server.controller.ServerNetworkHandler;
import org.example.server.view.exercise.ExerciseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.example.common.utils.gson.JsonUtils; // Giả định package chứa JsonUtils của bạn

import javax.swing.*;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExerciseController {
    private ExerciseView view;
    private final List<Assignment> assignmentList = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(ExerciseController.class);

    public ExerciseController() {
        // Có thể load bài tập cũ từ database/file JSON tại đây nếu cần
    }

    /**
     * Gắn View vào Controller và thiết lập các sự kiện
     */
    public void setView(ExerciseView exerciseView) {
        this.view = exerciseView;
        if (this.view == null) return;

        SwingUtilities.invokeLater(() -> {
            // 1. Cập nhật danh sách bài tập hiện có lên View
            view.assignmentModel.clear();
            for (Assignment a : assignmentList) {
                view.assignmentModel.addElement(a);
            }

            // 2. Sự kiện chọn bài tập trong danh sách
            view.assignmentList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    loadSubmissions(view.assignmentList.getSelectedValue());
                }
            });

            // 3. SỰ KIỆN NHẤN ĐÚP CHUỘT ĐỂ MỞ FILE (Đã sửa ở đây)
            view.submissionList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    if (evt.getClickCount() == 2) { // Nhấn đúp chuột
                        Submission selected = view.submissionList.getSelectedValue();
                        if (selected != null) {
                            log.info("[UI] Giáo viên nhấn đúp mở bài nộp của: {}", selected.studentName);
                            openSubmissionFile(selected);
                        }
                    }
                }
            });
        });
    }

    public void loadSubmissions(Assignment a) {
        if (view == null) return;
        view.submissionModel.clear();
        if (a != null) {
            a.submissions.forEach(view.submissionModel::addElement);
            view.statLabel.setText("Đã nộp: " + a.submissions.size());
        }
    }

    /**
     * Hàm mở file bài nộp
     */
    //   private void openSubmissionFile(Submission s) {
//        // Quy tắc đặt tên file phải khớp 100% với hàm handleSubmissionFromClient
//        String safeFileName = s.fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
//        String fileName = s.studentId + "_" + s.studentName + "_" + safeFileName;
//
//        File dir = new File("submissions/" + s.assignmentId);
//        File file = new File(dir, fileName);
//
//        log.info("[System] Đang thử mở file: {}", file.getAbsolutePath());
//
//        try {
//            if (!file.exists()) {
//                JOptionPane.showMessageDialog(view, "Không tìm thấy file bài nộp tại:\n" + file.getAbsolutePath(),
//                    "Lỗi", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            if (Desktop.isDesktopSupported()) {
//                Desktop desktop = Desktop.getDesktop();
//                if (desktop.isSupported(Desktop.Action.OPEN)) {
//                    desktop.open(file);
//                } else {
//                    log.warn("Hành động OPEN không được hỗ trợ.");
//                }
//            } else {
//                // Dự phòng cho Windows
//                if (System.getProperty("os.name").toLowerCase().contains("win")) {
//                    new ProcessBuilder("explorer.exe", file.getAbsolutePath()).start();
//                }
//            }
//        } catch (IOException e) {
//            log.error("Không thể mở file: ", e);
//            JOptionPane.showMessageDialog(view, "Lỗi khi mở file: " + e.getMessage());
//        }
//    }

    // Sửa thành nhận 1 tham số duy nhất
    public void openSubmissionFile(Submission s) {
        if (s == null) return;

        // Quy tắc đặt tên file
        String safeFileName = s.fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileName = s.studentId + "_" + s.studentName + "_" + safeFileName;

        // Đường dẫn
        File dir = new File("submissions/" + s.assignmentId);
        File file = new File(dir, fileName);

        try {
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(view, "File không tồn tại: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Lỗi mở file: ", e);
        }
    }

    public void createAssignment(String title, String content, LocalDateTime deadline) {
        Assignment a = new Assignment();
        a.id = UUID.randomUUID().toString();
        a.title = title;
        a.content = content;
        a.assignedDate = LocalDateTime.now();
        a.deadline = deadline;

        assignmentList.add(a);

        if (view != null) {
            SwingUtilities.invokeLater(() -> view.assignmentModel.addElement(a));
        }

        saveAssignments();
        ServerStates.fireAssignmentCreated(a);
    }

    public void handleSubmissionFromClient(Submission s, ServerNetworkHandler handler) {
        try {
            log.info("[ExerciseController] Nhận bài nộp từ: {}", s.studentName);

            Assignment assignment = assignmentList.stream()
                    .filter(a -> a.id.equals(s.assignmentId))
                    .findFirst()
                    .orElse(null);

            if (assignment == null) {
                handler.speak_submissionResult(false, "Không tìm thấy bài tập!");
                return;
            }

            File baseDir = new File("submissions");
            if (!baseDir.exists()) baseDir.mkdir();

            File assignmentDir = new File(baseDir, assignment.id);
            if (!assignmentDir.exists()) assignmentDir.mkdirs();

            String cleanFileName = s.fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
            File file = new File(assignmentDir, s.studentId + "_" + s.studentName + "_" + cleanFileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(s.fileData);
                fos.flush();
            }

            // Cập nhật dữ liệu (Nộp đè nếu đã tồn tại)
            assignment.submissions.removeIf(sub -> sub.studentId.equals(s.studentId));
            assignment.submissions.add(s);

            saveAssignments();

            // Cập nhật UI
            if (view != null) {
                SwingUtilities.invokeLater(() -> {
                    Assignment selected = view.assignmentList.getSelectedValue();
                    if (selected != null && selected.id.equals(assignment.id)) {
                        loadSubmissions(selected);
                    }
                });
            }

            handler.speak_submissionResult(true, "Nộp bài thành công!");
            ServerStates.fireSubmissionReceived(assignment, s);

        } catch (Exception e) {
            log.error("[Error] Lỗi xử lý bài nộp: ", e);
            if (handler != null) handler.speak_submissionResult(false, "Lỗi Server: " + e.getMessage());
        }
    }

    public void saveAssignments() {
        try {
            String json = JsonUtils.gson.toJson(assignmentList);
            File file = new File("assignments.json");
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write(json);
            }
            log.info("[System] Đã lưu dữ liệu vào assignments.json");
        } catch (Exception e) {
            log.error("[Error] Lỗi lưu bài tập: ", e.getMessage());
        }
    }

    public List<Assignment> getAllAssignments() { return assignmentList; }
}