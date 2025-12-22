package org.example.server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File Network của Server
 * File này là ServerSocket chính, tạo các thread ServerNetworkHandler để xử lý từng client một.
 * Code xử lý network cho bên client nằm ở ServerNetworkHandler.java
 *
 */
public class ServerNetwork {

	private final ServerSocket server;
    public static ArrayList<ServerNetworkHandler> clients = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(ServerNetwork.class);
	
	public ServerNetwork(ServerSocket server) {
		this.server = server;
	}
	
	public void startServer() {
        try {
            while (!server.isClosed()) {
                // accept client when connect to the server
                Socket clientSocket = server.accept();
                ServerNetworkHandler serverNetworkHandler = new ServerNetworkHandler(clientSocket);
                clients.add(serverNetworkHandler);
            }
        } catch (IOException e) {
            log.error(String.valueOf(e));
            closeServer();
        }
    }

    public void closeServer() {
        try {
            if (server != null) {
                server.close();
            }

        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
    }
}
