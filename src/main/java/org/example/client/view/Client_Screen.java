//package org.example.client.view;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.net.Socket;
//import javax.imageio.IIOImage;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageWriteParam;
//import javax.imageio.ImageWriter;
//import javax.imageio.stream.ImageOutputStream;
//import javax.swing.*;
//
//import org.example.client.controller.WhiteBoardController;
//
//public class Client_Screen extends JFrame {
//
//    private JPanel contentPane;
//	public int id_port_watch;
//	public int id_port_whiteboard;
//
//    public Client_Screen(String serverIP, int serverPort) {
//    	this.id_port_whiteboard = serverPort + 1;
//    	this.id_port_watch = serverPort + 2; 
//    	
//        setTitle("CLIENT - Quản lý học tập");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(600, 450);
//        setLocationRelativeTo(null);
//        
//        onWatchVideo(serverIP, id_port_watch);
//
//        contentPane = new JPanel();
//        contentPane.setLayout(new BorderLayout(10, 10));
//        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        setContentPane(contentPane);
//
//        // ===== TITLE =====
//        JLabel lblTitle = new JLabel("CLIENT CONTROL PANEL", SwingConstants.CENTER);
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        lblTitle.setForeground(new Color(33, 150, 243));
//        contentPane.add(lblTitle, BorderLayout.NORTH);
//
//        // ===== CENTER PANEL =====
//        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
//        contentPane.add(centerPanel, BorderLayout.CENTER);
//
//        // ===== MESSAGE PANEL =====
//        JPanel msgPanel = createPanel("GỬI TIN NHẮN");
//        JButton btnMsgAll = createButton("Gửi tin nhắn cho All Client");
//        JButton btnMsgServer = createButton("Gửi tin nhắn cho Server");
//        msgPanel.add(btnMsgAll);
//        msgPanel.add(btnMsgServer);
//
//        // ===== VIDEO PANEL =====
//        JPanel videoPanel = createPanel("CALL VIDEO");
//        JButton btnCallClient = createButton("Call Video Client");
//        JButton btnCallServer = createButton("Call Video Server");
//        videoPanel.add(btnCallClient);
//        videoPanel.add(btnCallServer);
//
//        // ===== TOOL PANEL =====
//        JPanel toolPanel = createPanel("CÔNG CỤ");
//        JButton btnWhiteboard = createButton("Bảng trắng");
//        JButton btnSubmit = createButton("Nộp bài");
//
//        // Khi nhấn nút "Bảng trắng", mở WhiteBoard client và kết nối server
//        btnWhiteboard.addActionListener(e -> {
//            try {
//                new WhiteBoardController(serverIP, id_port_whiteboard); // kết nối tới WhiteBoard server
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(this,
//                        "Không thể kết nối tới server WhiteBoard!",
//                        "Lỗi kết nối",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        toolPanel.add(btnWhiteboard);
//        toolPanel.add(btnSubmit);
//
//        // ADD TO CENTER
//        centerPanel.add(msgPanel);
//        centerPanel.add(videoPanel);
//        centerPanel.add(toolPanel);
//        
//        this.setVisible(true);
//        
//        new Thread(() -> {
//            onWatchVideo(serverIP, id_port_watch);
//        }).start();
//        
//        
//    }
//
//    // ===== PANEL TEMPLATE =====
//    private JPanel createPanel(String title) {
//        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
//        panel.setBorder(BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                title,
//                javax.swing.border.TitledBorder.LEFT,
//                javax.swing.border.TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 14),
//                new Color(80, 80, 80)
//        ));
//        return panel;
//    }
//
//    // ===== BUTTON TEMPLATE =====
//    private JButton createButton(String text) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        btn.setFocusPainted(false);
//        btn.setBackground(new Color(240, 240, 240));
//        return btn;
//    }
//    
//    public void onWatchVideo(String serverIP, int id_port_watch) {
//    	// Thêm cơ chế retry nếu server chưa mở
//        while (true) {
//            try (Socket socket = new Socket(serverIP, id_port_watch);
//                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
//                
//                Robot robot = new Robot();
//                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//
//                while (!socket.isClosed()) {
//                   BufferedImage screen = robot.createScreenCapture(screenRect);
//
//                   BufferedImage resized = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
//
//                   Graphics2D g = resized.createGraphics();
//                   g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//                   g.drawImage(screen, 0, 0, 1280, 720, null);
//                   g.dispose();
//
//                   // ===== NÉN JPEG =====
//                   ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                   ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
//
//                   ImageWriteParam param = writer.getDefaultWriteParam();
//                   param.setCompressionMode(
//                           ImageWriteParam.MODE_EXPLICIT);
//                   param.setCompressionQuality(0.7f);
//
//                   ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
//                   writer.setOutput(ios);
//                   writer.write(null, new IIOImage(resized, null, null), param);
//
//                   ios.close();
//                   writer.dispose();
//
//                   byte[] data = baos.toByteArray();
//
//                   dos.writeInt(data.length);
//                   dos.write(data);
//                   dos.flush();
//                   Thread.sleep(60); // Giảm xuống ~10 FPS để ổn định băng thông
//               }
//           } catch (Exception e) {
//               System.out.println("Đang thử kết nối lại tới Server Video...");
//               try { Thread.sleep(3000); } catch (InterruptedException ex) {} 
//           }
//        }
//    }
//      
//    
//}
package org.example.client.view;

import java.awt.*;
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

import org.example.client.controller.WhiteBoardController;

public class Client_Screen extends JFrame {

    private JPanel contentPane;
    public int id_port_watch;
    public int id_port_whiteboard;

    public Client_Screen(String serverIP, int serverPort) {
        this.id_port_whiteboard = serverPort + 1;
        this.id_port_watch = serverPort + 2; 
        
        // Cấu hình JFrame chính
        setTitle("CLIENT - Quản lý học tập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        // ===== XÂY DỰNG GIAO DIỆN (UI) =====
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("CLIENT CONTROL PANEL", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(33, 150, 243));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Message Panel
        JPanel msgPanel = createPanel("GỬI TIN NHẮN");
        msgPanel.add(createButton("Gửi tin nhắn cho All Client"));
        msgPanel.add(createButton("Gửi tin nhắn cho Server"));

        // Video Panel
        JPanel videoPanel = createPanel("CALL VIDEO");
        videoPanel.add(createButton("Call Video Client"));
        videoPanel.add(createButton("Call Video Server"));

        // Tool Panel
        JPanel toolPanel = createPanel("CÔNG CỤ");
        JButton btnWhiteboard = createButton("Bảng trắng");
        btnWhiteboard.addActionListener(e -> {
            try {
                new WhiteBoardController(serverIP, id_port_whiteboard);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối tới server WhiteBoard!");
            }
        });
        toolPanel.add(btnWhiteboard);
        toolPanel.add(createButton("Nộp bài"));

        centerPanel.add(msgPanel);
        centerPanel.add(videoPanel);
        centerPanel.add(toolPanel);

        // Hiển thị giao diện trước
        this.setVisible(true);

        // Chạy luồng gửi Video trong Thread riêng để KHÔNG làm đơ UI
        new Thread(() -> {
            onWatchVideo(serverIP, id_port_watch);
        }).start();
    }

    private JPanel createPanel(String title) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), title,
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(80, 80, 80)));
        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(240, 240, 240));
        return btn;
    }

    public void onWatchVideo(String serverIP, int port) {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        
        while (true) {
            System.out.println("🔄 Đang thử kết nối tới Server Video: " + serverIP + ":" + port);
            try (Socket socket = new Socket(serverIP, port);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                
                System.out.println("✅ Đã kết nối thành công tới Server Video!");
                Robot robot = new Robot();
                
                // Chuẩn bị ImageWriter một lần duy nhất để tối ưu
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                if (!writers.hasNext()) throw new IllegalStateException("Không tìm thấy JPG writer");
                ImageWriter writer = writers.next();
                
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.6f); // Giảm nhẹ chất lượng để tăng tốc độ truyền

                while (!socket.isClosed()) {
                    // 1. Chụp màn hình
                    BufferedImage screen = robot.createScreenCapture(screenRect);

                    // 2. Resize ảnh (1280x720)
                    BufferedImage resized = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = resized.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(screen, 0, 0, 1280, 720, null);
                    g.dispose();

                    // 3. Nén JPEG vào bộ nhớ đệm
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                        writer.setOutput(ios);
                        writer.write(null, new IIOImage(resized, null, null), param);
                    }

                    // 4. Gửi dữ liệu qua Socket
                    byte[] data = baos.toByteArray();
                    dos.writeInt(data.length);
                    dos.write(data);
                    dos.flush();

                    // Giữ khoảng 15 FPS (66ms) để cân bằng giữa độ mượt và băng thông mạng
                    Thread.sleep(66); 
                }
                writer.dispose();
                
            } catch (Exception e) {
                System.err.println("❌ Lỗi kết nối Video: " + e.getMessage());
                try { Thread.sleep(3000); } catch (InterruptedException ex) {}
            }
        }
    }
}