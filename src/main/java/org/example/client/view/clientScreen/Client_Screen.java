package org.example.client.view.clientScreen;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;

import org.example.client.ClientStates;
import org.example.client.controller.services.lock.LockController;
import org.example.client.controller.services.whiteBoard.WhiteBoardController;

public class Client_Screen extends JFrame {
    private JPanel contentPane;
    public int id_port_watch;
    public int id_port_whiteboard;
    public int id_port_lock;
    private volatile boolean videoThreadRunning = true;

    public Client_Screen(String serverIP, int serverPort) {
        this.id_port_whiteboard = serverPort + 1;
        this.id_port_watch = serverPort + 2;
        this.id_port_lock = serverPort + 3;

        setupUI(serverIP);

        // Khởi động luồng truyền màn hình (Watch)
        startVideoThread(serverIP, id_port_watch);

        // Khởi tạo bộ điều khiển khóa màn hình
        LockController lockController = new LockController(serverIP, id_port_lock);

        // Xử lý đóng cửa sổ
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                videoThreadRunning = false; // Ngắt luồng capture
                if (lockController != null) lockController.shutdown();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void setupUI(String serverIP) {
        setTitle("CLIENT - Hệ thống quản lý lớp học");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        // Tiêu đề
        JLabel lblTitle = new JLabel("BẢNG ĐIỀU KHIỂN SINH VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(41, 128, 185));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        // Panel trung tâm chứa các nhóm chức năng
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        centerPanel.setBackground(Color.WHITE);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // 1. Nhóm Tin nhắn
        JPanel msgPanel = createGroupPanel("TRAO ĐỔI");
        msgPanel.add(createStyledButton("Gửi tin cho lớp", new Color(52, 152, 219)));
        msgPanel.add(createStyledButton("Chat với giáo viên", new Color(52, 152, 219)));

        // 2. Nhóm Video
        JPanel videoPanel = createGroupPanel("TRỰC TUYẾN");
        videoPanel.add(createStyledButton("Video Call Nhóm", new Color(46, 204, 113)));
        videoPanel.add(createStyledButton("Video Call Giáo viên", new Color(46, 204, 113)));

        // 3. Nhóm Công cụ & Bài tập
        JPanel toolPanel = createGroupPanel("HỌC TẬP");

        // Nút Bảng trắng
        JButton btnWhiteboard = createStyledButton("Bảng trắng", new Color(155, 89, 182));
        btnWhiteboard.addActionListener(e -> {
            try {
                new WhiteBoardController(serverIP, id_port_whiteboard);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể kết nối Bảng trắng!");
            }
        });

        // NÚT NỘP BÀI - ĐIỂM QUAN TRỌNG
        JButton btnSubmit = createStyledButton("Nộp bài tập", new Color(231, 76, 60));
        btnSubmit.addActionListener(e -> {
            // Chỉ gửi tín hiệu yêu cầu hiển thị.
            // ExerciseController (đã khởi tạo ở CoreClient) sẽ bắt được và show View.
            ClientStates.fireShowExerciseView();
        });

        toolPanel.add(btnWhiteboard);
        toolPanel.add(btnSubmit);

        centerPanel.add(msgPanel);
        centerPanel.add(videoPanel);
        centerPanel.add(toolPanel);
    }

    private JPanel createGroupPanel(String title) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 2), title,
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(127, 140, 141)));
        return panel;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }

    private void startVideoThread(String serverIP, int port) {
        Thread t = new Thread(() -> onWatchVideo(serverIP, port));
        t.setDaemon(true);
        t.start();
    }

    private void onWatchVideo(String serverIP, int port) {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        Robot robot;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            return;
        }

        while (videoThreadRunning) {
            try (Socket socket = new Socket(serverIP, port);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                ImageWriter writer = writers.next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.5f); // Giảm chất lượng một chút để tăng tốc độ truyền

                while (!socket.isClosed() && videoThreadRunning) {
                    BufferedImage screen = robot.createScreenCapture(screenRect);

                    // Resize để giảm băng thông (Quan trọng cho truyền mượt)
                    BufferedImage resized = new BufferedImage(1024, 576, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = resized.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(screen, 0, 0, 1024, 576, null);
                    g.dispose();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                        writer.setOutput(ios);
                        writer.write(null, new IIOImage(resized, null, null), param);
                    }

                    byte[] data = baos.toByteArray();
                    dos.writeInt(data.length);
                    dos.write(data);
                    dos.flush();

                    Thread.sleep(100); // ~10 FPS là đủ để quan sát
                }
                writer.dispose();
            } catch (Exception e) {
                // Thử kết nối lại sau 5 giây nếu rớt mạng
                try { Thread.sleep(5000); } catch (InterruptedException ex) {}
            }
        }
    }
}