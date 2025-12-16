package org.example.server.controller;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.example.common.objects.messages.ConnectionResponseJSON;
import org.example.common.objects.messages.EstablishingResponseJSON;
import org.example.common.utils.gson.GsonHelper;
import org.example.common.utils.network.NetworkUtils;
import org.example.server.ServerStates;
import org.example.server.model.database.JDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerNetworkHandler {

    public static ArrayList<ServerNetworkHandler> clients = new ArrayList<>();
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String client_name;

    private static final Logger log = LoggerFactory.getLogger(ServerNetworkHandler.class);

    public ServerNetworkHandler(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(new DataInputStream(clientSocket.getInputStream())));
            out = new BufferedWriter(new OutputStreamWriter(new DataOutputStream(clientSocket.getOutputStream())));
            clients.add(this);
            hear();
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
    }

    public void hear() {
        Thread thread = new Thread(() -> {
            try {
                while (clientSocket.isConnected()) {
                    String msg = in.readLine();
                    if (msg == null) {
                        closeEverything();
                        break;
                    }
                    log.info("Received: {}", msg);

                    JsonObject json = JsonParser.parseString(msg).getAsJsonObject();
                    String type = json.has("type") ? json.get("type").getAsString() : null;
                    if (type == null) {
                        log.warn("Message without type: {}", msg);
                        continue;
                    }

                    // Route theo type
                    switch (type) {
                        case "establishingRequest":
                            hear_establishingRequest(json);
                            break;

                        case "connectionRequest":
                            hear_connectionRequest(json);
                            break;

                        default:
                            log.warn("Unknown message type: {}", type);
                            break;
                    }
                }
            } catch (Exception e) {
                closeEverything();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void speak(String message) {
        log.info("Sending: {}", message);
        try {
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            log.error(String.valueOf(e));
            closeEverything();
        }
    }

    public void closeEverything() {
        try {
            log.info("Client {} - {} disconnected.", client_name, clientSocket.getInetAddress());

            // update dashboard UI.
            if (ServerStates.onClient_dashboardDisconnectedListener != null) ServerStates.onClient_dashboardDisconnectedListener.onClient_dashboardDisconnected(client_name);

            // removing this one client from clients ArrayList
            clients.remove(this);

            if (clientSocket != null) {
                clientSocket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
    }

    private String generateClientToken() {
        byte[] bytes = new byte[64]; // 64 bytes = 512 bits
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);

        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }


//  == Establishing (Thiết lập kết nối lần đầu) ==
    private void hear_establishingRequest(JsonObject json) {
        client_name = json.get("client_name").getAsString();
        int maLienKet = json.get("maLienKet").getAsInt();

        // if maLienKet is match with server states.MA_LIEN_KET
        if (maLienKet == ServerStates.MA_LIEN_KET) {
            // generate client_token
            String client_token = generateClientToken();

            // save established client into table established_clients
            String sql = "INSERT INTO established_clients (clientInetAddress, client_name, client_token) VALUES (?, ?, ?)";
            JDBCUtil.runUpdate(sql, String.valueOf(clientSocket.getInetAddress()), client_name, client_token);

            // send back EstablishingResponseJSON with approved = true
            EstablishingResponseJSON establishingResponseJSON = new EstablishingResponseJSON();
            establishingResponseJSON.approval = true;
            establishingResponseJSON.client_name = client_name;
            establishingResponseJSON.client_token = client_token;
            establishingResponseJSON.server_IP = NetworkUtils.getLocalIPAddress();
            establishingResponseJSON.server_port = 6060;
            String jsonString = GsonHelper.toJson(establishingResponseJSON);
            speak(jsonString);

            // close lkClient_modal window if it's opened in server GUI
            if (ServerStates.lkModal != null) {
                ServerStates.lkModal.turnOffLinkMode();
                ServerStates.lkModal = null;
            }
            
            // Calling setOnClient_dashboardNewClientListenerCallback() in CoreServer
            if (ServerStates.onClient_dashboardNewClientListener != null) ServerStates.onClient_dashboardNewClientListener.onClient_dashboardNewClient(client_name);
            
        } else {
            // send back EstablishingResponseJSON with approved = false
            EstablishingResponseJSON establishingResponseJSON = new EstablishingResponseJSON();
            establishingResponseJSON.approval = false;
            String jsonString = GsonHelper.toJson(establishingResponseJSON);
            speak(jsonString);
        }
    }


//  == Connection (Kết nối với server) ==
    private void hear_connectionRequest(JsonObject json) {
        String client_token = json.get("client_token").getAsString();

        // check if client_token exists in table established_clients
        AtomicReference<Integer> count = new AtomicReference<>();
        String sql = "SELECT COUNT(*) FROM established_clients WHERE client_token = ?";
        JDBCUtil.runQuery(sql, rs -> {
            while (rs.next()) {
                count.set(rs.getInt("COUNT(*)"));
            }
        }, client_token);

        if (count.get() > 0) {
            // retrieve client_name from database
            AtomicReference<String> client_name_atomic = new AtomicReference<>();
            String sql_get_name = "SELECT client_name FROM established_clients WHERE client_token = ?";
            JDBCUtil.runQuery(sql_get_name, rs -> {
                while (rs.next()) {
                    client_name_atomic.set(rs.getString("client_name"));
                }
            }, client_token);
            client_name = client_name_atomic.get();

            // connection approved
            ConnectionResponseJSON connectionResponseJSON = new ConnectionResponseJSON();
            connectionResponseJSON.isLinked = true;
            String jsonString = GsonHelper.toJson(connectionResponseJSON);
            speak(jsonString);

            // update dashboard UI.
            if (ServerStates.onClient_dashboardConnectedListener != null) ServerStates.onClient_dashboardConnectedListener.onClient_dashboardConnected(client_name);

            log.info("Client {} - {} connected successfully.", client_name, clientSocket.getInetAddress());

        } else {
            // connection denied
            ConnectionResponseJSON connectionResponseJSON = new ConnectionResponseJSON();
            connectionResponseJSON.isLinked = false;
            String jsonString = GsonHelper.toJson(connectionResponseJSON);
            speak(jsonString);

            log.info("Client {} with token {} failed to connect: invalid token.", clientSocket.getInetAddress(), client_token);
        }
    }
}
