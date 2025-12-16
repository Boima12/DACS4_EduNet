package org.example.server.view.dashboard;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;

import org.example.common.utils.gui.ImageHelper;
import org.example.common.utils.gui.RoundedBorder;
import org.example.common.utils.gui.WrapLayout;
import org.example.server.ServerStates;
import org.example.server.model.database.JDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PatternVariableCanBeUsed", "Convert2Lambda"})
public class Dashboard {

	private static JFrame frame;
	private static JLabel lbl_info_icon;
	private static JLabel lbl_info_name;
	public static JPanel client_dashboard;
	public static ArrayList<JPanel> client_dashboard_JPanelList = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(Dashboard.class);

	/**
	 * Create the application.
	 */
	public Dashboard() {
		initialize();	// line này dùng để bật WindowBuilder, nếu comment line này sẽ tối ưu ứng dụng nhưng không thể sài windowBuilder trong file này
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1111, 730);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(build());
	}
	
	public static JPanel build() {
		JPanel dashboard = new JPanel();
		dashboard.setLayout(null);
		dashboard.setBackground(new Color(251, 251, 251));
		dashboard.setBounds(75, 0, 1111, 730);
		
		JPanel infobar = new JPanel();
		infobar.setBackground(new Color(251, 251, 251));
		infobar.setBounds(861, 0, 250, 730);
		infobar.setLayout(null);
		dashboard.add(infobar);
		
		lbl_info_icon = new JLabel("");
		lbl_info_icon.setBackground(new Color(240, 240, 240));
		lbl_info_icon.setBounds(50, 75, 150, 150);
		infobar.add(lbl_info_icon);
		
		lbl_info_name = new JLabel("Computer name");
		lbl_info_name.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_info_name.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_info_name.setBounds(10, 250, 230, 30);
		infobar.add(lbl_info_name);
		
		JButton btn_info_placeholder1 = new JButton("Button 1");
		btn_info_placeholder1.setBorder(new RoundedBorder(8));
		btn_info_placeholder1.setForeground(new Color(0, 0, 0));
		btn_info_placeholder1.setBackground(new Color(251, 251, 251));
		btn_info_placeholder1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_info_placeholder1.setBounds(50, 350, 150, 35);
		btn_info_placeholder1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		infobar.add(btn_info_placeholder1);
		
		JButton btn_info_placeholder2 = new JButton("Button 2");
		btn_info_placeholder2.setBorder(new RoundedBorder(8));
		btn_info_placeholder2.setForeground(new Color(0, 0, 0));
		btn_info_placeholder2.setBackground(new Color(251, 251, 251));
		btn_info_placeholder2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_info_placeholder2.setBounds(50, 413, 150, 35);
		btn_info_placeholder2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		infobar.add(btn_info_placeholder2);
		
		JButton btn_info_placeholder3 = new JButton("Button 3");
		btn_info_placeholder3.setBorder(new RoundedBorder(8));
		btn_info_placeholder3.setForeground(new Color(0, 0, 0));
		btn_info_placeholder3.setBackground(new Color(251, 251, 251));
		btn_info_placeholder3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_info_placeholder3.setBounds(50, 476, 150, 35);
		btn_info_placeholder3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		infobar.add(btn_info_placeholder3);
		
		JButton btn_info_placeholder4 = new JButton("Button 4");
		btn_info_placeholder4.setBorder(new RoundedBorder(8));
		btn_info_placeholder4.setForeground(new Color(0, 0, 0));
		btn_info_placeholder4.setBackground(new Color(251, 251, 251));
		btn_info_placeholder4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_info_placeholder4.setBounds(50, 539, 150, 35);
		btn_info_placeholder4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		infobar.add(btn_info_placeholder4);
		
		JPanel dashboard_options = new JPanel();
		dashboard_options.setBackground(new Color(251, 251, 251));
		dashboard_options.setBounds(0, 0, 862, 60);
		dashboard_options.setLayout(null);
		dashboard.add(dashboard_options);
		
		JButton btn_do_lkClient = new JButton("Liên kết Client");
		btn_do_lkClient.setForeground(Color.BLACK);
		btn_do_lkClient.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_do_lkClient.setBorder(new RoundedBorder(8));
		btn_do_lkClient.setBackground(new Color(251, 251, 251));
		btn_do_lkClient.setBounds(15, 10, 150, 35);
		btn_do_lkClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onlkClient();
			}
		});
		dashboard_options.add(btn_do_lkClient);

		JButton btn_do_lanScan = new JButton("Quét mạng tìm Client");
		btn_do_lanScan.setForeground(Color.BLACK);
		btn_do_lanScan.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btn_do_lanScan.setBorder(new RoundedBorder(8));
		btn_do_lanScan.setBackground(new Color(251, 251, 251));
		btn_do_lanScan.setBounds(185, 10, 150, 35);
		btn_do_lanScan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		dashboard_options.add(btn_do_lanScan);
		
		JButton btn_do_placeholder3 = new JButton("Button 3");
		btn_do_placeholder3.setForeground(Color.BLACK);
		btn_do_placeholder3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_do_placeholder3.setBorder(new RoundedBorder(8));
		btn_do_placeholder3.setBackground(new Color(251, 251, 251));
		btn_do_placeholder3.setBounds(355, 10, 150, 35);
		btn_do_placeholder3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		dashboard_options.add(btn_do_placeholder3);
		
		JButton btn_do_placeholder4 = new JButton("Button 4");
		btn_do_placeholder4.setForeground(Color.BLACK);
		btn_do_placeholder4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_do_placeholder4.setBorder(new RoundedBorder(8));
		btn_do_placeholder4.setBackground(new Color(251, 251, 251));
		btn_do_placeholder4.setBounds(525, 10, 150, 35);
		btn_do_placeholder4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		dashboard_options.add(btn_do_placeholder4);
		
		JButton btn_do_placeholder5 = new JButton("Button 5");
		btn_do_placeholder5.setForeground(Color.BLACK);
		btn_do_placeholder5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_do_placeholder5.setBorder(new RoundedBorder(8));
		btn_do_placeholder5.setBackground(new Color(251, 251, 251));
		btn_do_placeholder5.setBounds(695, 10, 150, 35);
		btn_do_placeholder5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		dashboard_options.add(btn_do_placeholder5);
		
		JTextArea ta_log = new JTextArea();
		ta_log.setBackground(new Color(240, 240, 240));
		ta_log.setLineWrap(true);
		ta_log.setEditable(false);
		ta_log.setBounds(0, 495, 862, 235);
		dashboard.add(ta_log);
		
		client_dashboard = new JPanel();
		client_dashboard.setBackground(new Color(245, 245, 245));
		client_dashboard.setBounds(75, 59, 862, 435);
		client_dashboard.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
		JScrollPane client_dashboardScrollPane = new JScrollPane(client_dashboard);
		client_dashboardScrollPane.setBorder(null);
		client_dashboardScrollPane.setBounds(0, 59, 862, 435);
		client_dashboardScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		client_dashboardScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		dashboard.add(client_dashboardScrollPane);
		
		return dashboard;
	}

	public static void onlkClient() {
        ServerStates.lkModal = new LienKetModal(frame);
        ServerStates.lkModal.setVisible(true);
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
					} else {
						lbl_info_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop_off.png", 150, 150));
					}
					lbl_info_name.setText(clientName);
				}

				@Override
				public void onDeselected() {
					lbl_info_icon.setIcon(null);
					lbl_info_name.setText("Computer name");
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
            try {
                Thread.sleep(300);
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
}
