package org.example.client.controller.services.whiteBoard;

import java.io.*;
import java.net.Socket;
import javax.swing.*;

import org.example.client.view.whiteboard.WhiteBoardView;
import org.example.common.objects.services.whiteBoard.WhiteboardPacket;

/**
 * WhiteBoardController - Client (FINAL)
 */
public class WhiteBoardController {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private WhiteBoardView view;

    public WhiteBoardController(String serverIP, int serverPort) {
        connect(serverIP, serverPort);
    }

    private void connect(String ip, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in  = new ObjectInputStream(socket.getInputStream());

                SwingUtilities.invokeLater(() -> {
                    view = new WhiteBoardView(this::sendPacket);
                    view.setVisible(true);
                });

                // Thread lắng nghe server
                new Thread(this::listenServer).start();

            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(
                        null,
                        "Không thể kết nối WhiteBoard Server",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    )
                );
            }
        }).start();
    }

    private void listenServer() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof WhiteboardPacket packet) {
                    SwingUtilities.invokeLater(() ->
                        view.applyPacket(packet)
                    );
                }
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(
                    view,
                    "Mất kết nối WhiteBoard Server",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                )
            );
            close();
        }
    }

    public void sendPacket(WhiteboardPacket packet) {
        try {
            if (out != null) {
                out.writeObject(packet);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}

