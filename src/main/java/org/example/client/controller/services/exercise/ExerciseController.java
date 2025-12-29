//package org.example.client.controller.services.exercise;
//
//import org.example.client.ClientStates;
//import org.example.client.controller.ClientNetwork;
//import org.example.client.view.exercise.ExerciseView;
//import org.example.common.objects.exercise.Assignment;
//import org.example.common.objects.exercise.Submission;
//import javax.swing.*;
//import java.io.File;
//import java.nio.file.Files;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class ExerciseController {
//    private final ExerciseView view;
//    private final ClientNetwork clientNetwork;
//    private JFrame frame;
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//
//    public ExerciseController(ClientNetwork clientNetwork) {
//        this.clientNetwork = clientNetwork;
//        this.view = new ExerciseView();
//        
//        initUIEvents();
//        initNetworkListeners();
//        
//        // Đăng ký sự kiện mở giao diện từ Dashboard/Menu chính
//        ClientStates.setOnShowExerciseListener(this::showView);
//    }
//
//    /* ================= GIAO DIỆN (UI) ================= */
//    
//    private void initUIEvents() {
//        // Lắng nghe chọn bài tập trong danh sách
//        view.assignmentList.addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) showDetail();
//        });
//        
//        // Nút nộp bài
//        view.submitBtn.addActionListener(e -> submitAssignment());
//    }
//
//    private void showDetail() {
//        Assignment a = view.assignmentList.getSelectedValue();
//        if (a == null) {
//            view.detailArea.setText("Chọn một bài tập để xem chi tiết.");
//            return;
//        }
//        
//        String info = String.format(
//            "Tên bài: %s\n" +
//            "Hạn nộp: %s\n" +
//            "----------------------------------\n" +
//            "Nội dung:\n%s\n" +
//            "----------------------------------\n" +
//            "Ngày giao: %s",
//            a.title, 
//            a.deadline.format(formatter), 
//            a.content, 
//            a.assignedDate.format(formatter)
//        );
//        view.detailArea.setText(info);
//    }
//
//    private void showView() {
//        SwingUtilities.invokeLater(() -> {
//            if (frame == null) {
//                frame = new JFrame("Bài tập về nhà");
//                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//                frame.setSize(700, 500);
//                frame.setLocationRelativeTo(null);
//                frame.setContentPane(new ExerciseView()); // OK! JPanel hợp lệ
//                }
//            frame.setVisible(true);
//            frame.toFront(); // Đưa lên trên cùng nếu đang bị che
//            
//            // Tự động yêu cầu server cập nhật danh sách bài tập khi mở app
//            clientNetwork.requestAssignments();
//        });
//    }
//
//    /* ================= NHẬN DỮ LIỆU TỪ SERVER ================= */
//    
//    private void initNetworkListeners() {
//        // Khi nhận được danh sách bài tập từ Server
//        ClientStates.setOnAssignmentListReceivedListener(assignments -> {
//            if (assignments == null) return;
//            
//            SwingUtilities.invokeLater(() -> {
//                // Duyệt qua danh sách bài tập nhận được
//                for (Assignment newAsg : assignments) {
//                    boolean exists = false;
//                    for (int i = 0; i < view.assignmentModel.size(); i++) {
//                        if (view.assignmentModel.get(i).id.equals(newAsg.id)) {
//                            view.assignmentModel.set(i, newAsg); // Cập nhật nếu đã có
//                            exists = true;
//                            break;
//                        }
//                    }
//                    if (!exists) view.assignmentModel.addElement(newAsg); // Thêm mới nếu chưa có
//                }
//                view.statusLabel.setText("Danh sách bài tập đã được cập nhật.");
//            });
//        });
//
//        // Khi nhận được phản hồi kết quả nộp bài
//        ClientStates.setOnSubmissionResultListener((success, msg) -> 
//            SwingUtilities.invokeLater(() -> {
//                view.submitBtn.setEnabled(true);
//                view.statusLabel.setText(success ? "Nộp bài thành công!" : "Lỗi: " + msg);
//                
//                JOptionPane.showMessageDialog(frame, msg, 
//                    success ? "Thông báo" : "Lỗi nộp bài", 
//                    success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
//            })
//        );
//    }
//
//    /* ================= GỬI DỮ LIỆU LÊN SERVER ================= */
//    
//    private void submitAssignment() {
//        Assignment a = view.assignmentList.getSelectedValue();
//        if (a == null) {
//            JOptionPane.showMessageDialog(frame, "Vui lòng chọn bài tập muốn nộp!");
//            return;
//        }
//
//        // Kiểm tra thời hạn
//        if (LocalDateTime.now().isAfter(a.deadline)) {
//            JOptionPane.showMessageDialog(frame, "Đã quá hạn nộp bài tập này!", "Hết hạn", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        // Nhập thông tin (Có thể lấy từ bộ nhớ MemoryBox để tự điền nếu muốn)
//        JTextField nameField = new JTextField();
//        JTextField idField = new JTextField();
//        Object[] message = {
//            "Họ và tên:", nameField,
//            "Mã số sinh viên:", idField
//        };
//
//        int option = JOptionPane.showConfirmDialog(frame, message, "Thông tin nộp bài", JOptionPane.OK_CANCEL_OPTION);
//        if (option != JOptionPane.OK_OPTION) return;
//
//        String studentName = nameField.getText().trim();
//        String studentId = idField.getText().trim();
//
//        if (studentName.isEmpty() || studentId.isEmpty()) {
//            JOptionPane.showMessageDialog(frame, "Thông tin không được để trống!");
//            return;
//        }
//
//        // Chọn file
//        JFileChooser chooser = new JFileChooser();
//        chooser.setDialogTitle("Chọn file bài làm của bạn");
//        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
//        
//        File file = chooser.getSelectedFile();
//        
//        // Thực hiện gửi trong Thread riêng để không làm treo UI khi đọc file lớn
//        view.submitBtn.setEnabled(false);
//        view.statusLabel.setText("Đang đọc file và gửi lên server...");
//
//        new Thread(() -> {
//            try {
//                Submission sub = new Submission();
//                sub.assignmentId = a.id;
//                sub.studentName = studentName;
//                sub.studentId = studentId;
//                sub.fileName = file.getName();
//                sub.fileData = Files.readAllBytes(file.toPath());
//                sub.submitTime = LocalDateTime.now();
//
//                clientNetwork.sendSubmission(sub);
//            } catch (Exception ex) {
//                ClientStates.fireSubmissionResult(false, "Không thể đọc file: " + ex.getMessage());
//            }
//        }).start();
//    }
//    
//    public void addAssignmentFromServer(Assignment a) {
//        // Kiểm tra xem bài tập đã có trong danh sách chưa (tránh trùng)
//        boolean exists = assignments.stream().anyMatch(existing -> existing.id.equals(a.id));
//        if (!exists) {
//            assignments.add(a);
//            if (view != null) {
//                SwingUtilities.invokeLater(() -> view.assignmentModel.addElement(a));
//            }
//        }
//    }
//}
package org.example.client.controller.services.exercise;

