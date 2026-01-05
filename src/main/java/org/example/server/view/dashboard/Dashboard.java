package org.example.server.view.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;
import org.example.common.utils.gui.Alert;
import org.example.common.utils.gui.ImageHelper;
import org.example.common.utils.gui.RoundedBorder;
import org.example.common.utils.gui.WrapLayout;
import org.example.server.ServerStates;
import org.example.server.model.database.JDBCUtil;
import org.example.server.view.manage.Manage;
import org.example.server.view.watch.WatchView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Code UI/logic cho giao diện dashboard
 * JPanel này sẽ được load vào bên ServerUI.java bằng build()
 *
 */
@SuppressWarnings({"PatternVariableCanBeUsed", "Convert2Lambda"})
public class Dashboard extends JFrame {

    public static ArrayList<JPanel> client_dashboard_JPanelList = new ArrayList<>();
    public static String currentSelectedClientName = "none";

    private static final Logger log = LoggerFactory.getLogger(Dashboard.class);
        
    private static JFrame frame;
    
    private static JLabel lbl_info_icon;
    private static JLabel lbl_info_name;
    
    private static JPanel client_dashboard;
    
    private static JButton btn_manage;
    private static JButton btn_info_notificationSingle;
    private static JButton btn_info_placeholder3; 
    private static JButton btn_info_placeholder4;
    
	private static Color mau081C15;
	private static Color mauD8F3DC;
	private static Color mau2D6A4F;
	private static Color mauB0B0B0; 
    
    public Dashboard() {
    	// line này dùng để bật WindowBuilder, nếu comment line này sẽ tối ưu ứng dụng nhưng không thể sài Eclipse windowBuilder trong file này
    	initialize();   
    }

