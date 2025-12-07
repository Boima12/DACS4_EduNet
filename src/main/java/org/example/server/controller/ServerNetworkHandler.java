package org.example.server.controller;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.common.objects.messages.EstablishingResponseJSON;
import org.example.common.utils.gson.GsonHelper;
import org.example.common.utils.network.NetworkUtils;
import org.example.server.States;
import org.example.server.model.database.JDBCUtil;
import org.example.server.view.UIState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerNetworkHandler {

    public static ArrayList<ServerNetworkHandler> clients = new ArrayList<>();
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;

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
                            onEstablishingRequest(json);
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

    private void onEstablishingRequest(JsonObject json) {
        int maLienKet = json.get("maLienKet").getAsInt();

        // if maLienKet is match with server states.MA_LIEN_KET
        if (maLienKet == States.MA_LIEN_KET) {
            // generate client_token
            String client_token = generateClientToken();

            // save client_token into table established_clients
            String sql = "INSERT INTO established_clients (client_token) VALUES (?)";
            JDBCUtil.runUpdate(sql, client_token);

            // send back EstablishingResponseJSON with approved = true
            EstablishingResponseJSON establishingResponseJSON = new EstablishingResponseJSON();
            establishingResponseJSON.approval = true;
            establishingResponseJSON.client_token = client_token;
            establishingResponseJSON.server_IP = NetworkUtils.getLocalIPAddress();
            establishingResponseJSON.server_port = 6060;
            String jsonString = GsonHelper.toJson(establishingResponseJSON);
            speak(jsonString);

            // close lkClient_modal window if it's opened in server GUI
            if (UIState.lkModal != null) {
                UIState.lkModal.dispose();
                UIState.lkModal = null;
            }
        } else {
            // send back EstablishingResponseJSON with approved = false
            EstablishingResponseJSON establishingResponseJSON = new EstablishingResponseJSON();
            establishingResponseJSON.approval = false;
            String jsonString = GsonHelper.toJson(establishingResponseJSON);
            speak(jsonString);
        }
    }
}
