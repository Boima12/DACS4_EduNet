package org.example.client.controller.services.whiteBoard;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.example.client.view.whiteboard.WhiteBoardView;
import org.example.common.objects.services.whiteBoard.WhiteboardPacket;

/**
 * WhiteBoardController - Phiên bản client tối ưu
 *
 * - Gửi stroke (WhiteboardPacket) tới server
 * - Nhận stroke từ server và cập nhật trực tiếp trong WhiteBoardView
 */
public class WhiteBoardController {

    private String serverIP;
    private int serverPort;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private WhiteBoardView view;

    public WhiteBoardController(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        initClient();
    }

    private void initClient() {
        new Thread(() -> {
            try {
                // Kết nối tới server
                socket = new Socket(serverIP, serverPort);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Tạo GUI WhiteBoard client
                SwingUtilities.invokeLater(() -> {
                    view = new WhiteBoardView(out, in);
                    view.setVisible(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        null,
                        "Không thể kết nối tới WhiteBoard server!\n" + e.getMessage(),
                        "Lỗi kết nối",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        }).start();
    }

    // Gửi stroke thủ công từ bên ngoài (nếu cần)
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

    // Đóng kết nối khi client thoát
    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