    private void initialize() { 
        frame = new JFrame();
        frame.setBounds(100, 50, 1300, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(build());
    }
	
	public static JPanel build() {
		
		mau081C15 = Color.decode("#081C15");
		mauD8F3DC = Color.decode("#D8F3DC");
		mau2D6A4F = Color.decode("#2D6A4F");
		mauB0B0B0 = Color.decode("#B0B0B0");

		JPanel dashboard = new JPanel(); 
		dashboard.setLayout(null);
		dashboard.setBackground(new Color(251, 251, 251));
		dashboard.setBounds(100, 50, 1300, 700);
		
		JPanel item_1 = new JPanel();
		item_1.setBackground(mau2D6A4F);
		item_1.setBounds(0, 0, 975, 10);
		dashboard.add(item_1);
		
		JPanel item_2 = new JPanel();
		item_2.setBackground(mauB0B0B0);
		item_2.setBounds(0, 70, 975, 5);
		dashboard.add(item_2);
		
		JPanel infobar = new JPanel();
		infobar.setBackground(mau081C15);
		infobar.setBounds(975, 0, 250, 700);
		infobar.setLayout(null);
		dashboard.add(infobar);
		
		lbl_info_icon = new JLabel("");
		lbl_info_icon.setBackground(new Color(240, 240, 240));
		lbl_info_icon.setBounds(50, 50, 150, 150);
		infobar.add(lbl_info_icon);
		
		lbl_info_name = new JLabel("Computer name");
		lbl_info_name.setForeground(new Color(255, 255, 255));
		lbl_info_name.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_info_name.setFont(new Font("Tahoma", Font.BOLD, 15));
		lbl_info_name.setBounds(10, 210, 230, 30);
		infobar.add(lbl_info_name);
		
		btn_manage = new JButton("Quản lý");
		btn_manage.setEnabled(false);
		btn_manage.setBorder(null);
		btn_manage.setForeground(new Color(0, 0, 0));
		btn_manage.setBackground(new Color(251, 251, 251));
		btn_manage.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_manage.setBounds(50, 300, 150, 35);
		btn_manage.addActionListener(e -> onManage());
		infobar.add(btn_manage);
		
		btn_info_notificationSingle = new JButton("Thông báo");
		btn_info_notificationSingle.setEnabled(false);
		btn_info_notificationSingle.setBorder(null);
		btn_info_notificationSingle.setForeground(new Color(0, 0, 0));
		btn_info_notificationSingle.setBackground(new Color(251, 251, 251));
		btn_info_notificationSingle.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_info_notificationSingle.setBounds(50, 350, 150, 35);
		btn_info_notificationSingle.addActionListener(e -> onNotificationSingle());
		infobar.add(btn_info_notificationSingle);
		
		btn_info_placeholder3 = new JButton("Khóa Máy");
		btn_info_placeholder3.setEnabled(false);
		btn_info_placeholder3.setForeground(new Color(0, 0, 0));
		btn_info_placeholder3.setBackground(new Color(251, 251, 251));
		btn_info_placeholder3.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_info_placeholder3.setBounds(50, 400, 150, 35);
		btn_info_placeholder3.setBorder(null);
		btn_info_placeholder3.addActionListener(e -> openLockServer());
		infobar.add(btn_info_placeholder3);
		
		btn_info_placeholder4 = new JButton("Button 4");
		btn_info_placeholder4.setEnabled(false);
		btn_info_placeholder4.setBorder(null);
		btn_info_placeholder4.setForeground(new Color(0, 0, 0));
		btn_info_placeholder4.setBackground(new Color(251, 251, 251));
		btn_info_placeholder4.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_info_placeholder4.setBounds(50, 450, 150, 35);
		btn_info_placeholder4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		infobar.add(btn_info_placeholder4);
		
		JPanel dashboard_options = new JPanel();
		dashboard_options.setBackground(mau2D6A4F);
		dashboard_options.setBounds(0, 10, 975, 60);
		dashboard_options.setLayout(null);
		dashboard.add(dashboard_options);
		
		JButton btn_do_lkClient = new JButton("Liên kết Client");
		btn_do_lkClient.setForeground(Color.BLACK);
		btn_do_lkClient.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_do_lkClient.setBackground(new Color(251, 251, 251));
		btn_do_lkClient.setBounds(10, 10, 150, 35);
		btn_do_lkClient.setBorder(null);
		btn_do_lkClient.addActionListener(e -> onlkClient());
		dashboard_options.add(btn_do_lkClient);

		JButton btn_do_lanScan = new JButton("Quét mạng tìm Client");
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
		
		JButton btn_do_notificationAll = new JButton("Thông báo tất cả");
		btn_do_notificationAll.setForeground(Color.BLACK);
		btn_do_notificationAll.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_do_notificationAll.setBackground(new Color(251, 251, 251));
		btn_do_notificationAll.setBounds(330, 10, 150, 35);
		btn_do_notificationAll.setBorder(null);
		//Câu lệnh chức năng 
		btn_do_notificationAll.addActionListener(e -> onNotificationAll());
		dashboard_options.add(btn_do_notificationAll);
		
		JButton btn_do_watch = new JButton("Quan sát all Client");
		btn_do_watch.setForeground(Color.BLACK);
		btn_do_watch.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_do_watch.setBackground(new Color(251, 251, 251));
		btn_do_watch.setBounds(490, 10, 150, 35);
		btn_do_watch.setBorder(null);
		btn_do_watch.addActionListener(e -> showWatchController());

		dashboard_options.add(btn_do_watch);
		
		JButton btn_do_whiteBoard = new JButton("White Board");
		btn_do_whiteBoard.setForeground(Color.BLACK);
		btn_do_whiteBoard.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn_do_whiteBoard.setBackground(new Color(251, 251, 251));
		btn_do_whiteBoard.setBounds(650, 10, 150, 35);
		btn_do_whiteBoard.setBorder(null);
		btn_do_whiteBoard.addActionListener(e -> openWhiteBoardServer());
		dashboard_options.add(btn_do_whiteBoard);

        JButton bton_nopbaitap = new JButton("Baitap");
        bton_nopbaitap.setForeground(Color.BLACK);
        bton_nopbaitap.setFont(new Font("Tahoma", Font.BOLD, 12));
        bton_nopbaitap.setBackground(new Color(251, 251, 251));
        bton_nopbaitap.setBounds(810, 10, 150, 35);
        bton_nopbaitap.setBorder(null);
        bton_nopbaitap.addActionListener(e -> onExercise());
        dashboard_options.add(bton_nopbaitap);
		
		JTextArea ta_log = new JTextArea();
		ta_log.setBackground(mauD8F3DC);
		ta_log.setLineWrap(true);
		ta_log.setEditable(false);
		ta_log.setBounds(0, 475, 975, 225);
		dashboard.add(ta_log);
		
		client_dashboard = new JPanel();
		client_dashboard.setBackground(new Color(245, 245, 245));
		client_dashboard.setBounds(75, 75, 975, 400);
		client_dashboard.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
		
		JScrollPane client_dashboardScrollPane = new JScrollPane(client_dashboard);
		client_dashboardScrollPane.setBorder(null);
		client_dashboardScrollPane.setBounds(0, 75, 975, 400);
		client_dashboardScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		client_dashboardScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		dashboard.add(client_dashboardScrollPane);
		
		return dashboard;
	}
	
	/**
	 * Create a new client item panel with selection callback
	 * @param name The name/address of the client
	 * @param isConnected Whether the client is currently connected
	 * @return A new Client_dashboard_JPanel instance
	 */
	public static Client_dashboard_JPanel createClientItem(String name, boolean isConnected) {
		return new Client_dashboard_JPanel(name, isConnected,
			new Client_dashboard_JPanel.OnClientItemSelected() {
				@Override
				public void onSelected(Client_dashboard_JPanel panel, String clientName, boolean connected) {
					// Deselect all other items
					for (JPanel item : client_dashboard_JPanelList) {
						if (item instanceof Client_dashboard_JPanel) {
							Client_dashboard_JPanel clientPanel = (Client_dashboard_JPanel) item;
							if (!clientPanel.equals(panel) && clientPanel.isSelected()) {
								clientPanel.deselect();
							}
						}
					}

					// Update the info panel
					if (connected) {
						lbl_info_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop.png", 150, 150));

                        currentSelectedClientName = clientName;
                        btn_manage.setEnabled(true);
                        btn_info_notificationSingle.setEnabled(true);
                        btn_info_placeholder3.setEnabled(true);
                        btn_info_placeholder4.setEnabled(true);
					} else {
						lbl_info_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop_off.png", 150, 150));
					}
					lbl_info_name.setText(clientName);
				}

				@Override
				public void onDeselected() {
					lbl_info_icon.setIcon(null);
					lbl_info_name.setText("Computer name");

                    currentSelectedClientName = "none";
					btn_manage.setEnabled(false);
					btn_info_notificationSingle.setEnabled(false);
					btn_info_placeholder3.setEnabled(false);
					btn_info_placeholder4.setEnabled(false);
				}
			},
			client_dashboard_JPanelList
		);
	}

    public static void setupClient_dashboard() {
        // clear all client_dashboard items first
        client_dashboard.removeAll();
        client_dashboard_JPanelList.clear();

        AtomicReference<String> client_name_atomic = new AtomicReference<>();

        String sql = "SELECT * FROM established_clients";
        JDBCUtil.runQuery(sql, rs -> {
            while (rs.next()) {
                client_name_atomic.set(rs.getString("client_name"));
                client_dashboard.add(createClientItem(client_name_atomic.get(), false));
                log.info("[Setting up] Loaded client '{}' into dashboard.", client_name_atomic.get());
            }
        });

        client_dashboard.revalidate();
        client_dashboard.repaint();
        log.info("Client dashboard setup complete.");
    }

    public static void client_dashboardNewClient(String client_name) {
        client_dashboard.add(createClientItem(client_name, false));
        client_dashboard.revalidate();
        client_dashboard.repaint();
    }

    public static void client_dashboardConnected(String client_name) {
        // Add a 300ms delay to ensure the panel is added to the list first
        Thread delayThread = new Thread(() -> {
            try { Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            SwingUtilities.invokeLater(() -> {
                // Find the client panel with matching name and update its connection status
                for (JPanel panel : client_dashboard_JPanelList) {
                    if (panel instanceof Client_dashboard_JPanel) {
                        Client_dashboard_JPanel clientPanel = (Client_dashboard_JPanel) panel;
                        String panelName = clientPanel.getClientName();
                        if (panelName.equals(client_name)) {
                            clientPanel.setConnected(true);
                            client_dashboard.revalidate();
                            client_dashboard.repaint();
                            break; // Found and updated, exit loop
                        }
                    }
                }
            });
        });
        delayThread.setName("Client-Connected-Delay-" + client_name);
        delayThread.setDaemon(true);
        delayThread.start();
    }

    public static void client_dashboardDisconnected(String client_name) {
        SwingUtilities.invokeLater(() -> {
            for (JPanel panel : client_dashboard_JPanelList) {
                if (panel instanceof Client_dashboard_JPanel) {
                    Client_dashboard_JPanel clientPanel = (Client_dashboard_JPanel) panel;
                    if (clientPanel.getClientName().equals(client_name)) {
                        clientPanel.setConnected(false);
                        client_dashboard.revalidate();
                        client_dashboard.repaint();
                        break; // Found and updated, exit loop
                    }
                }
            }
        });
    }

    public static boolean checkIsClientConnected(String client_name) {
        for (JPanel panel : client_dashboard_JPanelList) {
            if (panel instanceof Client_dashboard_JPanel) {
                Client_dashboard_JPanel clientPanel = (Client_dashboard_JPanel) panel;
                if (clientPanel.getClientName().equals(client_name)) {
                    return clientPanel.isClientConnected();
                }
            }
        }
        return false;
    }
    
    private static void onManage() {
        if (ServerStates.manage == null) {
            // check if currentSelectedClientName is online
            if (!checkIsClientConnected(currentSelectedClientName)) {
                Alert.showError("Client này hiện không kết nối. Vui lòng chọn Client khác.");

                lbl_info_icon.setIcon(null);
                lbl_info_name.setText("Computer name");

                currentSelectedClientName = "none";
                btn_manage.setEnabled(false);
                btn_info_notificationSingle.setEnabled(false);
                btn_info_placeholder3.setEnabled(false);
                btn_info_placeholder4.setEnabled(false);

                return;
            }

            ServerStates.manage = new Manage(currentSelectedClientName);
            ServerStates.manage.display();
        } else {
            Alert.showError("Vui lòng đóng cửa sổ quản lý hiện tại trước khi mở cửa sổ mới.");
        }
    }
    
    private static void onNotificationAll() {
        NotificationInputModal modal = new NotificationInputModal(frame);
        String message = modal.showDialog();

        if (message != null && !message.isEmpty()) {
            if (ServerStates.onNotificationAllRequestListener != null) ServerStates.onNotificationAllRequestListener.onNotificationAllRequest(message);
        }
    }
    
    private static void onNotificationSingle() {
        NotificationInputModal modal = new NotificationInputModal(frame);
        String message = modal.showDialog();

        if (message != null && !message.isEmpty()) {
            if (ServerStates.onNotificationSingleRequestListener != null) ServerStates.onNotificationSingleRequestListener.onNotificationSingleRequest(currentSelectedClientName, message);
        }
    }

    private static void openWhiteBoardServer() {
        if (ServerStates.onWhiteBoardControllerShowListener != null) ServerStates.onWhiteBoardControllerShowListener.onWhiteBoardControllerShow();
    }

    public static void onExercise() {
        // Gọi đúng lớp trung gian quản lý trạng thái
        org.example.server.ServerStates.fireExerciseViewShow();
    }

    private static boolean isLocked = false;

    private static void openLockServer() {
        if (ServerStates.onLockListener == null) return;
        if (!isLocked) {
            // gửi yêu cầu LOCK
            ServerStates.onLockListener.onLock("LOCK");

            btn_info_placeholder3.setText("Mở khóa");
            btn_info_placeholder3.setBackground(Color.RED);
        } else {
            // gửi yêu cầu UNLOCK
            ServerStates.onLockListener.onLock("UNLOCK");

            btn_info_placeholder3.setText("Khóa máy");
            btn_info_placeholder3.setBackground(new Color(251, 251, 251));
        }
        isLocked = !isLocked;
    }
    
	public static void onlkClient() {
        ServerStates.lkModal = new LienKetModal(frame);
        ServerStates.lkModal.setVisible(true);
    }
	
	
    public  WatchView view = new WatchView();
	
	public static void showWatchController() {
	    if (ServerStates.onWatchControllerShowListener != null) {
	        ServerStates.onWatchControllerShowListener.onWatchControllerShow();
	    }
	}
}
