package org.example.server.controller;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.example.common.objects.messages.connection.ConnectionResponseJSON;
import org.example.common.objects.messages.establish.EstablishingResponseJSON;
import org.example.common.objects.messages.notification.NotificationRequestJSON;
import org.example.common.objects.messages.systemInfo.SystemInfoRequestJSON;
import org.example.common.objects.services.exercise.Assignment;
import org.example.common.objects.services.exercise.Packet;
import org.example.common.objects.services.exercise.PacketType;
import org.example.common.objects.services.exercise.Submission;
import org.example.common.utils.gson.GsonHelper;
import org.example.common.utils.network.NetworkUtils;
import org.example.server.ServerStates;
import org.example.server.controller.services.exercise.ExerciseController;
import org.example.server.model.database.JDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.example.server.controller.ServerNetwork.clients;

/**
 * Code xử lý Network chính của Server cho mỗi client kết nối đến.
 *
 */
public class ServerNetworkHandler {

    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String client_name;
    private volatile boolean isClosed = false;

    private static ExerciseController exerciseController;

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

    public static void setExerciseController(ExerciseController controller) {
        exerciseController = controller;
    }

    public String getClient_name() {
        return client_name;
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

                        case "systemInfoResponse":
                            hear_systemInfoResponse(json);
                            break;

                        // --- CHỨC NĂNG BÀI TẬP (NEW) ---
                        case "SUBMISSION":
                            hear_handleSubmissionRequest(json);
                            break;

                        case "REQUEST_ASSIGNMENTS":
                            speak_sendAllExistingAssignments();
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
        if (isClosed) {
            log.warn("Attempted to send message on closed connection: {}", message);
            return;
        }
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
        if (isClosed) {
            return; // Already closed, prevent double-closing
        }
        isClosed = true;

        try {
            log.info("Client {} - {} disconnected.", client_name, clientSocket.getInetAddress());

            // update dashboard UI.
            if (ServerStates.onClientDashboardDisconnectedListener != null) ServerStates.onClientDashboardDisconnectedListener.onClient_dashboardDisconnected(client_name);

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
            if (ServerStates.onClientDashboardNewClientListener != null) ServerStates.onClientDashboardNewClientListener.onClient_dashboardNewClient(client_name);
            
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
            if (ServerStates.onClientDashboardConnectedListener != null) ServerStates.onClientDashboardConnectedListener.onClient_dashboardConnected(client_name);

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
    
    
//  == SystemInfo (nhận gửi thông tin CPU/RAM, ...) ==
    public void speak_systemInfoRequest() {
        SystemInfoRequestJSON systemInfoRequestJSON = new SystemInfoRequestJSON();
        String jsonString = GsonHelper.toJson(systemInfoRequestJSON);
        speak(jsonString);
    }
    
    private void hear_systemInfoResponse(JsonObject json) {
        String OS = json.get("OS").getAsString();
        String CPU_cores = json.get("CPU_cores").getAsString();
        String CPU_load = json.get("CPU_load").getAsString();
        String RAM = json.get("RAM").getAsString();
        String Disk = json.get("Disk").getAsString();

        if (ServerStates.onSystemInfoResponseListener != null) ServerStates.onSystemInfoResponseListener.onSystemInfoResponse(OS, CPU_cores, CPU_load, RAM, String.valueOf(clientSocket.getInetAddress()), Disk);
    }


// == Notification (thông báo từ server đến client) ==
    public void speak_notificationRequest(String message) {
        NotificationRequestJSON notificationRequestJSON = new NotificationRequestJSON();
        notificationRequestJSON.msg = message;
        String jsonString = GsonHelper.toJson(notificationRequestJSON);
        speak(jsonString);
    }


// == XỬ LÝ BÀI NỘP TỪ CLIENT ==
    private void hear_handleSubmissionRequest(JsonObject json) {
        String rawJson = GsonHelper.toJson(json);

        try {
            // 1. Parse thành Packet trước
            Packet packet = GsonHelper.fromJson(rawJson, Packet.class);

            // 2. Chuyển phần data (Object) ngược lại thành chuỗi JSON rồi mới parse sang Submission
            String dataJson = GsonHelper.toJson(packet.data);
            Submission submission = GsonHelper.fromJson(dataJson, Submission.class);

            if (exerciseController != null) {
                exerciseController.handleSubmissionFromClient(submission, this); // Chuyền thêm 'this' để phản hồi
            }
        } catch (Exception e) {
            log.error("Lỗi parse bài nộp: {}", e.getMessage());
            speak_submissionResult(false, "Lỗi định dạng dữ liệu bài nộp.");
        }
    }

    private void speak_sendAllExistingAssignments() {
        if (exerciseController != null) {
            for (Assignment a : exerciseController.getAllAssignments()) {
                speak_assignment(a);
            }
        }
    }

    // Trong ServerNetworkHandler.java
    public void speak_assignment(Assignment a) {
        try {
            Packet p = new Packet(PacketType.ASSIGNMENT_LIST, a);
            String json = GsonHelper.toJson(p); // Đảm bảo GsonHelper đã có Adapter
            log.info("Đang gửi bài tập: {}", a.title);
            speak(json);
        } catch (Exception e) {
            log.error("Lỗi gửi bài tập: {}", e.getMessage());        }
    }

    /**
     * Gửi phản hồi kết quả nộp bài cho Client này
     */
    public void speak_submissionResult(boolean success, String message) {
        Packet p = new Packet(success ? PacketType.SUBMISSION_RESULT : PacketType.ERROR, message);
        speak(GsonHelper.toJson(p));
    }

    /**
     * Gửi 1 bài tập cụ thể cho Client này
     */
//    public void speak_assignment(Assignment a) {
//        // Gửi dưới dạng Packet JSON để Client dễ parse
//        Packet p = new Packet(PacketType.ASSIGNMENT_LIST, a);
//        speak(GsonHelper.toJson(p));
//    }
//    public void speak_assignment(Assignment a) {
//        // Thay vì dùng new Gson(), hãy dùng Builder có Adapter
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
//                .create();
//
//        String json = gson.toJson(a);
//        // Gửi chuỗi json này qua OutputStream...
//    }

//          private void handleSubmissionRequest(String rawJson) {
//        try {
//            // Chuyển JSON thành đối tượng Submission (bao gồm byte[] fileData)
//            Submission submission = GsonHelper.fromJson(rawJson, Submission.class);
//            if (exerciseController != null) {
//                // Đẩy sang Controller xử lý lưu file và database
//                exerciseController.handleSubmissionFromClient(submission);
//            }
//        } catch (Exception e) {
//            log.error("Lỗi xử lý bài nộp: {}", e.getMessage());
//            speak_submissionResult(false, "Định dạng bài nộp không hợp lệ!");
//        }
//    }
}
