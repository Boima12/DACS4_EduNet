package org.example.server.view.whiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.example.common.objects.services.whiteBoard.WhiteboardCommand;
import org.example.common.objects.services.whiteBoard.WhiteboardPacket;

/**
 * Code UI/Logic cho cửa sổ giao diện Whiteboard của Server
 *
 */
public class WhiteBoardView extends JFrame {

    private boolean locked = false;
    private Color currentColor = Color.BLACK;

    // Sử dụng CopyOnWriteArrayList tránh ConcurrentModificationException
    private final List<WhiteboardPacket> history = new ArrayList<>();
    private final BoardPanel board;

    public WhiteBoardView(Consumer<WhiteboardPacket> sender) {
        setTitle("SERVER WHITEBOARD");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        board = new BoardPanel();

        // Vẽ theo chuột
        board.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (locked) return;
                WhiteboardPacket p = new WhiteboardPacket(e.getX(), e.getY());
                p.command = WhiteboardCommand.DRAW;
                p.color = currentColor;
                applyPacket(p);       // Server tự vẽ
                sender.accept(p);     // Gửi tới Client
            }
        });

        // Tool panel
        JButton clear = new JButton("CLEAR");
        clear.addActionListener(e -> {
            WhiteboardPacket p = new WhiteboardPacket(WhiteboardCommand.CLEAR);
            applyPacket(p);
            sender.accept(p);
        });

        JButton undo = new JButton("UNDO");
        undo.addActionListener(e -> {
            WhiteboardPacket p = new WhiteboardPacket(WhiteboardCommand.UNDO);
            applyPacket(p);
            sender.accept(p);
        });

        JButton lockBtn = new JButton("LOCK");
        lockBtn.addActionListener(e -> {
            locked = !locked;
            WhiteboardPacket p = new WhiteboardPacket(WhiteboardCommand.LOCK);
            p.lock = locked;
            sender.accept(p);
            lockBtn.setText(locked ? "UNLOCK" : "LOCK");
        });

        JButton colorBtn = new JButton("Chọn màu");
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Chọn màu vẽ", currentColor);
            if (c != null) currentColor = c;
        });

        JPanel tools = new JPanel();
        tools.add(clear);
        tools.add(undo);
        tools.add(lockBtn);
        tools.add(colorBtn);

        add(tools, BorderLayout.NORTH);
        add(board, BorderLayout.CENTER);
       
//        setVisible(true);
    }

    // Áp dụng packet tới GUI Server
    public void applyPacket(WhiteboardPacket p) {
        switch (p.command) {
            case DRAW, SYNC -> history.add(p);
            case CLEAR -> history.clear();
            case UNDO -> {
                if (!history.isEmpty()) history.remove(history.size() - 1);
            }
            case LOCK -> locked = p.lock;
        }
        board.repaint();
    }

    // Trả về history để gửi SYNC
    public List<WhiteboardPacket> getHistory() {
        return history;
    }

    // Panel vẽ
    class BoardPanel extends JPanel {
        BoardPanel() { setBackground(Color.WHITE); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (WhiteboardPacket p : history) {
                g.setColor(p.color != null ? p.color : Color.BLACK);
                g.fillOval(p.x, p.y, 5, 5);
            }
        }
    }

	public void showError(String string) {
		// TODO Auto-generated method stub
		
	}
}
