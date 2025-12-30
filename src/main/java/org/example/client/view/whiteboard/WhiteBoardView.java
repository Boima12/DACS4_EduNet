package org.example.client.view.whiteboard;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;
import javax.swing.*;

import org.example.common.objects.services.whiteBoard.WhiteboardCommand;
import org.example.common.objects.services.whiteBoard.WhiteboardPacket;

/**
 * Code UI/Logic cho cửa sổ giao diện whiteboard của Client.
 *
 */
public class WhiteBoardView extends JFrame {

    private boolean locked = false;
    private boolean canDraw = true;
    private Color currentColor = Color.BLACK;

    private final Stack<WhiteboardPacket> strokes = new Stack<>();
    private final JPanel board;

    public WhiteBoardView(ObjectOutputStream out, ObjectInputStream in) {
        setTitle("CLIENT WHITEBOARD");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== BOARD =====
        board = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                synchronized (strokes) {
                    for (WhiteboardPacket p : strokes) {
                        if (p.command == WhiteboardCommand.DRAW || p.command == WhiteboardCommand.SYNC) {
                            g.setColor(p.color != null ? p.color : Color.BLACK);
                            g.fillOval(p.x, p.y, 5, 5);
                        }
                    }
                }
            }
        };
        board.setBackground(Color.WHITE);

        // ===== DRAWING =====
        board.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (locked || !canDraw) return;
                try {
                    WhiteboardPacket p = new WhiteboardPacket(e.getX(), e.getY(), currentColor);
                    p.command = WhiteboardCommand.DRAW;

                    // Vẽ local trước
                    synchronized (strokes) {
                        strokes.push(p);
                    }
                    board.repaint();

                    // Gửi lên server
                    if (out != null) {
                        out.writeObject(p);
                        out.flush();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // ===== TOOLS =====
        JButton toggleDraw = new JButton("TẮT VẼ");
        toggleDraw.addActionListener(e -> {
            canDraw = !canDraw;
            toggleDraw.setText(canDraw ? "TẮT VẼ" : "BẬT VẼ");
        });

        JButton clear = new JButton("XÓA");
        clear.addActionListener(e -> {
            try {
                // Xóa local trước
                synchronized (strokes) {
                    strokes.clear();
                }
                board.repaint();

                // Gửi lệnh CLEAR tới server
                if (out != null) {
                    WhiteboardPacket p = new WhiteboardPacket(WhiteboardCommand.CLEAR);
                    out.writeObject(p);
                    out.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JButton colorBtn = new JButton("CHỌN MÀU");
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Chọn màu", currentColor);
            if (c != null) currentColor = c;
        });

        JPanel tools = new JPanel();
        tools.add(toggleDraw);
        tools.add(clear);
        tools.add(colorBtn);

        add(tools, BorderLayout.NORTH);
        add(board, BorderLayout.CENTER);

        // ===== LISTEN SERVER =====
        new Thread(() -> {
            try {
                while (true) {
                    Object obj = in.readObject();
                    if (!(obj instanceof WhiteboardPacket)) continue;
                    WhiteboardPacket p = (WhiteboardPacket) obj;

                    synchronized (strokes) {
                        switch (p.command) {
                            case DRAW, SYNC -> strokes.push(p); // Client nhận DRAW/SYNC từ server
                            case CLEAR -> strokes.clear();      // Nhận CLEAR từ server
                            case UNDO -> { if (!strokes.isEmpty()) strokes.pop(); }
                            case LOCK -> locked = p.lock;       // Lock/Unlock
                        }
                    }

                    // Cập nhật GUI an toàn
                    SwingUtilities.invokeLater(board::repaint);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        this,
                        "Mất kết nối với server WhiteBoard!",
                        "Lỗi kết nối",
                        JOptionPane.ERROR_MESSAGE
                ));
            }
        }).start();

        setVisible(true);
    }

    // Hiển thị lỗi cho client
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        ));
    }
}