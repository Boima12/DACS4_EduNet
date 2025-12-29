//package org.example.client.view.clientScreen;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.net.Socket;
//import java.util.Iterator;
//import javax.imageio.IIOImage;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageWriteParam;
//import javax.imageio.ImageWriter;
//import javax.imageio.stream.ImageOutputStream;
//import javax.swing.*;
//
//import org.example.client.controller.services.lock.LockController;
//import org.example.client.controller.services.whiteBoard.WhiteBoardController;
//import org.example.client.model.CoreClient;
//
//public class Client_Screen extends JFrame {
//
//    private JPanel contentPane;
//    public int id_port_watch;
//    public int id_port_whiteboard;
//    public int id_port_lock;
//
//    public Client_Screen(String serverIP, int serverPort) {
//    	
//        this.id_port_whiteboard = serverPort + 1;
//        this.id_port_watch = serverPort + 2; 
//        this.id_port_lock = serverPort + 3; 
//
//        // Cấu hình JFrame chính
//        setTitle("CLIENT - Quản lý học tập");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(600, 450);
//        setLocationRelativeTo(null);
//
//        // ===== XÂY DỰNG GIAO DIỆN (UI) =====
//        contentPane = new JPanel();
//        contentPane.setLayout(new BorderLayout(10, 10));
//        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        setContentPane(contentPane);
//
//        JLabel lblTitle = new JLabel("CLIENT CONTROL PANEL", SwingConstants.CENTER);
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        lblTitle.setForeground(new Color(33, 150, 243));
//        contentPane.add(lblTitle, BorderLayout.NORTH);
//
//        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
//        contentPane.add(centerPanel, BorderLayout.CENTER);
//
//        // Message Panel
//        JPanel msgPanel = createPanel("GỬI TIN NHẮN");
//        msgPanel.add(createButton("Gửi tin nhắn cho All Client"));
//        msgPanel.add(createButton("Gửi tin nhắn cho Server"));
//
//        // Video Panel
//        JPanel videoPanel = createPanel("CALL VIDEO");
//        videoPanel.add(createButton("Call Video Client"));
//        videoPanel.add(createButton("Call Video Server"));
//
//        // Tool Panel
//        JPanel toolPanel = createPanel("CÔNG CỤ");
//        JButton btnWhiteboard = createButton("Bảng trắng");
//        btnWhiteboard.addActionListener(e -> {
//            try {
//                new WhiteBoardController(serverIP, id_port_whiteboard);
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this, "Không thể kết nối tới server WhiteBoard!");
//            }
//        });
//        toolPanel.add(btnWhiteboard);
//       // .btnWhiteboard..add(createButton("Nộp bài"));
//        
//        JButton butonnopbai = createButton("Nộp bài");
//        butonnopbai.addActionListener(e -> {
//        	new CoreClient().start();
//        });
//        toolPanel.add(butonnopbai);
//// new CoreClient().start();
//        centerPanel.add(msgPanel);
//        centerPanel.add(videoPanel);
//        centerPanel.add(toolPanel);
//
//        // Hiển thị giao diện trước
//        this.setVisible(true);
//
//        // Chạy luồng gửi Video trong Thread riêng để KHÔNG làm đơ UI
//        new Thread(() -> {
//            onWatchVideo(serverIP, id_port_watch);
//        }).start();
//
//        
//        LockController lockController = new LockController(serverIP, id_port_lock);
////
////        // Cleanup
//        addWindowListener(new java.awt.event.WindowAdapter() {
//            @Override
//            public void windowClosed(java.awt.event.WindowEvent e) {
//                if (lockController != null) {
//                    lockController.shutdown();
//                }
//                System.exit(0);
//            }
//        });
//    }
//
//    private JPanel createPanel(String title) {
//        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
//        panel.setBorder(BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)), title,
//                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 14), new Color(80, 80, 80)));
//        return panel;
//    }
//
//    private JButton createButton(String text) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        btn.setFocusPainted(false);
//        btn.setBackground(new Color(240, 240, 240));
//        return btn;
//    }
//
//    public void onWatchVideo(String serverIP, int port) {
//        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//        
//        while (true) {
//            System.out.println("🔄 Đang thử kết nối tới Server Video: " + serverIP + ":" + port);
//            try (Socket socket = new Socket(serverIP, port);
//                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
//                
//                System.out.println("✅ Đã kết nối thành công tới Server Video!");
//                Robot robot = new Robot();
//                
//                // Chuẩn bị ImageWriter một lần duy nhất để tối ưu
//                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
//                if (!writers.hasNext()) throw new IllegalStateException("Không tìm thấy JPG writer");
//                ImageWriter writer = writers.next();
//                
//                ImageWriteParam param = writer.getDefaultWriteParam();
//                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//                param.setCompressionQuality(0.6f); // Giảm nhẹ chất lượng để tăng tốc độ truyền
//
//                while (!socket.isClosed()) {
//                    // 1. Chụp màn hình
//                    BufferedImage screen = robot.createScreenCapture(screenRect);
//
//                    // 2. Resize ảnh (1280x720)
//                    BufferedImage resized = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
//                    Graphics2D g = resized.createGraphics();
//                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                    g.drawImage(screen, 0, 0, 1280, 720, null);
//                    g.dispose();
//
//                    // 3. Nén JPEG vào bộ nhớ đệm
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
//                        writer.setOutput(ios);
//                        writer.write(null, new IIOImage(resized, null, null), param);
//                    }
//
//                    // 4. Gửi dữ liệu qua Socket
//                    byte[] data = baos.toByteArray();
//                    dos.writeInt(data.length);
//                    dos.write(data);
//                    dos.flush();
//
//                    // Giữ khoảng 15 FPS (66ms) để cân bằng giữa độ mượt và băng thông mạng
//                    Thread.sleep(66); 
//                }
//                writer.dispose();
//                
//            } catch (Exception e) {
//                System.err.println("❌ Lỗi kết nối Video: " + e.getMessage());
//                try { Thread.sleep(3000); } catch (InterruptedException ex) {}
//            }
//        }
//    }
//}


