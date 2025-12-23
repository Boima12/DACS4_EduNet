package org.example.server.model;

import org.example.common.utils.gui.Alert;
import org.example.common.utils.network.NetworkUtils;
import org.example.server.Config;
import org.example.server.ServerStates;
import org.example.server.controller.ServerNetwork;
import org.example.server.view.ServerUI;
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
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public class CoreServer {

    private static ServerNetwork serverNetwork;
    private final String localIP = NetworkUtils.getLocalIPAddress();
    private Login loginWindow;
    private ServerUI serverUIWindow;

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

            loginWindow.display();
        });
    }
}
