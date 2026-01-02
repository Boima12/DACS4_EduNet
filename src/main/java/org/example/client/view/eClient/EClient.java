package org.example.client.view.eClient;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;

import org.example.client.ClientStates;
import org.example.client.controller.services.lock.LockController;
import org.example.client.controller.services.whiteBoard.WhiteBoardController;
import org.example.common.utils.gui.RoundedBorder;
import org.example.common.utils.network.NetworkUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Iterator;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * UI chính của client
 *
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "ConstantValue", "UnusedAssignment"})
public class EClient {

	private boolean isStatusConnected;
	private String localIP;
	private String serverIP;
    private int serverPort = 0;
    public int id_port_watch;
    public int id_port_whiteboard;
    public int id_port_lock;
    private volatile boolean videoThreadRunning = true;
	
	private JFrame frame;
	private JLabel lbl_status2;
    private JLabel lbl_serverIP;

	/**
	 * Create the application.
	 */
	public EClient(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.id_port_whiteboard = serverPort + 1;
        this.id_port_watch = serverPort + 2;
        this.id_port_lock = serverPort + 3;

		isStatusConnected = true;	// TODO remove later when implement 15s check functionality
		localIP = NetworkUtils.getLocalIPAddress();

		initialize();
		updateStatus();

        // Update label text after values are set to avoid nulls
        if (lbl_serverIP != null) {
            lbl_serverIP.setText("Server IP: " + (this.serverIP != null ? this.serverIP : "-") + ":" + (this.serverPort != 0 ? this.serverPort : "-"));
            lbl_serverIP.revalidate();
            lbl_serverIP.repaint();
        }

        // Khởi động luồng truyền màn hình (Watch)
        startVideoThread(serverIP, id_port_watch);

        // Khởi tạo bộ điều khiển khóa màn hình
        LockController lockController = new LockController(serverIP, id_port_lock);

        // Xử lý đóng cửa sổ
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                videoThreadRunning = false; // Ngắt luồng capture
                if (lockController != null) lockController.shutdown();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
	}
	
	public void display() {
		frame.setVisible(true);
	}

	public void undisplay() {
		frame.setVisible(false); 
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(251, 251, 251));
		frame.setBounds(100, 100, 400, 390);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("EClient - " + localIP);
		frame.getContentPane().setLayout(null);
		
		JLabel lbl_EduNet = new JLabel("EduNet client");
		lbl_EduNet.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lbl_EduNet.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_EduNet.setBounds(70, 17, 236, 30);
		frame.getContentPane().add(lbl_EduNet);
		
		JLabel lbl_status1 = new JLabel("Tình trạng: ");
		lbl_status1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_status1.setBounds(119, 62, 65, 22);
		frame.getContentPane().add(lbl_status1);
		
		lbl_status2 = new JLabel("Đã liên kết");
		lbl_status2.setForeground(new Color(19, 166, 19));	
		lbl_status2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_status2.setBounds(186, 62, 151, 22);
		frame.getContentPane().add(lbl_status2);
		
		// Initialize with placeholder; actual text set in display()
		lbl_serverIP = new JLabel("Server IP: -:-");
		lbl_serverIP.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_serverIP.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl_serverIP.setBounds(53, 94, 287, 22);
		frame.getContentPane().add(lbl_serverIP);
		
