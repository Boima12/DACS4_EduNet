package org.example.server.view.whiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.example.common.objects.services.whiteBoard.WhiteboardCommand;
import org.example.common.objects.services.whiteBoard.WhiteboardPacket;
import org.example.common.utils.gui.ImageHelper;
import org.example.server.view.about.AboutModal;

/**
 * Code UI/Logic cho cửa sổ giao diện Whiteboard của Server
 *
 */
public class WhiteBoardView extends JFrame {
    private boolean locked = false;
    private Color currentColor = Color.BLACK;

	private JFrame frame;

	private final List<WhiteboardPacket> history = new CopyOnWriteArrayList<>();
    
    private final BoardPanel board;
    
	private Color mau081C15;
	private Color mau2D6A4F;
	private Color mauFFFFFF;

    public WhiteBoardView(Consumer<WhiteboardPacket> sender) {
        this.setBounds(100, 50, 1300, 700);
        this.getContentPane().setLayout(null);
        this.setUndecorated(true);

		mau081C15 = Color.decode("#081C15");
		mau2D6A4F = Color.decode("#2D6A4F");
		mauFFFFFF = Color.decode("#FFFFFF");

        JPanel jPanel = new JPanel(); 
        jPanel.setBounds(0, 0, 1300, 700);
        jPanel.setLayout(null);
        getContentPane().add(jPanel);
        
        JPanel item_2 = new JPanel();
        item_2.setBounds(75, 0, 1225, 10);
        item_2.setLayout(null);
        item_2.setBackground(mau2D6A4F);
        jPanel.add(item_2);
        
        JPanel item_1 = new JPanel();
        item_1.setBounds(0, 0, 75, 700);
        item_1.setLayout(null);
        item_1.setBackground(mau081C15);
        jPanel.add(item_1);
        
        JButton btn_about = new JButton("");
		btn_about.setSize(new Dimension(30, 30));
		btn_about.setBorderPainted(false);
		btn_about.setBackground(mau081C15);
		btn_about.setBounds(0, 625, 75, 75);
		btn_about.setIcon(ImageHelper.getScaledIcon("/images/about_white.png", 27, 27));
		btn_about.addActionListener(e -> onAbout());
		item_1.add(btn_about);
		
        JPanel tools = new JPanel();
        tools.setBounds(75, 10, 1225, 60);
        tools.setLayout(null); 
        tools.setBackground(mau2D6A4F);
        jPanel.add(tools);
        
        // Tool panel
        // EXIT
        JButton exit = new JButton("EXIT");
        exit.setBackground(mauFFFFFF);
        exit.setFont(new Font("Tahoma", Font.BOLD, 13));
        exit.setBounds(10, 10, 150, 35);
        exit.setBorder(null);
        exit.addActionListener(e -> {
            setVisible(false);
            dispose();
        });
        tools.add(exit);
        
        //CLEAR
        JButton clear = new JButton("CLEAR");
        clear.setFont(new Font("Tahoma", Font.BOLD, 13));
        clear.setBounds(170, 10, 150, 35);
        clear.setBorder(null);
        clear.setBackground(mauFFFFFF);
        clear.addActionListener(e -> {
            WhiteboardPacket p = new WhiteboardPacket(WhiteboardCommand.CLEAR);
            applyPacket(p);
            sender.accept(p);
        });
        tools.add(clear);

        //DELATE
        JButton undo = new JButton("DELATE");
        undo.setFont(new Font("Tahoma", Font.BOLD, 13));
        undo.setBounds(330, 10, 150, 35);
        undo.setBorder(null);
        undo.setBackground(mauFFFFFF);
        undo.addActionListener(e -> {
            WhiteboardPacket p = new WhiteboardPacket(WhiteboardCommand.UNDO);
            applyPacket(p);
            sender.accept(p);
        });
        tools.add(undo);
        
        //LOCK
        JButton lockBtn = new JButton("LOCK");
        lockBtn.setFont(new Font("Tahoma", Font.BOLD, 13));
        lockBtn.setBounds(490, 10, 150, 35);
        lockBtn.setBorder(null);
        lockBtn.setBackground(mauFFFFFF);
        lockBtn.addActionListener(e -> {
            locked = !locked;
            WhiteboardPacket p = new WhiteboardPacket(WhiteboardCommand.LOCK);
            p.lock = locked;
            sender.accept(p);
            lockBtn.setText(locked ? "UNLOCK" : "LOCK");
        });
        tools.add(lockBtn);

        //COLOR
        JButton colorBtn = new JButton("COLOR");
        colorBtn.setBackground(new Color(255, 255, 255));
        colorBtn.setFont(new Font("Tahoma", Font.BOLD, 13));
        colorBtn.setBounds(650, 10, 150, 35);
        colorBtn.setBorder(null);
        colorBtn.setBackground(mauFFFFFF);
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Chọn màu vẽ", currentColor);
            if (c != null) currentColor = c;
        });
        tools.add(colorBtn);
        
        //TOPIC
        JLabel jLabel = new JLabel("WHITE BOARD");
        jLabel.setForeground(new Color(255, 255, 255));
        jLabel.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        jLabel.setBounds(840, 10, 385, 35);
        jLabel.setBorder(null);
        tools.add(jLabel);

        board = new BoardPanel();
        board.setBounds(75, 70, 1225, 630);
        
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
        jPanel.add(board);
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

	public void showError(String msg) {
		// TODO Auto-generated method stub
	    JOptionPane.showMessageDialog(this,msg,"Lỗi",JOptionPane.ERROR_MESSAGE);
	}
	
	private void onAbout() {
	    JOptionPane.showMessageDialog(
	            this.frame,
	            AboutModal.createContent(),
	            "Về hệ thống EduNet",
	            JOptionPane.PLAIN_MESSAGE
	    );
	}
}