import org.example.client.ClientStates;
import org.example.client.controller.ClientNetwork;
import org.example.client.view.exercise.ExerciseView;
import org.example.common.objects.exercise.Assignment;
import org.example.common.objects.exercise.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExerciseController {
    private static final Logger log = LoggerFactory.getLogger(ExerciseController.class);
    private final ExerciseView view;
    private final ClientNetwork clientNetwork;
    private JFrame frame;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ExerciseController(ClientNetwork clientNetwork) {
        this.clientNetwork = clientNetwork;
        this.view = new ExerciseView(); // View khởi tạo sẵn
        
        log.info("[ExerciseController] Đang khởi tạo Controller...");
        initUIEvents();
        initNetworkListeners();
        
        // Đăng ký sự kiện mở giao diện từ Dashboard
        ClientStates.setOnShowExerciseListener(this::showView);
        log.info("[ExerciseController] Khởi tạo hoàn tất.");
    }

    /* ================= GIAO DIỆN (UI) ================= */
    
    private void initUIEvents() {
        // Lắng nghe chọn bài tập
        view.assignmentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetail();
        });
        
        // Nút nộp bài
        view.submitBtn.addActionListener(e -> {
            log.info("[UI] Người dùng nhấn nút Nộp bài.");
            submitAssignment();
        });
    }

    private void showDetail() {
        Assignment a = view.assignmentList.getSelectedValue();
        if (a == null) {
            view.detailArea.setText("Chọn một bài tập để xem chi tiết.");
            return;
        }
        log.info("[UI] Đang xem chi tiết bài tập: {}", a.title);
        
        String info = String.format(
            "Tên bài: %s\n" +
            "Hạn nộp: %s\n" +
            "----------------------------------\n" +
            "Nội dung:\n%s\n" +
            "----------------------------------\n" +
            "Ngày giao: %s",
            a.title, 
            a.deadline.format(formatter), 
            a.content, 
            a.assignedDate.format(formatter)
        );
        view.detailArea.setText(info);
    }

    private void showView() {
        SwingUtilities.invokeLater(() -> {
            log.info("[UI] Đang hiển thị cửa sổ Bài tập.");
            if (frame == null) {
                frame = new JFrame("Bài tập về nhà");
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setSize(800, 550);
                frame.setLocationRelativeTo(null);
                frame.setContentPane(view); // Sử dụng view đã khởi tạo ở Constructor
                log.info("[UI] Đã tạo Frame mới cho ExerciseView.");
            }
            frame.setVisible(true);
            frame.toFront();
            
            // Yêu cầu server cập nhật danh sách bài tập khi mở app
            view.statusLabel.setText("Đang yêu cầu danh sách bài tập từ Server...");
            clientNetwork.requestAssignments();
            log.info("[Network] Đã gửi REQUEST_ASSIGNMENTS tới Server.");
        });
    }

    /* ================= NHẬN DỮ LIỆU TỪ SERVER ================= */
    
    private void initNetworkListeners() {
        // Khi nhận được bài tập từ Server
        ClientStates.setOnAssignmentListReceivedListener(assignments -> {
            log.info("[Network] Nhận được gói tin ASSIGNMENT_LIST. Số lượng: {}", (assignments != null ? assignments.size() : 0));
            
            SwingUtilities.invokeLater(() -> {
                if (assignments == null || assignments.isEmpty()) {
                    view.statusLabel.setText("Hiện tại không có bài tập nào.");
                    return;
                }
                
                for (Assignment newAsg : assignments) {
                    addAssignmentToModel(newAsg);
                }
                view.statusLabel.setText("Đã cập nhật " + assignments.size() + " bài tập từ Server.");
            });
        });

//        // Khi nhận được phản hồi kết quả nộp bài
//        ClientStates.setOnSubmissionResultListener((success, msg) -> 
//            SwingUtilities.invokeLater(() -> {
//                log.info("[Network] Kết quả nộp bài: success={}, message={}", success, msg);
//                view.submitBtn.setEnabled(true);
//                view.statusLabel.setText(success ? "Nộp bài thành công!" : "Lỗi: " + msg);
//                
//                JOptionPane.showMessageDialog(frame, msg, 
//                    success ? "Thông báo" : "Lỗi nộp bài", 
//                    success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
//            })
//        );
        ClientStates.setOnSubmissionResultListener((success, msg) -> 
        SwingUtilities.invokeLater(() -> {
            view.submitBtn.setEnabled(true); // Mở khóa nút
            if (success) {
                view.statusLabel.setText("Đã nộp bài thành công!");
            } else {
                view.statusLabel.setText("Lỗi: " + msg);
            }
            JOptionPane.showMessageDialog(frame, msg, success ? "Thông báo" : "Lỗi", 
                success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        })
    );
    }

    /**
     * Helper cập nhật bài tập vào UI tránh trùng lặp
     */
    public void addAssignmentToModel(Assignment a) {
        if (a == null) return;
        boolean exists = false;
        for (int i = 0; i < view.assignmentModel.size(); i++) {
            if (view.assignmentModel.get(i).id.equals(a.id)) {
                view.assignmentModel.set(i, a);
                exists = true;
                log.info("[UI Model] Đã cập nhật lại bài tập cũ: {}", a.title);
                break;
            }
        }
        if (!exists) {
            view.assignmentModel.addElement(a);
            log.info("[UI Model] Đã thêm bài tập mới vào danh sách: {}", a.title);
        }
    }

    /* ================= GỬI DỮ LIỆU LÊN SERVER ================= */
    
    private void submitAssignment() {
        Assignment a = view.assignmentList.getSelectedValue();
        if (a == null) {
            log.warn("[UI] Cố gắng nộp bài khi chưa chọn bài tập.");
            JOptionPane.showMessageDialog(frame, "Vui lòng chọn bài tập muốn nộp!");
            return;
        }

        if (LocalDateTime.now().isAfter(a.deadline)) {
            log.warn("[UI] Bài tập '{}' đã quá hạn.", a.title);
            JOptionPane.showMessageDialog(frame, "Đã quá hạn nộp bài tập này!", "Hết hạn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Nhập thông tin sinh viên
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        Object[] message = { "Họ và tên:", nameField, "Mã số sinh viên:", idField };

        int option = JOptionPane.showConfirmDialog(frame, message, "Thông tin nộp bài", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return;

        String studentName = nameField.getText().trim();
        String studentId = idField.getText().trim();

        if (studentName.isEmpty() || studentId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Thông tin không được để trống!");
            return;
        }

        // Chọn file bài làm
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file bài làm (" + a.title + ")");
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            log.info("[UI] Người dùng hủy chọn file.");
            return;
        }
        
        File file = chooser.getSelectedFile();
        log.info("[UI] Đã chọn file: {}. Dung lượng: {} bytes", file.getName(), file.length());
        
        view.submitBtn.setEnabled(false);
        view.statusLabel.setText("Đang chuẩn bị gửi file: " + file.getName());

        new Thread(() -> {
            try {
                log.info("[System] Đang đọc dữ liệu file...");
                Submission sub = new Submission();
                sub.assignmentId = a.id;
                sub.studentName = studentName;
                sub.studentId = studentId;
                sub.fileName = file.getName();
                sub.fileData = Files.readAllBytes(file.toPath());
                sub.submitTime = LocalDateTime.now();

                log.info("[Network] Đang gửi Submission tới Server qua Socket...");
                clientNetwork.sendSubmission(sub);
            } catch (Exception ex) {
                log.error("[Error] Lỗi khi chuẩn bị bài nộp: ", ex);
                ClientStates.fireSubmissionResult(false, "Lỗi đọc file: " + ex.getMessage());
            }
        }).start();
    }
    
    
    
    
}