		JPanel msgPanel = new JPanel(new GridLayout(1, 2, 15, 15));
		msgPanel.setBounds(15, 131, 352, 62);
		msgPanel.setOpaque(false);
		msgPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 2), "TRAO ĐỔI",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(127, 140, 141)));
		frame.getContentPane().add(msgPanel);
		
		JButton btn_traoDoi_msgClass = new JButton("Gửi tin cho lớp");
		btn_traoDoi_msgClass.setBorder(new RoundedBorder(8));
		btn_traoDoi_msgClass.setForeground(new Color(0, 0, 0));
		btn_traoDoi_msgClass.setBackground(new Color(251, 251, 251));
		btn_traoDoi_msgClass.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_traoDoi_msgClass.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn_traoDoi_msgClass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		msgPanel.add(btn_traoDoi_msgClass);
		
		JButton btn_traoDoi_msgTeacher = new JButton("Chat với giáo viên");
		btn_traoDoi_msgTeacher.setForeground(Color.BLACK);
		btn_traoDoi_msgTeacher.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_traoDoi_msgTeacher.setBorder(new RoundedBorder(8));
		btn_traoDoi_msgTeacher.setBackground(new Color(251, 251, 251));
		btn_traoDoi_msgTeacher.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn_traoDoi_msgTeacher.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		msgPanel.add(btn_traoDoi_msgTeacher);
		
		JPanel videoPanel = new JPanel(new GridLayout(1, 2, 15, 15));
		videoPanel.setBounds(15, 203, 352, 62);
		videoPanel.setOpaque(false);
		videoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 2), "TRỰC TUYẾN",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(127, 140, 141)));
		frame.getContentPane().add(videoPanel);
		
		JButton btn_trucTuyen_callGroup = new JButton("Video Call Nhóm");
		btn_trucTuyen_callGroup.setForeground(Color.BLACK);
		btn_trucTuyen_callGroup.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_trucTuyen_callGroup.setBorder(new RoundedBorder(8));
		btn_trucTuyen_callGroup.setBackground(new Color(251, 251, 251));
		btn_trucTuyen_callGroup.setBounds(20, 181, 166, 35);
		btn_trucTuyen_callGroup.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn_trucTuyen_callGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		videoPanel.add(btn_trucTuyen_callGroup);
		
		JButton btn_trucTuyen_callTeacher = new JButton("Video Call Giáo viên");
		btn_trucTuyen_callTeacher.setForeground(Color.BLACK);
		btn_trucTuyen_callTeacher.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_trucTuyen_callTeacher.setBorder(new RoundedBorder(8));
		btn_trucTuyen_callTeacher.setBackground(new Color(251, 251, 251));
		btn_trucTuyen_callTeacher.setBounds(196, 181, 166, 35);
		btn_trucTuyen_callTeacher.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn_trucTuyen_callTeacher.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		videoPanel.add(btn_trucTuyen_callTeacher);
		
		JPanel toolPanel = new JPanel(new GridLayout(1, 2, 15, 15));
		toolPanel.setBounds(15, 275, 352, 62);
		toolPanel.setOpaque(false);
		toolPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 2), "HỌC TẬP",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(127, 140, 141)));
		frame.getContentPane().add(toolPanel);
		
		JButton btn_hocTap_bangTrang = new JButton("Bảng trắng");
		btn_hocTap_bangTrang.setForeground(Color.BLACK);
		btn_hocTap_bangTrang.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_hocTap_bangTrang.setBorder(new RoundedBorder(8));
		btn_hocTap_bangTrang.setBackground(new Color(251, 251, 251));
		btn_hocTap_bangTrang.setBounds(20, 231, 166, 35);
		btn_hocTap_bangTrang.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn_hocTap_bangTrang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                try {
                    new WhiteBoardController(serverIP, id_port_whiteboard);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Lỗi: Không thể kết nối Bảng trắng!");
                }
			}
		});
		toolPanel.add(btn_hocTap_bangTrang);
		
		JButton btn_hocTap_nopBaiTap = new JButton("Nộp bài tập");
		btn_hocTap_nopBaiTap.setForeground(Color.BLACK);
		btn_hocTap_nopBaiTap.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_hocTap_nopBaiTap.setBorder(new RoundedBorder(8));
		btn_hocTap_nopBaiTap.setBackground(new Color(251, 251, 251));
		btn_hocTap_nopBaiTap.setBounds(196, 231, 166, 35);
		btn_hocTap_nopBaiTap.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn_hocTap_nopBaiTap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                // Chỉ gửi tín hiệu yêu cầu hiển thị.
                // ExerciseController (đã khởi tạo ở CoreClient) sẽ bắt được và show View.
                ClientStates.fireShowExerciseView();
			}
		});
		toolPanel.add(btn_hocTap_nopBaiTap);
	}
	
	private void updateStatus() {
		// every 15 seconds, update lbl_status2
		
		// TODO every 15 seconds check connection between server and client
		if (isStatusConnected) {
			lbl_status2.setText("Đã liên kết");
			lbl_status2.setForeground(new Color(19, 166, 19));	
		} else {
			lbl_status2.setText("Mất kết nối");
			lbl_status2.setForeground(new Color(238, 59, 62));			
		}
	}

    private void startVideoThread(String serverIP, int port) {
        Thread t = new Thread(() -> onWatchVideo(serverIP, port));
        t.setDaemon(true);
        t.start();
    }

    private void onWatchVideo(String serverIP, int port) {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        Robot robot;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            return;
        }

        while (videoThreadRunning) {
            try (Socket socket = new Socket(serverIP, port);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                ImageWriter writer = writers.next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.5f); // Giảm chất lượng một chút để tăng tốc độ truyền

                while (!socket.isClosed() && videoThreadRunning) {
                    BufferedImage screen = robot.createScreenCapture(screenRect);

                    // Resize để giảm băng thông (Quan trọng cho truyền mượt)
                    BufferedImage resized = new BufferedImage(1024, 576, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = resized.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(screen, 0, 0, 1024, 576, null);
                    g.dispose();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                        writer.setOutput(ios);
                        writer.write(null, new IIOImage(resized, null, null), param);
                    }

                    byte[] data = baos.toByteArray();
                    dos.writeInt(data.length);
                    dos.write(data);
                    dos.flush();

                    Thread.sleep(100); // ~10 FPS là đủ để quan sát
                }
                writer.dispose();
            } catch (Exception e) {
                // Thử kết nối lại sau 5 giây nếu rớt mạng
                try { Thread.sleep(5000); } catch (InterruptedException ex) {}
            }
        }
    }
}
