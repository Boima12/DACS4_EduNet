package org.example.client.controller;

import java.io.*;
import java.net.Socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.client.States;
import org.example.common.objects.MemoryBox;
import org.example.common.objects.messages.ConnectionRequestJSON;
import org.example.common.objects.messages.EstablishingRequestJSON;
import org.example.common.utils.gson.GsonHelper;
import org.example.common.utils.gui.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientNetwork {

    private final Socket clientSocket;
    private final BufferedReader in;
    private final BufferedWriter out;

    private static final Logger log = LoggerFactory.getLogger(ClientNetwork.class);
    private final File runtimeJsonFile = new File("localStorage/memoryBox.json");

    public ClientNetwork(String serverIP, int port) throws IOException {
        this.clientSocket = new Socket(serverIP, port);
        in = new BufferedReader(new InputStreamReader(new DataInputStream(clientSocket.getInputStream())));
        out = new BufferedWriter(new OutputStreamWriter(new DataOutputStream(clientSocket.getOutputStream())));
        hear();
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
                        case "establishingResponse":
                            hear_establishingResponse(json);
                            break;

                        case "connectionResponse":
                            hear_connectionResponse(json);
                            break;

                        default:
                            log.warn("Unknown message type: {}", type);
                            break;
                    }
                }
            } catch (Exception e) {
                log.info("This client hear() has stopped, calling closeEverything()");
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
        log.info("Client {} disconnected.", clientSocket != null ? clientSocket.getInetAddress() : null);

        try {
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


    //  == Establishing (Thiết lập kết nối lần đầu) ==
    public void send_establishingRequest(int maLienKet) {
        EstablishingRequestJSON establishingRequestJSON = new EstablishingRequestJSON();
        establishingRequestJSON.maLienKet = maLienKet;
        String jsonString = GsonHelper.toJson(establishingRequestJSON);
        speak(jsonString);
    }

    public void hear_establishingResponse(JsonObject json) throws IOException {
        boolean approval = json.get("approval").getAsBoolean();

        if (approval) {
            String client_token = json.get("client_token").getAsString();
            String server_IP = json.get("server_IP").getAsString();
            String server_port = json.get("server_port").getAsString();

            MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
            assert memoryBox != null;
            memoryBox.serverConnection = "yesEstablished";
            memoryBox.token = client_token;
            memoryBox.server_IP = server_IP;
            memoryBox.server_port = server_port;
            GsonHelper.writeJsonFile(runtimeJsonFile.getPath(), memoryBox);

            if (States.onEstablishListener != null) States.onEstablishListener.onEstablish();
            closeEverything();
        } else {
            log.info("Connection denied by server.");
            Alert.showError("Mã liên kết không hợp lệ.");
            closeEverything();
        }
    }


    //  == Connection (Kết nối với server) ==
    public void send_connectionRequest(String client_token) {
        ConnectionRequestJSON connectionRequestJSON = new ConnectionRequestJSON();
        connectionRequestJSON.client_token = client_token;
        String jsonString = GsonHelper.toJson(connectionRequestJSON);
        speak(jsonString);
    }

    public void hear_connectionResponse(JsonObject json) {
        boolean isLinked = json.get("isLinked").getAsBoolean();

        if (isLinked) {
            log.info("Connection approved by server.");
            if (States.onConnectionListener != null) States.onConnectionListener.onConnection();
        } else {
            log.info("Connection denied by server.");
            Alert.showError("Kết nối bị từ chối bởi server, nếu cần thiết lập kết nối mới, hãy xóa file localStorage/memoryBox.json.");
            closeEverything();
        }
    }
}





















