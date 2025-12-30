package org.example.server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.common.objects.services.exercise.Assignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File Network của Server
 * File này là ServerSocket chính, tạo các thread ServerNetworkHandler để xử lý từng client một.
 * Code xử lý network cho bên client nằm ở ServerNetworkHandler.java
 *
 */
public class ServerNetwork {

	private final ServerSocket server;
//    public static ArrayList<ServerNetworkHandler> clients = new ArrayList<>();
    public static List<ServerNetworkHandler> clients = Collections.synchronizedList(new ArrayList<>());

    private static final Logger log = LoggerFactory.getLogger(ServerNetwork.class);
	
	public ServerNetwork(ServerSocket server) {
		this.server = server;
	}
	
	public void startServer() {
        try {
            while (!server.isClosed()) {
                // accept client when connect to the server
                Socket clientSocket = server.accept();
                new ServerNetworkHandler(clientSocket);
            }
        } catch (IOException e) {
            log.error(String.valueOf(e));
            closeServer();
        }
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
}
