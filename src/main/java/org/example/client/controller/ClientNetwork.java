package org.example.client.controller;

import java.io.*;
import java.net.Socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.client.States;
import org.example.common.objects.MemoryBox;
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
                            onEstablishingResponse(json);
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

    public void send_establishingRequest(int maLienKet) {
        EstablishingRequestJSON establishingRequestJSON = new EstablishingRequestJSON();
        establishingRequestJSON.maLienKet = maLienKet;
        String jsonString = GsonHelper.toJson(establishingRequestJSON);
        speak(jsonString);
    }

    public void onEstablishingResponse(JsonObject json) throws IOException {
        boolean approval = json.get("approval").getAsBoolean();

        if (approval) {
            String client_token = json.get("client_token").getAsString();
            String server_IP = json.get("server_IP").getAsString();
            String server_port = json.get("server_port").getAsString();

            MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
            memoryBox.serverConnection = "yesEstablished";
            memoryBox.token = client_token;
            memoryBox.server_IP = server_IP;
            memoryBox.server_port = server_port;
            GsonHelper.writeJsonFile(runtimeJsonFile.getPath(), memoryBox);

            if (States.onEstablishListener != null) States.onEstablishListener.onEstablish();
        } else {
            log.info("Connection denied by server.");
            Alert.showError("Mã liên kết không hợp lệ.");
        }
    }
}
