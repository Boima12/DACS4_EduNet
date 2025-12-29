package org.example.server.controller;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.common.objects.exercise.Assignment;
import org.example.common.objects.exercise.Packet;
import org.example.common.objects.exercise.PacketType;
import org.example.common.objects.exercise.Submission;
import org.example.common.objects.messages.ConnectionResponseJSON;
import org.example.common.objects.messages.EstablishingResponseJSON;
import org.example.common.objects.messages.NotificationRequestJSON;
import org.example.common.objects.messages.SystemInfoRequestJSON;
import org.example.common.utils.gson.GsonHelper;
import org.example.common.utils.network.NetworkUtils;
import org.example.server.ServerStates;
import org.example.server.controller.services.exercise.ExerciseController;
import org.example.server.controller.services.exercise.LocalDateTimeAdapter;
import org.example.server.model.database.JDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.example.server.controller.ServerNetwork.clients;

/**
 * Xử lý Network chính của Server - Tích hợp chức năng Giao/Nộp bài tập.
 */
public class ServerNetworkHandler {

    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String client_name;
    private volatile boolean isClosed = false;
    
    // Tham chiếu đến Controller để xử lý logic bài tập
    private static ExerciseController exerciseController;

    private static final Logger log = LoggerFactory.getLogger(ServerNetworkHandler.class);