//package org.example.client.view.clientScreen;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.net.Socket;
//import java.util.Iterator;
//import javax.imageio.IIOImage;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageWriteParam;
//import javax.imageio.ImageWriter;
//import javax.imageio.stream.ImageOutputStream;
//import javax.swing.*;
//
//import org.example.client.ClientStates;
//import org.example.client.controller.services.lock.LockController;
//import org.example.client.controller.services.whiteBoard.WhiteBoardController;
//import org.example.client.model.CoreClient;
//import org.example.client.view.exercise.ExerciseView;

//package org.example.client.view.clientScreen;
//
//import java.awt.*;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferInt;
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.util.Iterator;
//import javax.imageio.IIOImage;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageWriteParam;
//import javax.imageio.ImageWriter;
//import javax.imageio.stream.ImageOutputStream;
//import javax.swing.*;
//import org.example.client.ClientStates;  // SỬA: Import ClientStates để fire event
//import org.example.client.controller.services.lock.LockController;  // SỬA: Import LockController (giả sử package đúng)
//import org.example.client.controller.services.whiteBoard.WhiteBoardController;  // SỬA: Import WhiteBoardController
//
//public class Client_Screen extends JFrame {
//    private JPanel contentPane;
//    public int id_port_watch;
//    public int id_port_whiteboard;
//    public int id_port_lock;
//    // private ExerciseView exerciseView; // panel quản lý assignment (không cần nữa, dùng fire event)
//    private volatile boolean videoThreadRunning = true; // để dừng luồng video khi đóng cửa sổ
//
//    public Client_Screen(String serverIP, int serverPort) {
//        this.id_port_whiteboard = serverPort + 1;
//        this.id_port_watch = serverPort + 2;
//        this.id_port_lock = serverPort + 3;
//
//        // Cấu hình JFrame chính
//        setTitle("CLIENT - Quản lý học tập");
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setSize(800, 600);
//        setLocationRelativeTo(null);
//
//        contentPane = new JPanel(new BorderLayout(10, 10));
//        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        setContentPane(contentPane);
//
//        JLabel lblTitle = new JLabel("CLIENT CONTROL PANEL", SwingConstants.CENTER);
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        lblTitle.setForeground(new Color(33, 150, 243));
//        contentPane.add(lblTitle, BorderLayout.NORTH);
//
//        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
//        contentPane.add(centerPanel, BorderLayout.CENTER);
//
//        // Message Panel
//        JPanel msgPanel = createPanel("GỬI TIN NHẮN");
//        msgPanel.add(createButton("Gửi tin nhắn cho All Client"));
//        msgPanel.add(createButton("Gửi tin nhắn cho Server"));
//
//        // Video Panel
//        JPanel videoPanel = createPanel("CALL VIDEO");
//        videoPanel.add(createButton("Call Video Client"));
//        videoPanel.add(createButton("Call Video Server"));
//
//        // Tool Panel
//        JPanel toolPanel = createPanel("CÔNG CỤ");
//        JButton btnWhiteboard = createButton("Bảng trắng");
//        btnWhiteboard.addActionListener(e -> {
//            try {
//                new WhiteBoardController(serverIP, id_port_whiteboard);
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this, "Không thể kết nối tới server WhiteBoard!");
//            }
//        });
//        toolPanel.add(btnWhiteboard);
//
//        // Panel quản lý Exercise / Submit
//        //exerciseView = new ExerciseView();  // SỬA: Không cần khởi tạo trực tiếp, dùng fire event
//        JButton btnSubmit = createButton("Nộp bài");
//        btnSubmit.addActionListener(e -> {
//            ClientStates.fireShowExerciseView();  // SỬA: Giữ nguyên, fire event để show ExerciseView từ ExerciseController
//        });
//
//        toolPanel.add(btnSubmit);
//
//        centerPanel.add(msgPanel);
//        centerPanel.add(videoPanel);
//        centerPanel.add(toolPanel);
//
//        this.setVisible(true);
//
//        // Luồng video
//        new Thread(() -> onWatchVideo(serverIP, id_port_watch)).start();
//
//        LockController lockController = new LockController(serverIP, id_port_lock);  // SỬA: Giả sử constructor đúng
//
//        addWindowListener(new WindowAdapter() {  // SỬA: Sử dụng WindowAdapter thay vì anonymous WindowAdapter
//            @Override
//            public void windowClosing(WindowEvent e) {
//                videoThreadRunning = false;
//                if (lockController != null) lockController.shutdown();
//            }
//
//            @Override
//            public void windowClosed(WindowEvent e) {
//                System.exit(0);
//            }
//        });
//    }
//
//    private JPanel createPanel(String title) {
//        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
//        panel.setBorder(BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)), title,
//                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 14), new Color(80, 80, 80)));
//        return panel;
//    }
//
//    private JButton createButton(String text) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        btn.setFocusPainted(false);
//        btn.setBackground(new Color(240, 240, 240));
//        return btn;
//    }
//
//    // SỬA: Đổi tên method thành private để encapsulate (không public nếu không cần)
//    private void onWatchVideo(String serverIP, int port) {
//        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//        while (videoThreadRunning) {
//            try (Socket socket = new Socket(serverIP, port);
//                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
//
//                Robot robot = new Robot();
//                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
//                if (!writers.hasNext()) throw new IllegalStateException("Không tìm thấy JPG writer");
//                ImageWriter writer = writers.next();
//                ImageWriteParam param = writer.getDefaultWriteParam();
//                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//                param.setCompressionQuality(0.6f);
//
//                while (!socket.isClosed() && videoThreadRunning) {
//                    BufferedImage screen = robot.createScreenCapture(screenRect);
//                    BufferedImage resized = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
//                    Graphics2D g = resized.createGraphics();
//                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                    g.drawImage(screen, 0, 0, 1280, 720, null);
//                    g.dispose();
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
//                        writer.setOutput(ios);
//                        writer.write(null, new IIOImage(resized, null, null), param);
//                    }
//
//                    byte[] data = baos.toByteArray();
//                    dos.writeInt(data.length);
//                    dos.write(data);
//                    dos.flush();
//
//                    Thread.sleep(66); // ~15 FPS
//                }
//                writer.dispose();
//            } catch (Exception e) {
//                // SỬA: Log lỗi nếu cần (thêm import slf4j nếu dùng Logger)
//                System.err.println("Video thread error: " + e.getMessage());  // Hoặc dùng Logger
//                try { Thread.sleep(3000); } catch (InterruptedException ex) {}
//            }
//        }
//    }
//}
//public class Client_Screen extends JFrame {
//
//    private JPanel contentPane;
//    public int id_port_watch;
//    public int id_port_whiteboard;
//    public int id_port_lock;
//   // private ExerciseView exerciseView; // panel quản lý assignment
//    private volatile boolean videoThreadRunning = true; // để dừng luồng video khi đóng cửa sổ
//
//    public Client_Screen(String serverIP, int serverPort) {
//        this.id_port_whiteboard = serverPort + 1;
//        this.id_port_watch = serverPort + 2; 
//        this.id_port_lock = serverPort + 3; 
//
//        // Cấu hình JFrame chính
//        setTitle("CLIENT - Quản lý học tập");
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setSize(800, 600);
//        setLocationRelativeTo(null);
//
//        contentPane = new JPanel(new BorderLayout(10, 10));
//        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        setContentPane(contentPane);
//
//        JLabel lblTitle = new JLabel("CLIENT CONTROL PANEL", SwingConstants.CENTER);
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        lblTitle.setForeground(new Color(33, 150, 243));
//        contentPane.add(lblTitle, BorderLayout.NORTH);
//
//        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
//        contentPane.add(centerPanel, BorderLayout.CENTER);
//
//        // Message Panel
//        JPanel msgPanel = createPanel("GỬI TIN NHẮN");
//        msgPanel.add(createButton("Gửi tin nhắn cho All Client"));
//        msgPanel.add(createButton("Gửi tin nhắn cho Server"));
//
//        // Video Panel
//        JPanel videoPanel = createPanel("CALL VIDEO");
//        videoPanel.add(createButton("Call Video Client"));
//        videoPanel.add(createButton("Call Video Server"));
//
//        // Tool Panel
//        JPanel toolPanel = createPanel("CÔNG CỤ");
//        JButton btnWhiteboard = createButton("Bảng trắng");
//        btnWhiteboard.addActionListener(e -> {
//            try {
//                new WhiteBoardController(serverIP, id_port_whiteboard);
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this, "Không thể kết nối tới server WhiteBoard!");
//            }
//        });
//        toolPanel.add(btnWhiteboard);
//
//        // Panel quản lý Exercise / Submit
//        //exerciseView = new ExerciseView();
//        JButton btnSubmit = createButton("Nộp bài");
////        btnSubmit.addActionListener(e -> {
////            // Hiển thị panel ExerciseView trong JFrame nếu muốn
////            if (!exerciseView.isVisible()) {
////                JFrame frame = new JFrame("Quản lý bài tập");
////                frame.setSize(600, 400);
////                frame.setLocationRelativeTo(this);
////                frame.add(exerciseView);
////                frame.setVisible(true);
////            }
////        });
////        btnSubmit.addActionListener(e -> {
////            JFrame frame = new JFrame("Quản lý bài tập");
////            frame.setSize(600, 400);
////            frame.setLocationRelativeTo(this);
////            frame.setContentPane(exerciseView); // giờ exerciseView là JPanel
////            frame.setVisible(true);
////        });
//        
////        btnSubmit.addActionListener(e -> {
////            // Hiển thị panel ExerciseView trong JFrame nếu muốn
////            if (!exerciseView.isVisible()) {
////                JFrame frame = new JFrame("Quản lý bài tập");
////                frame.setSize(600, 400);
////                frame.setLocationRelativeTo(this);
////                frame.add(exerciseView); // <-- đây là nguyên nhân lỗi
////                frame.setVisible(true);
////            }
////        });
//        btnSubmit.addActionListener(e -> {
//        	
//            ClientStates.fireShowExerciseView();
//            
//            //exerciseView.isVisible();
//        });
//
//
//        toolPanel.add(btnSubmit);
//
//        centerPanel.add(msgPanel);
//        centerPanel.add(videoPanel);
//        centerPanel.add(toolPanel);
//
//        this.setVisible(true);
//
//        // Luồng video
//        new Thread(() -> onWatchVideo(serverIP, id_port_watch)).start();
//
//        LockController lockController = new LockController(serverIP, id_port_lock);
//
//        addWindowListener(new java.awt.event.WindowAdapter() {
//            @Override
//            public void windowClosing(java.awt.event.WindowEvent e) {
//                videoThreadRunning = false;
//                if (lockController != null) lockController.shutdown();
//            }
//
//            @Override
//            public void windowClosed(java.awt.event.WindowEvent e) {
//                System.exit(0);
//            }
//        });
//    }
//
//    private JPanel createPanel(String title) {
//        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
//        panel.setBorder(BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)), title,
//                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 14), new Color(80, 80, 80)));
//        return panel;
//    }
//
//    private JButton createButton(String text) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        btn.setFocusPainted(false);
//        btn.setBackground(new Color(240, 240, 240));
//        return btn;
//    }
//
//   // p/
//
//    public void onWatchVideo(String serverIP, int port) {
//        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//        while (videoThreadRunning) {
//            try (Socket socket = new Socket(serverIP, port);
//                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
//
//                Robot robot = new Robot();
//                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
//                if (!writers.hasNext()) throw new IllegalStateException("Không tìm thấy JPG writer");
//                ImageWriter writer = writers.next();
//                ImageWriteParam param = writer.getDefaultWriteParam();
//                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//                param.setCompressionQuality(0.6f);
//
//                while (!socket.isClosed() && videoThreadRunning) {
//                    BufferedImage screen = robot.createScreenCapture(screenRect);
//                    BufferedImage resized = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
//                    Graphics2D g = resized.createGraphics();
//                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                    g.drawImage(screen, 0, 0, 1280, 720, null);
//                    g.dispose();
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
//                        writer.setOutput(ios);
//                        writer.write(null, new IIOImage(resized, null, null), param);
//                    }
//
//                    byte[] data = baos.toByteArray();
//                    dos.writeInt(data.length);
//                    dos.write(data);
//                    dos.flush();
//
//                    Thread.sleep(66); // ~15 FPS
//                }
//                writer.dispose();
//            } catch (Exception e) {
//                try { Thread.sleep(3000); } catch (InterruptedException ex) {}
//            }
//        }
//    }
//}

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
