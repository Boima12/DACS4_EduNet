package org.example.server.view.manage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;

import org.example.common.utils.gui.ImageHelper;
import org.example.common.utils.gui.RoundedBorder;
import org.example.server.ServerStates;
import org.example.server.view.about.AboutModal;


/**
 * Code UI/logic cho phần quản lý client
 * Hiện tại chỉ có thể bật được duy nhất 1 Cửa sổ Manage, nếu muốn mở thêm thì phải đóng cửa sổ hiện tại trước
 * Có thể nâng cấp để mở nhiều cửa sổ Manage cùng lúc cho từng client 1 nhưng hiện tại tạm thời bỏ qua vì logic cũng tương đối phức tạp
 *
 */
@SuppressWarnings({"FieldMayBeFinal", "Convert2Lambda"})
public class Manage {

    private String client_name;

	private JFrame frame;
	private JLabel lbl_item_name;
	private JLabel lbl_os;
	private JLabel lbl_cpu_cores;
	private JLabel lbl_cpu_load;
	private JLabel lbl_ram;
	private JLabel lbl_INetAddress;
	private JTextArea ta_diskStorages;

	private Color mau081C15;

	private Color mauD8F3DC;

	private Color mau2D6A4F;

	private Color mauB0B0B0;

	/**
	 * Create the application.
	 */
	public Manage(String client_name) { 
		initialize();
		onClosingEvent();

        this.client_name = client_name;

        // setup callback làm mới thông tin hệ thống cho cửa sổ Manage
        ServerStates.setOnSystemInfoResponseListenerCallback((os, cpu_cores, cpu_load, ram, inet_address, disk_storages) -> {
            lbl_item_name.setText(client_name);
            lbl_os.setText("OS: " + os);
            lbl_cpu_cores.setText("CPU Cores: " + cpu_cores);
            lbl_cpu_load.setText("CPU Load: " + cpu_load);
            lbl_ram.setText("RAM: " + ram);
            lbl_INetAddress.setText(inet_address);
            ta_diskStorages.setText(disk_storages);

            frame.setTitle(inet_address + " / " + client_name);

            frame.revalidate();
            frame.repaint();
        });

        // gửi yêu cầu lấy thông tin hệ thống ngay khi mở cửa sổ Manage
        onSystemInfoRequest();
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
		
		mau081C15 = Color.decode("#081C15");
		mauD8F3DC = Color.decode("#D8F3DC");
		mau2D6A4F = Color.decode("#2D6A4F");
		mauB0B0B0 = Color.decode("#B0B0B0");
		
		frame = new JFrame();
		
		frame.getContentPane().setBackground(new Color(242, 242, 242));
		frame.setBounds(100, 50, 1300, 700);
		frame.setTitle("InetAddress / Client name placeholder");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel item_1 = new JPanel();
		item_1.setLayout(null);
		item_1.setBackground(mau081C15);
		item_1.setBounds(0, 0, 75, 700);
		frame.getContentPane().add(item_1);	
		
		JButton btn_about = new JButton("");
		btn_about.setSize(new Dimension(30, 30));
		btn_about.setBorderPainted(false);
		btn_about.setBackground(mau081C15);
		btn_about.setBounds(0, 590, 75, 75);
		btn_about.setIcon(ImageHelper.getScaledIcon("/images/about_white.png", 27, 27));
		btn_about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onAbout();
			}
		});
		item_1.add(btn_about);
		
		JPanel item_2 = new JPanel();
		item_2.setLayout(null);
		item_2.setBackground(mau081C15);
		item_2.setBounds(1050, 0, 250, 700);
		frame.getContentPane().add(item_2);
		
		JPanel item_3 = new JPanel();
		item_3.setLayout(null);
		item_3.setBackground(mau2D6A4F);
		item_3.setBounds(75, 0, 975, 20);
		frame.getContentPane().add(item_3);
		
		JPanel item_4 = new JPanel();
		item_4.setBackground(mauB0B0B0);
		item_4.setBounds(75, 235, 975, 5);
		frame.add(item_4);
		
		JPanel panel_top = new JPanel();
	    panel_top.setLayout(null);
		panel_top.setBackground(new Color(251, 251, 251));
		panel_top.setBounds(75, 20, 975, 155);
		frame.getContentPane().add(panel_top);

	    JPanel client_item = new JPanel();
	    client_item.setBounds(0, 0, 135, 145);
	    client_item.setLayout(null);
	    client_item.setBackground(new Color(251, 251, 251));
	    panel_top.add(client_item);
	    
	    JLabel lbl_item_icon = new JLabel();
	    lbl_item_icon.setBounds(20, 10, 95, 95);
    	lbl_item_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop.png", 95, 95));	    	
	    client_item.add(lbl_item_icon);
	    
	    lbl_item_name = new JLabel("client_name", SwingConstants.CENTER);
	    lbl_item_name.setFont(new Font("Tahoma", Font.PLAIN, 15));
	    lbl_item_name.setBounds(10, 110, 115, 20);
	    client_item.add(lbl_item_name);
	    
	    JPanel panel_middle = new JPanel();
	    panel_middle.setBackground(new Color(251, 251, 251));
	    panel_middle.setBounds(135, 10, 725, 135);
	    panel_top.add(panel_middle);
	    panel_middle.setLayout(null);
	    
	    JPanel panel_middle_1 = new JPanel();
	    panel_middle_1.setBounds(0, 0, 350, 135);
	    panel_middle_1.setBackground(new Color(251, 251, 251));
	    panel_middle.add(panel_middle_1);
	    panel_middle_1.setLayout(null);
	    
	    lbl_os = new JLabel("OS: ?", SwingConstants.LEFT);
	    lbl_os.setFont(new Font("Tahoma", Font.PLAIN, 16));
	    lbl_os.setBounds(0, 0, 350, 20);
	    panel_middle_1.add(lbl_os);
	    
	    lbl_cpu_cores = new JLabel("CPU Cores: ?", SwingConstants.LEFT);
	    lbl_cpu_cores.setFont(new Font("Tahoma", Font.PLAIN, 12));
	    lbl_cpu_cores.setBounds(0, 30, 350, 15);
	    panel_middle_1.add(lbl_cpu_cores);
	    
	    lbl_cpu_load = new JLabel("CPU Load: ?", SwingConstants.LEFT);
	    lbl_cpu_load.setFont(new Font("Tahoma", Font.PLAIN, 12));
	    lbl_cpu_load.setBounds(0, 55, 350, 15);
	    panel_middle_1.add(lbl_cpu_load);
	    
	    lbl_ram = new JLabel("RAM: ?", SwingConstants.LEFT);
	    lbl_ram.setFont(new Font("Tahoma", Font.PLAIN, 12));
	    lbl_ram.setBounds(0, 80, 350, 15);
	    panel_middle_1.add(lbl_ram);
	    
	    lbl_INetAddress = new JLabel("/xxx.xxx.xxx.xxx", SwingConstants.LEFT);
	    lbl_INetAddress.setFont(new Font("Tahoma", Font.PLAIN, 12));
	    lbl_INetAddress.setBounds(0, 105, 350, 15);
	    panel_middle_1.add(lbl_INetAddress);
	    
	    ta_diskStorages = new JTextArea();
	    ta_diskStorages.setEditable(false);
	    ta_diskStorages.setText("Disk C:\\: ?\r\nDisk D:\\: ?\r\nDisk E:\\: ?");
	    ta_diskStorages.setBackground(new Color(251, 251, 251));
	    ta_diskStorages.setFont(new Font("Tahoma", Font.PLAIN, 12));
	    ta_diskStorages.setWrapStyleWord(true);
	    ta_diskStorages.setBounds(360, 0, 365, 135);
	    panel_middle.add(ta_diskStorages);
		
		JButton btn_refresh = new JButton("");
		btn_refresh.setSize(new Dimension(30, 30));
		btn_refresh.setBorder(new RoundedBorder(8));
		btn_refresh.setBackground(new Color(251, 251, 251));
		btn_refresh.setBounds(900, 10, 50, 50);
		btn_refresh.setIcon(ImageHelper.getScaledIcon("/images/refresh.png", 28, 28));
		btn_refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                onSystemInfoRequest();
			}
		});
		panel_top.add(btn_refresh);
		
		
		//
		JPanel dashboard_options = new JPanel();
		dashboard_options.setBackground(mau2D6A4F);
		dashboard_options.setBounds(75, 175, 975, 60);
		dashboard_options.setLayout(null);
		frame.getContentPane().add(dashboard_options);
		
		JButton btn_do_lkClient = new JButton("Yêu cầu chụp ảnh");
		btn_do_lkClient.setForeground(Color.BLACK);
		btn_do_lkClient.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_do_lkClient.setBackground(new Color(251, 251, 251));
		btn_do_lkClient.setBounds(10, 10, 150, 35);
		btn_do_lkClient.setBorder(null);
		//btn_do_lkClient.addActionListener(e -> onlkClient());
		dashboard_options.add(btn_do_lkClient);

		JButton btn_do_lanScan = new JButton("Xem ảnh");
		btn_do_lanScan.setForeground(Color.BLACK);
		btn_do_lanScan.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_do_lanScan.setBackground(new Color(251, 251, 251));
		btn_do_lanScan.setBounds(170, 10, 150, 35);
		btn_do_lanScan.setBorder(null);
		btn_do_lanScan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		dashboard_options.add(btn_do_lanScan);
		
		JButton btn_do_notificationAll = new JButton("Xóa ảnh");
		btn_do_notificationAll.setForeground(Color.BLACK);
		btn_do_notificationAll.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_do_notificationAll.setBackground(new Color(251, 251, 251));
		btn_do_notificationAll.setBounds(330, 10, 150, 35);
		btn_do_notificationAll.setBorder(null);
		//Câu lệnh chức năng 
		//btn_do_notificationAll.addActionListener(e -> onNotificationAll());
		dashboard_options.add(btn_do_notificationAll);
		
		JButton btn_do_watch = new JButton("Thông báo");
		btn_do_watch.setForeground(Color.BLACK);
		btn_do_watch.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_do_watch.setBackground(new Color(251, 251, 251));
		btn_do_watch.setBounds(490, 10, 150, 35);
		btn_do_watch.setBorder(null);
		//btn_do_watch.addActionListener(e -> showWatchController());
		dashboard_options.add(btn_do_watch);
		
		JButton btn_do_whiteBoard = new JButton("Khóa máy");
		btn_do_whiteBoard.setForeground(Color.BLACK);
		btn_do_whiteBoard.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_do_whiteBoard.setBackground(new Color(251, 251, 251));
		btn_do_whiteBoard.setBounds(650, 10, 150, 35);
		btn_do_whiteBoard.setBorder(null);
		//btn_do_whiteBoard.addActionListener(e -> openWhiteBoardServer());
		dashboard_options.add(btn_do_whiteBoard);

        JButton bton_nopbaitap = new JButton("Tắt máy");
        bton_nopbaitap.setForeground(Color.BLACK);
        bton_nopbaitap.setFont(new Font("Tahoma", Font.BOLD, 12));
        bton_nopbaitap.setBackground(new Color(251, 251, 251));
        bton_nopbaitap.setBounds(810, 10, 150, 35);
        bton_nopbaitap.setBorder(null);
        //bton_nopbaitap.addActionListener(e -> onExercise());
        dashboard_options.add(bton_nopbaitap);
		
		
		//
		
		
