package org.example.client.controller;

import java.io.*;
import java.net.Socket;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.client.ClientStates;
import org.example.client.model.services.systemInfo.SystemInfo;
import org.example.common.objects.MemoryBox;
import org.example.common.objects.exercise.Assignment;
import org.example.common.objects.exercise.Packet;
import org.example.common.objects.exercise.PacketType;
import org.example.common.objects.exercise.Submission;
import org.example.common.objects.messages.ConnectionRequestJSON;
import org.example.common.objects.messages.EstablishingRequestJSON;
import org.example.common.utils.gson.GsonHelper;
import org.example.common.utils.gui.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientNetwork {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String client_name;
    private static final Logger log = LoggerFactory.getLogger(ClientNetwork.class);
    private final File runtimeJsonFile = new File("localStorage/memoryBox.json");

    public ClientNetwork(String serverIP, int port) throws IOException {
        clientSocket = new Socket(serverIP, port);
        // Sử dụng Data Stream bọc ngoài để đảm bảo tính ổn định của Stream
        in = new BufferedReader(new InputStreamReader(new DataInputStream(clientSocket.getInputStream())));
        out = new BufferedWriter(new OutputStreamWriter(new DataOutputStream(clientSocket.getOutputStream())));
        startListening();
    }

    private void startListening() {
        Thread t = new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    log.info("Received from Server: {}", msg);
                    processMessage(msg);
                }
            } catch (Exception e) {
                log.error("Lỗi kết nối hoặc luồng nghe bị ngắt", e);
            } finally {
                closeEverything();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void processMessage(String msg) {
        try {
            JsonObject json = JsonParser.parseString(msg).getAsJsonObject();
            if (!json.has("type")) return;

            String type = json.get("type").getAsString();
            
            switch (type) {
                case "establishingResponse" -> handleEstablishingResponse(json);
                case "connectionResponse" -> handleConnectionResponse(json);
                case "systemInfoRequest" -> handleSystemInfoRequest();
                case "notificationRequest" -> handleNotification(json);
                
                // Khớp với PacketType.ASSIGNMENT_LIST từ Server
                case "ASSIGNMENT_LIST" -> handleAssignmentList(msg);
                
                // Khớp với PacketType.SUBMISSION_RESULT hoặc PacketType.ERROR
                case "SUBMISSION_RESULT", "ERROR" -> handleSubmissionResult(json);
                
                default -> log.warn("Loại tin nhắn không xác định: {}", type);
            }
        } catch (Exception e) {
            log.error("Lỗi parse JSON: {}", e.getMessage());
        }
    }

//    public synchronized void speak(String message) {
//        try {
//            out.write(message);
//            out.newLine();
//            out.flush();
//        } catch (IOException e) {
//            log.error("Gửi dữ liệu thất bại", e);
//            closeEverything();
//        }
//    }

    // ================= LOGIC HỆ THỐNG =================

    public void sendEstablishingRequest(String clientName, int maLienKet) {
        EstablishingRequestJSON req = new EstablishingRequestJSON();
        req.client_name = clientName;
        req.maLienKet = maLienKet;
        speak(GsonHelper.toJson(req));
    }

    private void handleEstablishingResponse(JsonObject json) {
        boolean approval = json.get("approval").getAsBoolean();
        if (!approval) {
            Alert.showError("Mã liên kết không hợp lệ!");
            return;
        }
        this.client_name = json.get("client_name").getAsString();
        
        // Cập nhật bộ nhớ tạm
        MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
        if (memoryBox == null) memoryBox = new MemoryBox();
        
        memoryBox.serverConnection = "yesEstablished";
        memoryBox.client_name = this.client_name;
        memoryBox.token = json.get("client_token").getAsString();
        memoryBox.server_IP = json.get("server_IP").getAsString();
        memoryBox.server_port = String.valueOf(json.get("server_port").getAsInt());
        GsonHelper.writeJsonFile(runtimeJsonFile.getPath(), memoryBox);

        if (ClientStates.onEstablishListener != null) {
            try { ClientStates.onEstablishListener.onEstablish(); } catch (IOException e) { log.error(e.getMessage()); }
        }
    }

    public void sendConnectionRequest(String clientToken) {
        ConnectionRequestJSON req = new ConnectionRequestJSON();
        req.client_token = clientToken;
        speak(GsonHelper.toJson(req));
    }

    private void handleConnectionResponse(JsonObject json) {
        if (json.get("isLinked").getAsBoolean()) {
            // Sau khi kết nối thành công, yêu cầu Server gửi danh sách bài tập ngay
            requestAssignments();
            if (ClientStates.onConnectionListener != null) ClientStates.onConnectionListener.onConnection();
        } else {
            Alert.showError("Kết nối bị từ chối bởi Server.");
        }
    }

    private void handleSystemInfoRequest() {
        SystemInfo info = new SystemInfo();
        speak(info.getSystemInfoJSON());
    }

    private void handleNotification(JsonObject json) {
        Alert.showInfo(json.get("msg").getAsString());
    }

    // ================= LOGIC BÀI TẬP (FIXED) =================
//
//    private void handleAssignmentList(String fullJson) {
//        // Server gửi Packet chứa Assignment
//        Packet p = GsonHelper.fromJson(fullJson, Packet.class);
//        // Vì data trong Packet là một Object (Assignment), ta cần chuyển đổi lại
//        Assignment assignment = GsonHelper.fromJson(GsonHelper.toJson(p.data), Assignment.class);
//        
//        if (ClientStates.onAssignmentListReceivedListener != null) {
//            // Client có thể nhận từng bài hoặc danh sách tùy theo cách bạn thiết kế UI
//            ClientStates.onAssignmentListReceivedListener.onAssignmentListReceived(List.of(assignment));
//        }
//    }

    public void requestAssignments() {
        Packet packet = new Packet(PacketType.REQUEST_ASSIGNMENTS, null);
        packet.sender = this.client_name;
        speak(GsonHelper.toJson(packet));
    }

    public void sendSubmission(Submission sub) {
        Packet packet = new Packet(PacketType.SUBMISSION, sub);
        packet.sender = this.client_name;
        speak(GsonHelper.toJson(packet));
    }
    
    public synchronized void speak(String message) {
        try {
            out.write(message);
            out.newLine();
            out.flush();
            log.info("[Network] Dữ liệu đã được đẩy ra khỏi Socket thành công. Độ dài: {} ký tự", message.length());
        } catch (IOException e) {
            log.error("Gửi dữ liệu thất bại", e);
            closeEverything();
        }
    }

//    private void handleSubmissionResult(JsonObject json) {
//        // Kiểm tra loại gói tin để biết thành công hay lỗi
//        boolean success = json.get("type").getAsString().equals("SUBMISSION_RESULT");
//        String message = json.has("data") ? json.get("data").getAsString() : "No message";
//        
//        ClientStates.fireSubmissionResult(success, message);
//    }

    public void closeEverything() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            log.error("Lỗi khi đóng kết nối", e);
        }
    }
    
    public void send_establishingRequest(String client_name, int maLienKet) {
        EstablishingRequestJSON establishingRequestJSON = new EstablishingRequestJSON();
        establishingRequestJSON.client_name = client_name;
        establishingRequestJSON.maLienKet = maLienKet;
        String jsonString = GsonHelper.toJson(establishingRequestJSON);
        speak(jsonString);
    }
    
 // TRONG ClientNetwork.java

    private void handleAssignmentList(String fullJson) {
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

    private void handleSubmissionResult(JsonObject json) {
        // Server gửi Packet(PacketType.SUBMISSION_RESULT, "Message")
        // json lúc này là cấu trúc của Packet
        String type = json.get("type").getAsString();
        boolean success = type.equals("SUBMISSION_RESULT");
        
        // Lấy thông báo từ trường data của Packet
        String message = json.has("data") ? json.get("data").getAsString() : "Thông báo từ Server";
        
        ClientStates.fireSubmissionResult(success, message);
    }
}

