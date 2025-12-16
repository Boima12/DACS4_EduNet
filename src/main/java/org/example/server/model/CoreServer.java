package org.example.server.model;

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
 * run view/Login.java to let user login
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

            // setup Dashboard.client_dashboard
            Dashboard.setupClient_dashboard();
        });

        // turn off server network
        ServerStates.setOnServerCloseListenerCallback(() -> {
            if (serverNetwork != null) {
                serverNetwork.closeServer();
                log.info("Server Closed on {}:" + Config.SERVER_PORT, localIP);
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

            loginWindow.display();
        });
    }
}
