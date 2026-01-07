package org.example.server.controller.services.whiteBoard;

import org.example.common.objects.services.whiteBoard.WhiteboardPacket;
import org.example.common.objects.services.whiteBoard.WhiteboardCommand;
import org.example.server.view.whiteBoard.WhiteBoardView;

import javax.swing.SwingUtilities;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WhiteBoardController - Server WhiteBoard LAN (FIXED)
 */
public class WhiteBoardController {

    private ServerSocket serverSocket;
    private List<ObjectOutputStream> clients = new CopyOnWriteArrayList<>();
    private WhiteBoardView serverView;
    private int port;

    public WhiteBoardController(int port) {
        this.port = port;

        // GUI Server
        serverView = new WhiteBoardView(packet -> {
            broadcast(packet);
            serverView.applyPacket(packet);
        });

        new Thread(this::startServer).start();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[WhiteBoard Server] Listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[WhiteBoard Server] Client connected: " + socket.getInetAddress());

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                clients.add(out);

                // Gửi lịch sử cho client mới
                for (WhiteboardPacket p : serverView.getHistory()) {
                    WhiteboardPacket sync = new WhiteboardPacket(p.x, p.y, p.color);
                    sync.command = WhiteboardCommand.SYNC;
                    out.writeObject(sync);
                }
                out.flush();

                // Thread lắng nghe client
                new Thread(() -> listenClient(socket, in)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenClient(Socket socket, ObjectInputStream in) {
        try {
            while (true) {
                WhiteboardPacket packet = (WhiteboardPacket) in.readObject();

                // Server tự vẽ
                SwingUtilities.invokeLater(() -> {
                    serverView.applyPacket(packet);
                });

                // Broadcast cho các client
                broadcast(packet);
            }
        } catch (Exception e) {
            System.out.println("[WhiteBoard Server] Client disconnected: " + socket.getInetAddress());
            removeClient(socket);
        }
    }

    private void broadcast(WhiteboardPacket packet) {
        for (ObjectOutputStream out : clients) {
            try {
                out.writeObject(packet);
                out.flush();
            } catch (IOException e) {
                clients.remove(out);
            }
        }
    }

    private void removeClient(Socket socket) {
        clients.removeIf(out -> {
            try {
                out.close();
            } catch (IOException ignored) {}
            return true;
        });
    }

    public void showWindow() {
        serverView.setVisible(true);
    }
}
