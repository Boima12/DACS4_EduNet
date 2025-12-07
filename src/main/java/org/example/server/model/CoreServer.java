package org.example.server.model;

import org.example.common.utils.network.NetworkUtils;
import org.example.server.Config;
import org.example.server.controller.ServerNetwork;
import org.example.server.view.Dashboard;
import org.example.server.view.login.Login;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * run view/Login.java to let user login
 *
 */
public class CoreServer {

    private final String localIP = NetworkUtils.getLocalIPAddress();
    private Login loginWindow;
    private Dashboard dashBoardWindow;

    public CoreServer() throws IOException {
        // start server network
        ServerSocket serverSocket = new ServerSocket(Config.SERVER_PORT);
        ServerNetwork serverNetwork = new ServerNetwork(serverSocket);
        Thread serverThread = new Thread(serverNetwork::startServer);
        serverThread.start();
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            loginWindow = new Login();
            dashBoardWindow = new Dashboard(localIP + ":" + Config.SERVER_PORT);

            loginWindow.setOnLoginListenerCallback(() -> {
                loginWindow.undisplay();
                dashBoardWindow.display();
            });

            loginWindow.display();
        });
    }
}
