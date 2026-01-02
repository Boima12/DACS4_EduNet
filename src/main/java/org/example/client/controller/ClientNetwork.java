package org.example.client.controller;

import java.io.*;
import java.net.Socket;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.client.ClientStates;
import org.example.client.model.services.systemInfo.SystemInfo;
import org.example.common.objects.MemoryBox;
import org.example.common.objects.messages.connection.ConnectionRequestJSON;
import org.example.common.objects.messages.establish.EstablishingRequestJSON;
import org.example.common.objects.services.exercise.Assignment;
import org.example.common.objects.services.exercise.Packet;
import org.example.common.objects.services.exercise.PacketType;
import org.example.common.objects.services.exercise.Submission;
import org.example.common.utils.gson.GsonHelper;
import org.example.common.utils.gui.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Code xử lý Network chính của Client.
 *
 */
public class ClientNetwork {

    private final Socket clientSocket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private String client_name; 

    private static final Logger log = LoggerFactory.getLogger(ClientNetwork.class);
    private final File runtimeJsonFile = new File("localStorage/memoryBox.json");

    public ClientNetwork(String serverIP, int port) throws IOException {
        clientSocket = new Socket(serverIP, port);
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

                        case "systemInfoRequest":
                        	hear_systemInfoRequest();
                        	break;

                        case "notificationRequest":
                            hear_notificationRequest(json);
                            break;

                        case "ASSIGNMENT_LIST":
                            hear_handleAssignmentList(json);
                            break;

                        case "SUBMISSION_RESULT", "ERROR":
                            hear_handleSubmissionResult(json);
                            break;

                        default:
                            log.warn("Unknown message type: {}", type);
                            break;
                    }
                }
            } catch (Exception e) {
                log.error(String.valueOf(e));
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
        log.info("Client {} - {} disconnected.", client_name, clientSocket != null ? clientSocket.getInetAddress() : null);

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
    public void send_establishingRequest(String client_name, int maLienKet) {
        EstablishingRequestJSON establishingRequestJSON = new EstablishingRequestJSON();
        establishingRequestJSON.client_name = client_name;
        establishingRequestJSON.maLienKet = maLienKet;
        String jsonString = GsonHelper.toJson(establishingRequestJSON);
        speak(jsonString);
    }

    private void hear_establishingResponse(JsonObject json) throws IOException {
        client_name = json.get("client_name").getAsString();
        boolean approval = json.get("approval").getAsBoolean();
        String denied_reason = json.get("denied_reason").getAsString();

        if (approval) {
            String client_token = json.get("client_token").getAsString();
            String server_IP = json.get("server_IP").getAsString();
            String server_port = json.get("server_port").getAsString();

            MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
            assert memoryBox != null;
            memoryBox.serverConnection = "yesEstablished";
            memoryBox.client_name = client_name;
            memoryBox.token = client_token;
            memoryBox.server_IP = server_IP;
            memoryBox.server_port = server_port;
            GsonHelper.writeJsonFile(runtimeJsonFile.getPath(), memoryBox);

            log.info("Establishment approved by server. Closing this connection to reconnect as established client...");
            if (ClientStates.onEstablishListener != null) ClientStates.onEstablishListener.onEstablish();
            closeEverything();
        } else {
            log.info("Connection denied by server. - {}", denied_reason);
            Alert.showError(denied_reason);
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

    private void hear_connectionResponse(JsonObject json) {
        boolean isLinked = json.get("isLinked").getAsBoolean();

        if (isLinked) {
            log.info("Connection approved by server.");
            if (ClientStates.onConnectionListener != null) ClientStates.onConnectionListener.onConnection();
        } else {
            log.info("Connection denied by server.");
            Alert.showError("Kết nối bị từ chối bởi server, nếu cần thiết lập kết nối mới, hãy xóa file localStorage/memoryBox.json.");
            closeEverything();
        }
    }
    
    
//  == SystemInfo (nhận gửi thông tin CPU/RAM, ...) ==
    private void hear_systemInfoRequest() {
        speak_systemInfoResponse();
    }
    
    private void speak_systemInfoResponse() {
        SystemInfo systemInfo = new SystemInfo();
        String jsonString = systemInfo.getSystemInfoJSON();
        speak(jsonString);
    }


// == Notification (thông báo từ server đến client) ==
    private void hear_notificationRequest(JsonObject json) {
        String notificationMessage = json.get("msg").getAsString();
        Alert.showInfo(notificationMessage);
    }


// == Exercise (bài tập) ==
    public void speak_requestAssignments() {
        Packet packet = new Packet(PacketType.REQUEST_ASSIGNMENTS, null);
        packet.sender = this.client_name;
        speak(GsonHelper.toJson(packet));
    }

    private void hear_handleAssignmentList(JsonObject json) {
        String fullJson = GsonHelper.toJson(json);

        System.out.println("DEBUG: Đã nhận được JSON bài tập: " + fullJson); // Thêm dòng này
        try {
            // Parse toàn bộ gói tin
            Packet p = GsonHelper.fromJson(fullJson, Packet.class);

            // Chuyển đổi dữ liệu bên trong Packet (đang là Object/Map) sang Assignment
            // Lưu ý: Đảm bảo GsonHelper của Client cũng đã đăng ký LocalDateTimeAdapter
            String assignmentJson = GsonHelper.toJson(p.data);
            Assignment assignment = GsonHelper.fromJson(assignmentJson, Assignment.class);

            if (ClientStates.onAssignmentListReceivedListener != null) {
                ClientStates.onAssignmentListReceivedListener.onAssignmentListReceived(List.of(assignment));
            }
            System.out.println("DEBUG: Parse thành công bài tập: " + assignment.title);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý danh sách bài tập: {}", e.getMessage());
        }
    }

    public void speak_sendSubmission(Submission sub) {
        Packet packet = new Packet(PacketType.SUBMISSION, sub);
        packet.sender = this.client_name;
        speak(GsonHelper.toJson(packet));
    }

    private void hear_handleSubmissionResult(JsonObject json) {
        // Server gửi Packet(PacketType.SUBMISSION_RESULT, "Message")
        // json lúc này là cấu trúc của Packet
        String type = json.get("type").getAsString();
        boolean success = type.equals("SUBMISSION_RESULT");

        // Lấy thông báo từ trường data của Packet
        String message = json.has("data") ? json.get("data").getAsString() : "Thông báo từ Server";

        ClientStates.fireSubmissionResult(success, message);
    }
}





















