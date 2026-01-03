package org.example.server.model;

import org.example.common.utils.gui.Alert;
import org.example.common.utils.network.NetworkUtils;
import org.example.server.Config;
import org.example.server.ServerStates;
import org.example.server.controller.ServerNetwork;
import org.example.server.controller.ServerNetworkHandler;
import org.example.server.controller.services.exercise.ExerciseController;
import org.example.server.controller.services.lock.LockController;
import org.example.server.controller.services.watch.WatchController;
import org.example.server.controller.services.whiteBoard.WhiteBoardController;
import org.example.server.view.ServerUI;
import org.example.server.view.exercise.ExerciseView;
import org.example.server.view.login.Login;
import org.example.server.view.dashboard.Dashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.net.ServerSocket;

/**
 * Trung tâm chỉ huy cho cả Server
 *
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr", "FieldMayBeFinal", "SynchronizeOnNonFinalField"})
public class CoreServer {

    private static ServerNetwork serverNetwork;
    private final String localIP = NetworkUtils.getLocalIPAddress();
    private static int id_port_whiteboard = 6061;
    private static int id_port_watch = 6062;
    private static final int ID_PORT_LOCK       = 6063;
    private static final int ID_PORT_EXERCISE   = 6064;

    private Login loginWindow;
    private ServerUI serverUIWindow;
    private static WatchController watchController;
    private static WhiteBoardController serverWBController;
    private static LockController lockController;
    private static ExerciseController exerciseController;

    private static final Logger log = LoggerFactory.getLogger(CoreServer.class);

    public CoreServer() {
        // start server network
        ServerStates.setOnServerStartListenerCallback(() -> {
            ServerSocket serverSocket = new ServerSocket(Config.SERVER_PORT);
            serverNetwork = new ServerNetwork(serverSocket);
            Thread serverThread = new Thread(serverNetwork::startServer);
            serverThread.start();

            log.info("Server Started on {}:" + Config.SERVER_PORT, localIP);
            Alert.showInfo("Server khởi động thành công.");

            // setup Dashboard.client_dashboard
            Dashboard.setupClient_dashboard();
        });

        // turn off server network
        ServerStates.setOnServerCloseListenerCallback(() -> {
            if (serverNetwork != null) {
                serverNetwork.closeServer();
                log.info("Server Closed on {}:" + Config.SERVER_PORT, localIP);
                Alert.showInfo("Server tắt nguồn thành công");
            }
        });
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            loginWindow = new Login();
            serverUIWindow = new ServerUI(localIP + ":" + Config.SERVER_PORT);
            serverWBController = new WhiteBoardController(id_port_whiteboard);
            watchController = new WatchController(id_port_watch);
            lockController     = new LockController(ID_PORT_LOCK);
            exerciseController = new ExerciseController();

            lockController.start();

            loginWindow.setOnLoginListenerCallback(() -> {
                loginWindow.undisplay();
                serverUIWindow.display();
            });

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
                // find the right client in ServerNetwork.clients base on client_name and request system info
                synchronized (ServerNetwork.clients) {
                    for (var client : ServerNetwork.clients) {
                        if (client.getClient_name().equals(client_name)) {
                            if (!client.isClosed()) {
                                client.speak_systemInfoRequest();
                            } else {
                                System.out.println("[SystemInfoRequestListener] Client " + client_name + " connection is closed, did the old one not remove from ArrayList correctly?");
                            }
                            break;
                        }
                    }
                }
            });

            ServerStates.setOnNotificationAllRequestListenerCallback((message) -> {
                synchronized (ServerNetwork.clients) {
                    for (var client : ServerNetwork.clients) {
                        if (!client.isClosed()) {
                            client.speak_notificationRequest(message);
                        } else {
                            System.out.println("[NotificationAllRequestListener] Client " + client.getClient_name() + " connection is closed, did this old one not remove from ArrayList correctly?.");
                        }
                    }
                }
            });

            ServerStates.setOnNotificationSingleRequestListenerCallback((client_name, message) -> {
                synchronized (ServerNetwork.clients) {
                    for (var client : ServerNetwork.clients) {
                        if (client.getClient_name().equals(client_name)) {
                            if (!client.isClosed()) {
                                client.speak_notificationRequest(message);
                            } else {
                                System.out.println("[NotificationSingleRequestListener] Client " + client_name + " connection is closed, did the old one not remove from ArrayList correctly?");
                            }
                            break;
                        }
                    }
                }
            });

            ServerStates.setOnWatchControllerShowListenerCallback(() -> {
                if (watchController != null) {
                    watchController.showWatchView();
                } else {
                    // Đề phòng trường hợp khởi tạo lỗi
                    watchController = new WatchController(id_port_watch);
                    watchController.showWatchView();
                }
            });

            ServerStates.setOnWhiteBoardControllerShowListenerCallback(() -> {
                try {
                    // WhiteBoardController nên được quản lý static tương tự watchController
                    // để tránh mở nhiều Server Socket cùng lúc gây lỗi "Address already in use"
                    serverWBController.showWindow();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Lỗi mở WhiteBoard: " + ex.getMessage());
                }
            });

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

            ServerNetworkHandler.setExerciseController(this.exerciseController);

            loginWindow.display();
        });
    }

    // Phương thức hỗ trợ truy cập Controller từ bên ngoài nếu cần
    public static ExerciseController getExerciseController() {
        return exerciseController;
    }
}
