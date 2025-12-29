package org.example.server.model;

import org.example.server.controller.ServerNetwork;
import org.example.server.controller.ServerNetworkHandler;
import org.example.server.controller.services.exercise.ExerciseController;
import org.example.server.controller.services.lock.LockController;
import org.example.server.controller.services.watch.WatchController;
import org.example.server.controller.services.whiteBoard.WhiteBoardController;
import org.example.server.ServerStates;
import org.example.server.Config;
import org.example.server.view.ServerUI;
import org.example.server.view.dashboard.Dashboard;
import org.example.server.view.exercise.ExerciseView;
import org.example.server.view.login.Login;
import org.example.common.utils.gui.Alert;
import org.example.common.utils.network.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.net.ServerSocket;

@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr", "FieldMayBeFinal"})
public class CoreServer {

    private static ServerNetwork serverNetwork;
    private final String localIP = NetworkUtils.getLocalIPAddress();

    private static final int ID_PORT_WHITEBOARD = 6061;
    private static final int ID_PORT_WATCH      = 6062;
    private static final int ID_PORT_LOCK       = 6063;
    // Cổng này có thể dùng cho Socket riêng hoặc quản lý logic trong ServerNetwork
    private static final int ID_PORT_EXERCISE   = 6064; 

    private Login loginWindow;
    private ServerUI serverUIWindow;

    private static WatchController watchController;
    private static WhiteBoardController serverWBController;
    private static LockController lockController;
    private static ExerciseController exerciseController;

    private static final Logger log = LoggerFactory.getLogger(CoreServer.class);

 // TRONG CoreServer.java - Tìm đến Constructor CoreServer()
    public CoreServer() {
        ServerStates.setOnServerStartListenerCallback(() -> {
            try { // Thêm lại try-catch vì ServerSocket có thể ném IOException
                ServerSocket serverSocket = new ServerSocket(Config.SERVER_PORT);
                serverNetwork = new ServerNetwork(serverSocket);
                
                // --- PHẦN SỬA LỖI ---
                // Đăng ký: Khi ExerciseController phát tín hiệu đã tạo bài tập
                ServerStates.setOnAssignmentCreatedListener((assignment) -> {
                    if (serverNetwork != null) {
                        // Gọi hàm static từ ServerNetwork để gửi cho tất cả Client
                        ServerNetwork.broadcastAssignment(assignment);
                    }
                });

                // Đăng ký: Gửi phản hồi kết quả nộp bài (Thành công/Thất bại) về cho Client
                ServerStates.setOnSubmissionResultListenerCallback((clientName, success, message) -> {
                    if (serverNetwork != null) {
                        // Tìm client handler tương ứng trong list của ServerNetwork để gửi phản hồi
                        synchronized (ServerNetwork.clients) {
                            for (var handler : ServerNetwork.clients) {
                                if (handler.getClient_name().equals(clientName)) {
                                    handler.speak_submissionResult(success, message);
                                    break;
                                }
                            }
                        }
                    }
                });
                // ----------------------

                new Thread(serverNetwork::startServer).start();
                log.info("Server Started on {}:{}", localIP, Config.SERVER_PORT);
                Alert.showInfo("Server khởi động thành công");
                Dashboard.setupClient_dashboard();

            } catch (Exception e) {
                log.error("Không thể khởi động Server", e);
                Alert.showError("Khởi động server thất bại: " + e.getMessage());
            }
        });
    }
    
    public void start() {
        SwingUtilities.invokeLater(() -> {
            loginWindow        = new Login();
            serverUIWindow     = new ServerUI(localIP + ":" + Config.SERVER_PORT);
            serverWBController = new WhiteBoardController(ID_PORT_WHITEBOARD);
            watchController    = new WatchController(ID_PORT_WATCH);
            lockController     = new LockController(ID_PORT_LOCK);
            
            // 1. Khởi tạo ExerciseController
            exerciseController = new ExerciseController();

            lockController.start();
            
            loginWindow.setOnLoginListenerCallback(() -> {
                loginWindow.undisplay();
                serverUIWindow.display();
            });

            /* ================= CÁC LISTENERS BÀI TẬP (MỚI SỬA) ================= */

            // Lắng nghe yêu cầu hiển thị giao diện bài tập từ Dashboard
            ServerStates.setOnExerciseViewShowListenerCallback(() -> {
                if (exerciseController != null) {
                    // Tạo view mới, gắn vào controller và hiển thị
                    ExerciseView view = new ExerciseView();
                    exerciseController.setView(view);
                    
                    JFrame frame = new JFrame("Quản lý bài tập & Thu bài");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setContentPane(view);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });

            /* ================= CÁC LISTENERS CŨ (GIỮ NGUYÊN) ================= */
            
            ServerStates.setOnClient_dashboardNewClientListenerCallback((client_name) -> {
                Dashboard.client_dashboardNewClient(client_name);
            });
            
            ServerStates.setOnClient_dashboardConnectedCallback((client_name) -> {
                Dashboard.client_dashboardConnected(client_name);
            });

            ServerStates.setOnClient_dashboardDisconnectedCallback((client_name) -> {
                Dashboard.client_dashboardDisconnected(client_name);
            });

            ServerStates.setOnSystemInfoRequestListenerCallback((client_name) -> {
                for (var client : ServerNetwork.clients) {
                    if (client.getClient_name().equals(client_name)) {
                        client.speak_systemInfoRequest();
                        break;
                    }
                }
            });

            ServerStates.setOnNotificationAllRequestListenerCallback((message) -> {
                for (var client : ServerNetwork.clients) {
                    client.speak_notificationRequest(message);
                }
            });

            ServerStates.setOnNotificationSingleRequestListenerCallback((client_name, message) -> {
                for (var client : ServerNetwork.clients) {
                    if (client.getClient_name().equals(client_name)) {
                        client.speak_notificationRequest(message);
                        break;
                    }
                }
            });

            ServerStates.setOnWatchControllerShowListenerCallback(() -> {
                if (watchController != null) {
                    watchController.showWatchView();
                } else {
                    watchController = new WatchController(ID_PORT_WATCH);
                    watchController.showWatchView();
                }
            });

            ServerStates.setOnWhiteBoardControllerShowListenerCallback(() -> {
                try {
                    serverWBController.showWindow();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Lỗi mở WhiteBoard: " + ex.getMessage());
                }
            });
            
         // Trong phương thức start() hoặc lúc khởi tạo serverNetwork
            ServerNetworkHandler.setExerciseController(this.exerciseController);
            loginWindow.display();
        });
    }

    // Phương thức hỗ trợ truy cập Controller từ bên ngoài nếu cần
    public static ExerciseController getExerciseController() {
        return exerciseController;
    }  
}
