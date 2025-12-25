package org.example.server.controller.services.watch;

import org.example.server.view.watch.WatchPanel;
import org.example.server.view.watch.WatchView;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

public class WatchController {

    private final int port;
    private final WatchView view;
    // Map để quản lý Socket và Panel tương ứng của mỗi Client
    private final ConcurrentHashMap<Socket, WatchPanel> clientsMap = new ConcurrentHashMap<>();
    private static final int MAX_IMAGE_SIZE = 10_000_000; // Tăng lên 10MB cho an toàn với ảnh HD

    public WatchController(int port) {
        this.port = port;
        this.view = new WatchView();
        startServer();
    }

    /**
     * Hiển thị cửa sổ quan sát
     */
    public void showWatchView() {
        SwingUtilities.invokeLater(() -> {
            view.setVisible(true);
            view.toFront(); // Đưa cửa sổ lên trên cùng
        });
    }

    /**
     * Khởi động Server lắng nghe kết nối từ các Client
     */
    private void startServer() {
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("📡 Video Server đang lắng nghe tại cổng: " + port);

                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("✅ Client mới kết nối: " + clientSocket.getInetAddress());

                    // Tạo Panel hiển thị riêng cho Client này
                    WatchPanel panel = new WatchPanel();
                    // Lưu vào map trước khi chạy luồng xử lý
                    clientsMap.put(clientSocket, panel);
                    
                    // Cập nhật giao diện: Thêm panel vào khung hình
                    SwingUtilities.invokeLater(() -> view.addClientPanel(panel));

                    // Tạo luồng riêng để nhận dữ liệu hình ảnh từ Client này
                    Thread clientHandler = new Thread(() -> handleClient(clientSocket));
                    clientHandler.setDaemon(true); // Luồng sẽ tự tắt khi app chính đóng
                    clientHandler.start();
                }
            } catch (IOException e) {
                System.err.println("❌ Lỗi Server Socket: " + e.getMessage());
            }
        });
        serverThread.setName("Video-Server-Daemon");
        serverThread.start();
    }

    /**
     * Xử lý luồng dữ liệu hình ảnh từ từng Client
     */
    private void handleClient(Socket client) {
        WatchPanel panel = clientsMap.get(client);
        
        try (DataInputStream dis = new DataInputStream(client.getInputStream())) {
            while (!client.isClosed()) {
                // 1. Đọc kích thước file ảnh
                int size = dis.readInt();
                
                if (size <= 0 || size > MAX_IMAGE_SIZE) {
                    System.err.println("⚠️ Kích thước ảnh không hợp lệ: " + size);
                    break; 
                }

                // 2. Đọc mảng byte của ảnh
                byte[] buffer = new byte[size];
                dis.readFully(buffer);

                // 3. Giải mã ảnh (Thực hiện ở luồng phụ để không treo UI)
                try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer)) {
                    Image img = ImageIO.read(bais);
                    
                    if (img != null) {
                        // 4. Đẩy ảnh đã giải mã lên giao diện
                        SwingUtilities.invokeLater(() -> view.updateClientImage(panel, img));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("ℹ️ Client đã ngắt kết nối: " + client.getInetAddress());
        } finally {
            cleanupClient(client, panel);
        }
    }

    /**
     * Dọn dẹp tài nguyên khi client thoát
     */
    private void cleanupClient(Socket client, WatchPanel panel) {
        try {
            clientsMap.remove(client);
            if (client != null && !client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Xóa panel khỏi giao diện
        if (panel != null) {
            SwingUtilities.invokeLater(() -> view.removeClientPanel(panel));
        }
    }
}