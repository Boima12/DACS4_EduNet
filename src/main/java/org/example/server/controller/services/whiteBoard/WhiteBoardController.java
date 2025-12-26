//package org.example.server.controller;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.*;
//import org.example.common.objects.whiteBoard.WhiteboardPacket;
//import org.example.common.objects.whiteBoard.WhiteboardCommand;
//import org.example.server.view.whiteboard.WhiteBoardView;
//
//public class WhiteBoardController {
//
//    private static List<ObjectOutputStream> clients = new ArrayList<>();
//    private static WhiteBoardView serverView;
//
//    public static void main(String[] args) throws Exception {
//        ServerSocket ss = new ServerSocket(9999);
//
//        // Tạo GUI Server
//        serverView = new WhiteBoardView(packet -> {
//            broadcast(packet);
//            serverView.applyPacket(packet); // SERVER tự vẽ
//        });
//
//        while (true) {
//            Socket s = ss.accept();
//            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
//            
//            // Gửi toàn bộ lịch sử hiện tại cho client mới
//            for (WhiteboardPacket p : serverView.getHistory()) {
//                WhiteboardPacket syncPacket = new WhiteboardPacket(p.x, p.y, p.color);
//                syncPacket.command = WhiteboardCommand.SYNC;
//                out.writeObject(syncPacket);
//            }
//            out.flush();
//
//            clients.add(out);
//
//            // Bắt đầu lắng nghe client
//            new Thread(() -> listen(s)).start();
//        }
//    }
//
//    private static void listen(Socket s) {
//        try {
//            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
//            while (true) {
//                WhiteboardPacket p = (WhiteboardPacket) in.readObject();
//                broadcast(p);
//                serverView.applyPacket(p); // CLIENT vẽ -> SERVER hiển thị
//            }
//        } catch (Exception e) {
//            System.out.println("Client disconnected.");
//        }
//    }
//
//    private static void broadcast(WhiteboardPacket p) {
//        for (ObjectOutputStream out : clients) {
//            try {
//                out.writeObject(p);
//                out.flush();
//            } catch (Exception ignored) {}
//        }
//    }
//    
//    public WhiteBoardController(boolean isServerViewOnly) {
//        if (isServerViewOnly) {
//            serverView = new WhiteBoardView(packet -> {
//                broadcast(packet);   // gửi tới tất cả client
//                serverView.applyPacket(packet); // server tự vẽ
//            });
//        }
//    }
//
//}

package org.example.server.controller.services.whiteBoard;

import org.example.common.objects.whiteBoard.WhiteboardPacket;
import org.example.common.objects.whiteBoard.WhiteboardCommand;
import org.example.server.view.whiteBoard.WhiteBoardView;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WhiteBoardController - Server WhiteBoard chạy trên LAN
 */
public class WhiteBoardController {

    private ServerSocket serverSocket;
    private List<ObjectOutputStream> clients = new CopyOnWriteArrayList<>();
    private WhiteBoardView serverView;
    private int port = 9999; // port mặc định

    /**
     * Khởi tạo server WhiteBoard
     */
    public WhiteBoardController(int port) {
    	this.port = port;
        // Khởi tạo serverView ngay lập tức
        serverView = new WhiteBoardView(packet -> {
            broadcast(packet);        // gửi packet tới tất cả client
            serverView.applyPacket(packet); // server tự vẽ
        });

        // Tạo một thread lắng nghe client
        new Thread(() -> startServer(port)).start();
    }

    /**
     * Khởi tạo GUI WhiteBoard server
     */
    private void initGUI() {
        serverView = new WhiteBoardView(packet -> {
            broadcast(packet);       // gửi packet đến tất cả client
            serverView.applyPacket(packet); // server tự vẽ
        });
    }

    /**
     * Khởi động server và lắng nghe client trên LAN
     */
    private void startServer(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("[WhiteBoard Server] Đang lắng nghe trên port " + port);
            while (true) {
                Socket s = ss.accept();
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

                // gửi lịch sử cho client mới
                for (WhiteboardPacket p : serverView.getHistory()) {
                    WhiteboardPacket syncPacket = new WhiteboardPacket(p.x, p.y, p.color);
                    syncPacket.command = WhiteboardCommand.SYNC;
                    out.writeObject(syncPacket);
                }
                out.flush();

                clients.add(out);

                // lắng nghe client
                new Thread(() -> listen(s)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object listen(Socket s) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * Lắng nghe dữ liệu vẽ từ client
     */
    private void listenClient(Socket clientSocket, ObjectInputStream in) {
        try {
            while (true) {
                WhiteboardPacket packet = (WhiteboardPacket) in.readObject();
                broadcast(packet);
                serverView.applyPacket(packet);
            }
        } catch (Exception e) {
            System.out.println("[WhiteBoard Server] Client ngắt kết nối: " + clientSocket.getInetAddress());
            removeClient(clientSocket);
        }
    }

    /**
     * Gửi dữ liệu vẽ tới tất cả client
     */
    private void broadcast(WhiteboardPacket packet) {
        for (ObjectOutputStream out : clients) {
            try {
                out.writeObject(packet);
                out.flush();
            } catch (IOException e) {
                // loại bỏ client lỗi
                clients.remove(out);
            }
        }
    }

    /**
     * Loại bỏ client khi mất kết nối
     */
    private void removeClient(Socket clientSocket) {
        clients.removeIf(out -> {
            try {
                return ((Socket) out.getClass().getMethod("getSocket").invoke(out)).equals(clientSocket);
            } catch (Exception e) {
                return true;
            }
        });
    }

    /**
     * Hiển thị GUI WhiteBoard server
     */
    public void showWindow() {
        if (serverView != null) {
            serverView.setVisible(true);
        }
    }


    /**
     * Main test độc lập
     */
//    public static void main(String[] args) {
//        new WhiteBoardController(); // khởi tạo server + GUI
//    }
}

