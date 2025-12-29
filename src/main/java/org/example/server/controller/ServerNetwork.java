package org.example.server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.common.objects.exercise.Assignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerNetwork {

    private final ServerSocket server;
    // Sử dụng List được synchronized để tránh lỗi xung đột khi nhiều client kết nối/ngắt kết nối cùng lúc
    public static List<ServerNetworkHandler> clients = Collections.synchronizedList(new ArrayList<>());

    private static final Logger log = LoggerFactory.getLogger(ServerNetwork.class);
    
    public ServerNetwork(ServerSocket server) {
        this.server = server;
    }
    
    public void startServer() {
        try {
            log.info("Server Socket đang lắng nghe tại cổng: " + server.getLocalPort());
            while (!server.isClosed()) {
                Socket clientSocket = server.accept();
                // Khởi tạo handler (Handler tự add mình vào list clients trong constructor)
                new ServerNetworkHandler(clientSocket);
            }
        } catch (IOException e) {
            log.error("Lỗi Server Socket: " + e.getMessage());
            closeServer();
        }
    }

    /**
     * GỬI BÀI TẬP MỚI CHO TẤT CẢ CLIENTS (Broadcast)
     * Hàm này sẽ được gọi từ ExerciseController khi giáo viên bấm nút "Giao bài"
     */
 // TRONG ServerNetwork.java
    public static void broadcastAssignment(Assignment a) {
        synchronized (clients) {
            for (ServerNetworkHandler client : clients) {
                try {
                    // Gọi phương thức speak_assignment trong handler của bạn
                    client.speak_assignment(a); 
                } catch (Exception e) {
                    log.error("Lỗi gửi bài tập tới client {}: {}", client.getClient_name(), e.getMessage());
                }
            }
        }
        log.info("Đã gửi bài tập '{}' tới {} sinh viên.", a.title, clients.size());
    }

    public void closeServer() {
        try {
            if (server != null) {
                server.close();
            }
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
    }
}