//		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
//		tabbedPane.setBackground(new Color(251, 251, 251));
//		tabbedPane.setBounds(56, 203, 965, 407);
//		frame.getContentPane().add(tabbedPane);
		
//		JPanel tabbed_panel1 = new JPanel();
//		tabbed_panel1.setBackground(new Color(251, 251, 251));
//		tabbedPane.addTab("tab 1", null, tabbed_panel1, null);
//		tabbed_panel1.setLayout(null);
//		
//		JLabel lbl_text1 = new JLabel("JTabbedPanel placeholder", SwingConstants.CENTER);
//		lbl_text1.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		lbl_text1.setBounds(297, 168, 350, 15);
//		tabbed_panel1.add(lbl_text1);
//		
//		JPanel tabbed_panel2 = new JPanel();
//		tabbed_panel2.setLayout(null);
//		tabbed_panel2.setBackground(new Color(251, 251, 251));
//		tabbedPane.addTab("tab 2", null, tabbed_panel2, null);
//		
//		JLabel lbl_text2 = new JLabel("JTabbedPanel placeholder", SwingConstants.CENTER);
//		lbl_text2.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		lbl_text2.setBounds(297, 168, 350, 15);
//		tabbed_panel2.add(lbl_text2);
//		
//		JPanel tabbed_panel3 = new JPanel();
//		tabbed_panel3.setLayout(null);
//		tabbed_panel3.setBackground(new Color(251, 251, 251));
//		tabbedPane.addTab("tab 3", null, tabbed_panel3, null);
//		
//		JLabel lbl_text3 = new JLabel("JTabbedPanel placeholder", SwingConstants.CENTER);
//		lbl_text3.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		lbl_text3.setBounds(297, 168, 350, 15);
//		tabbed_panel3.add(lbl_text3);
	}
	
    private void onClosingEvent() {
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                undisplay();
                ServerStates.manage = null;
                frame.dispose();
            }
        });
    }
	
	private void onSystemInfoRequest() {
        if (ServerStates.onSystemInfoRequestListener != null) ServerStates.onSystemInfoRequestListener.onSystemInfoRequest(client_name);
	}
	
	private void onAbout() {
	    JOptionPane.showMessageDialog(
	            this.frame,
	            AboutModal.createContent(),
	            "Về hệ thống EduNet",
	            JOptionPane.PLAIN_MESSAGE
	    );
	}
	
//	public static void main(String[] args) {
//	    EventQueue.invokeLater(() -> {
//	        Manage manage = new Manage("Client-Test");
//	        manage.display();
//
//	        // Fake dữ liệu để test giao diện
//	        manage.lbl_item_name.setText("Client-Test");
//	        manage.lbl_os.setText("OS: Windows 11");
//	        manage.lbl_cpu_cores.setText("CPU Cores: 8");
//	        manage.lbl_cpu_load.setText("CPU Load: 25%");
//	        manage.lbl_ram.setText("RAM: 16 GB");
//	        manage.lbl_INetAddress.setText("192.168.1.14");
//	        manage.ta_diskStorages.setText(
//	                "Disk C:\\ 120 GB / 256 GB\n" +
//	                "Disk D:\\ 500 GB / 1 TB"
//	        );
//	    });
//	}

	
}