    public ServerNetworkHandler(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            // Sử dụng DataInputStream/OutputStream như cấu trúc cũ của bạn
            in = new BufferedReader(new InputStreamReader(new DataInputStream(clientSocket.getInputStream())));
            out = new BufferedWriter(new OutputStreamWriter(new DataOutputStream(clientSocket.getOutputStream())));
            clients.add(this);
            hear();
        } catch (IOException e) {
            log.error("Khởi tạo Handler thất bại: {}", e.getMessage());
        }
    }

    public static void setExerciseController(ExerciseController controller) {
        exerciseController = controller;
    }

    public String getClient_name() {
        return client_name;
    }

    /**
     * Lắng nghe dữ liệu liên tục từ Client
     */
    public void hear() {
        Thread thread = new Thread(() -> {
            try {
                while (clientSocket != null && !clientSocket.isClosed() && !isClosed) {
                    String msg = in.readLine();
                    if (msg == null) {
                        closeEverything();
                        break;
                    }
                    log.info("Received from {}: {}", client_name != null ? client_name : "Unknown", msg);

                    JsonObject json = JsonParser.parseString(msg).getAsJsonObject();
                    String type = json.has("type") ? json.get("type").getAsString() : null;

                    if (type == null) continue;

                    // ROUTE THEO LOẠI GÓI TIN
                    switch (type) {
                        // --- CHỨC NĂNG BÀI TẬP (NEW) ---
                        case "SUBMISSION":
                            handleSubmissionRequest(msg);
                            break;
                        
                        case "REQUEST_ASSIGNMENTS":
                            sendAllExistingAssignments();
                            break;

                        // --- CHỨC NĂNG HỆ THỐNG CŨ ---
                        case "establishingRequest":
                            hear_establishingRequest(json);
                            break;

                        case "connectionRequest":
                            hear_connectionRequest(json);
                            break;

                        case "systemInfoResponse":
                            hear_systemInfoResponse(json);
                            break;

                        default:
                            log.warn("Unknown message type: {}", type);
                            break;
                    }
                }
            } catch (Exception e) {
                log.error("Lỗi trong luồng hear: {}", e.getMessage());
                closeEverything();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Gửi dữ liệu xuống Client
     */
    private synchronized void speak(String message) {
        if (isClosed) return;
        try {
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            log.error("Lỗi khi gửi dữ liệu: {}", e.getMessage());
            closeEverything();
        }
    }

    /* =======================================================
       LOGIC XỬ LÝ GỬI NHẬN BÀI TẬP (JSON)
       ======================================================= */

//    private void handleSubmissionRequest(String rawJson) {
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
    private void handleSubmissionRequest(String rawJson) {
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

    private void sendAllExistingAssignments() {
        if (exerciseController != null) {
            for (Assignment a : exerciseController.getAllAssignments()) {
                speak_assignment(a);
            }
        }
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

    /* =======================================================
       CÁC HÀM HỆ THỐNG CŨ (GIỮ NGUYÊN)
       ======================================================= */

    private void hear_establishingRequest(JsonObject json) {
        client_name = json.get("client_name").getAsString();
        int maLienKet = json.get("maLienKet").getAsInt();

        if (maLienKet == ServerStates.MA_LIEN_KET) {
            String client_token = generateClientToken();
            String sql = "INSERT INTO established_clients (clientInetAddress, client_name, client_token) VALUES (?, ?, ?)";
            JDBCUtil.runUpdate(sql, String.valueOf(clientSocket.getInetAddress()), client_name, client_token);

            EstablishingResponseJSON resp = new EstablishingResponseJSON();
            resp.approval = true;
            resp.client_name = client_name;
            resp.client_token = client_token;
            resp.server_IP = NetworkUtils.getLocalIPAddress();
            resp.server_port = 6060;
            speak(GsonHelper.toJson(resp));

            if (ServerStates.lkModal != null) {
                ServerStates.lkModal.turnOffLinkMode();
                ServerStates.lkModal = null;
            }
            if (ServerStates.onClientDashboardNewClientListener != null) 
                ServerStates.onClientDashboardNewClientListener.onClient_dashboardNewClient(client_name);
        } else {
            EstablishingResponseJSON resp = new EstablishingResponseJSON();
            resp.approval = false;
            speak(GsonHelper.toJson(resp));
        }
    }

    private void hear_connectionRequest(JsonObject json) {
        String client_token = json.get("client_token").getAsString();
        AtomicReference<Integer> count = new AtomicReference<>(0);
        String sql = "SELECT COUNT(*) FROM established_clients WHERE client_token = ?";
        JDBCUtil.runQuery(sql, rs -> {
            if (rs.next()) count.set(rs.getInt(1));
        }, client_token);

        if (count.get() > 0) {
            String sql_name = "SELECT client_name FROM established_clients WHERE client_token = ?";
            JDBCUtil.runQuery(sql_name, rs -> {
                if (rs.next()) client_name = rs.getString("client_name");
            }, client_token);

            ConnectionResponseJSON resp = new ConnectionResponseJSON();
            resp.isLinked = true;
            speak(GsonHelper.toJson(resp));

            if (ServerStates.onClientDashboardConnectedListener != null) 
                ServerStates.onClientDashboardConnectedListener.onClient_dashboardConnected(client_name);
        } else {
            ConnectionResponseJSON resp = new ConnectionResponseJSON();
            resp.isLinked = false;
            speak(GsonHelper.toJson(resp));
        }
    }

    public void speak_systemInfoRequest() {
        speak(GsonHelper.toJson(new SystemInfoRequestJSON()));
    }

    private void hear_systemInfoResponse(JsonObject json) {
        if (ServerStates.onSystemInfoResponseListener != null) {
            ServerStates.onSystemInfoResponseListener.onSystemInfoResponse(
                json.get("OS").getAsString(),
                json.get("CPU_cores").getAsString(),
                json.get("CPU_load").getAsString(),
                json.get("RAM").getAsString(),
                String.valueOf(clientSocket.getInetAddress()),
                json.get("Disk").getAsString()
            );
        }
    }

    public void speak_notificationRequest(String message) {
        NotificationRequestJSON req = new NotificationRequestJSON();
        req.msg = message;
        speak(GsonHelper.toJson(req));
    }

    public void closeEverything() {
        if (isClosed) return;
        isClosed = true;
        try {
            clients.remove(this);
            if (ServerStates.onClientDashboardDisconnectedListener != null) 
                ServerStates.onClientDashboardDisconnectedListener.onClient_dashboardDisconnected(client_name);
            
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            log.error("Lỗi đóng kết nối: {}", e.getMessage());
        }
    }

    private String generateClientToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) hex.append(String.format("%02x", b));
        return hex.toString();
    }
}