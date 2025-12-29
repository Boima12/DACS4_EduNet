////package org.example.client.model;
////
////import javax.swing.JOptionPane;
////import javax.swing.SwingUtilities;
////import java.io.*;
////import java.nio.charset.StandardCharsets;
////import org.example.client.ClientStates;
////import org.example.client.controller.ClientNetwork;
////import org.example.client.controller.services.exercise.ExerciseController;
////import org.example.client.view.clientScreen.Client_Screen;
////import org.example.client.view.eClient.EClient;
////import org.example.client.view.eClientConnector.EClientConnector;
////import org.example.common.objects.MemoryBox;
////import org.example.common.objects.exercise.Assignment;
////import org.example.common.utils.gson.GsonHelper;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////
////public class CoreClient {
////    private boolean isEstablished = false;
////    private ClientNetwork clientNetwork;
////    private ExerciseController exerciseController; 
////    private EClient eClientWindow;
////    private EClientConnector eClientConnectorWindow;
////    private static final Logger log = LoggerFactory.getLogger(CoreClient.class);
////    private final File runtimeJsonFile = new File("localStorage/memoryBox.json");
////
////    public CoreClient() {
////        // Đảm bảo thư mục localStorage tồn tại
////        File dir = new File("localStorage");
////        if (!dir.exists()) dir.mkdirs();
////
////        if (!runtimeJsonFile.exists()) {
////            copyDefaultMemoryBox();
////        }
////        MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
////        if (memoryBox != null && "yesEstablished".equals(memoryBox.serverConnection)) {
////            isEstablished = true;
////        }
////    }
////
////    public void start() {
////        SwingUtilities.invokeLater(() -> {
////            eClientWindow = new EClient();
////            eClientConnectorWindow = new EClientConnector();
////
////            // Lắng nghe sự kiện Establish (lần đầu liên kết mã)
////            ClientStates.setOnEstablishListenerCallback(() -> {
////                MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
////                if (memoryBox != null && "yesEstablished".equals(memoryBox.serverConnection)) {
////                    connectSafe(memoryBox);
////                }
////            });
////            
////         // Ví dụ đặt trong hàm khởi tạo hoặc lúc mở view bài tập
////            ClientStates.setOnAssignmentListReceivedListener(assignments -> {
////                SwingUtilities.invokeLater(() -> {
////                    for (Assignment a : assignments) {
////                        // exerciseController là biến quản lý view
////                        exerciseController.addAssignmentFromServer(a); 
////                    }
////                });
////            });
////
////            if (isEstablished) {
////                MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
////                connectSafe(memoryBox);
////            } else {
////                eClientConnectorWindow.display();
////            }
////        });
////    }
////
////    private void connectSafe(MemoryBox memoryBox) {
////        try {
////            connectToServer(memoryBox);
////        } catch (IOException e) {
////            log.error("Không thể kết nối Server: {}", e.getMessage());
////            JOptionPane.showMessageDialog(null, "Không thể kết nối tới Server: " + memoryBox.server_IP);
////        }
////    }
////
////    private void connectToServer(MemoryBox memoryBox) throws IOException {
////        // 1. Khởi tạo kết nối Network
////        clientNetwork = new ClientNetwork(
////                memoryBox.server_IP,
////                Integer.parseInt(memoryBox.server_port)
////        );
////
////        // 2. Khởi tạo ExerciseController và đăng ký các listener mạng
////        // Điều này cực kỳ quan trọng: Phải làm TRƯỚC khi gửi ConnectionRequest 
////        // để nếu server trả về bài tập ngay lập tức, Controller đã sẵn sàng nhận.
////        exerciseController = new ExerciseController(clientNetwork);
////
////        // 3. Đăng ký Listener khi kết nối thành công (LLinked)
////        ClientStates.setOnConnectionListenerCallback(() -> {
////            SwingUtilities.invokeLater(() -> {
////                // Hiển thị màn hình chính Client
////                Client_Screen clientScreen = new Client_Screen(memoryBox.server_IP, Integer.parseInt(memoryBox.server_port));
////                clientScreen.setVisible(true);
////                
////                // Tắt cửa sổ chờ/connector
////                eClientConnectorWindow.undisplay();
////                eClientWindow.display(memoryBox.server_IP, memoryBox.server_port);
////                
////                log.info("Kết nối Server thành công và UI đã sẵn sàng.");
////            });
////        });
////
////        // 4. Gửi yêu cầu định danh (token) lên Server
////        clientNetwork.sendConnectionRequest(memoryBox.token);
////    }
//package org.example.client.model;
//
//import javax.swing.JOptionPane;
//import javax.swing.SwingUtilities;
//import java.io.*;
//import org.example.client.ClientStates;
//import org.example.client.controller.ClientNetwork;
//import org.example.client.controller.services.exercise.ExerciseController;
//import org.example.client.view.clientScreen.Client_Screen;
//import org.example.client.view.eClient.EClient;
//import org.example.client.view.eClientConnector.EClientConnector;
//import org.example.common.objects.MemoryBox;
//import org.example.common.objects.exercise.Assignment;
//import org.example.common.utils.gson.GsonHelper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class CoreClient {
//    private boolean isEstablished = false;
//    private ClientNetwork clientNetwork;
//    private ExerciseController exerciseController; 
//    private EClient eClientWindow;
//    private EClientConnector eClientConnectorWindow;
//    private static final Logger log = LoggerFactory.getLogger(CoreClient.class);
//    private final File runtimeJsonFile = new File("localStorage/memoryBox.json");
//
//    public CoreClient() {
//        File dir = new File("localStorage");
//        if (!dir.exists()) dir.mkdirs();
//
//        if (!runtimeJsonFile.exists()) {
//            copyDefaultMemoryBox();
//        }
//        MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
//        if (memoryBox != null && "yesEstablished".equals(memoryBox.serverConnection)) {
//            isEstablished = true;
//        }
//    }
//
//    public void start() {
//        SwingUtilities.invokeLater(() -> {
//            eClientWindow = new EClient();
//            eClientConnectorWindow = new EClientConnector();
//
//            // 1. Đăng ký Listener nhận bài tập ngay từ đầu
//            // Nhưng phải kiểm tra exerciseController khác null mới gọi
//            ClientStates.setOnAssignmentListReceivedListener(assignments -> {
//                if (exerciseController != null) {
//                    SwingUtilities.invokeLater(() -> {
//                        for (Assignment a : assignments) {
//                        	exerciseController.addAssignmentToModel(a);                        }
//                    });
//                } else {
//                    log.warn("Nhận được bài tập nhưng ExerciseController chưa khởi tạo!");
//                }
//            });
//
//            // Lắng nghe sự kiện Establish (liên kết mã lần đầu)
//            ClientStates.setOnEstablishListenerCallback(() -> {
//                MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
//                if (memoryBox != null && "yesEstablished".equals(memoryBox.serverConnection)) {
//                    connectSafe(memoryBox);
//                }
//            });
//
//            if (isEstablished) {
//                MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
//                connectSafe(memoryBox);
//            } else {
//                eClientConnectorWindow.display();
//            }
//        });
//    }
//
//    private void connectSafe(MemoryBox memoryBox) {
//        try {
//            connectToServer(memoryBox);
//        } catch (IOException e) {
//            log.error("Không thể kết nối Server: {}", e.getMessage());
//            JOptionPane.showMessageDialog(null, "Không thể kết nối tới Server: " + memoryBox.server_IP);
//        }
//    }
//
//    private void connectToServer(MemoryBox memoryBox) throws IOException {
//        // 2. Khởi tạo kết nối Network
//        clientNetwork = new ClientNetwork(
//                memoryBox.server_IP,
//                Integer.parseInt(memoryBox.server_port)
//        );
//
//        // 3. Khởi tạo ExerciseController NGAY LẬP TỨC
//        // Điều này đảm bảo khi gói tin Assignment từ Server bay về, controller đã tồn tại
//        exerciseController = new ExerciseController(clientNetwork);
//
//        ClientStates.setOnConnectionListenerCallback(() -> {
//            SwingUtilities.invokeLater(() -> {
//                Client_Screen clientScreen = new Client_Screen(memoryBox.server_IP, Integer.parseInt(memoryBox.server_port));
//                clientScreen.setVisible(true);
//                
//                eClientConnectorWindow.undisplay();
//                eClientWindow.display(memoryBox.server_IP, memoryBox.server_port);
//                
//                log.info("Kết nối Server thành công và UI đã sẵn sàng.");
//            });
//        });
//
//        // 4. Cuối cùng mới gửi yêu cầu kết nối
//        clientNetwork.sendConnectionRequest(memoryBox.token);
//    }
//    
//    // ... (giữ nguyên copyDefaultMemoryBox)
//
//    private void copyDefaultMemoryBox() {
//        try (InputStream is = getClass().getResourceAsStream("/localStorage/memoryBox.json")) {
//            if (is == null) {
//                // Nếu không có trong resources, tạo mới một object rỗng
//                MemoryBox newBox = new MemoryBox();
//                GsonHelper.writeJsonFile(runtimeJsonFile.getPath(), newBox);
//                return;
//            }
//            byte[] data = is.readAllBytes();
//            try (FileOutputStream fos = new FileOutputStream(runtimeJsonFile)) {
//                fos.write(data);
//            }
//            log.info("Đã sao chép memoryBox.json mặc định.");
//        } catch (IOException e) {
//            log.error("Lỗi tạo file memoryBox.json: ", e);
//        }
//    }
//}

package org.example.client.model;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.*;
import org.example.client.ClientStates;
import org.example.client.controller.ClientNetwork;
import org.example.client.controller.services.exercise.ExerciseController;
import org.example.client.view.clientScreen.Client_Screen;
import org.example.client.view.eClient.EClient;
import org.example.client.view.eClientConnector.EClientConnector;
import org.example.common.objects.MemoryBox;
import org.example.common.objects.exercise.Assignment;
import org.example.common.utils.gson.GsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreClient {
    private boolean isEstablished = false;
    private ClientNetwork clientNetwork;
    private ExerciseController exerciseController; 
    private EClient eClientWindow;
    private EClientConnector eClientConnectorWindow;
    private static final Logger log = LoggerFactory.getLogger(CoreClient.class);
    private final File runtimeJsonFile = new File("localStorage/memoryBox.json");

    public CoreClient() {
        log.info("[CoreClient] Khởi tạo hệ thống...");
        File dir = new File("localStorage");
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            log.info("[System] Tạo thư mục localStorage: {}", created);
        }

        if (!runtimeJsonFile.exists()) {
            log.warn("[System] Không tìm thấy file bộ nhớ, đang sao chép mặc định...");
            copyDefaultMemoryBox();
        }
        
        MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
        if (memoryBox != null && "yesEstablished".equals(memoryBox.serverConnection)) {
            isEstablished = true;
            log.info("[CoreClient] Trạng thái: Đã liên kết (Established).");
        } else {
            log.info("[CoreClient] Trạng thái: Chưa liên kết.");
        }
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            log.info("[UI] Đang chuẩn bị các cửa sổ kết nối...");
            eClientWindow = new EClient();
            eClientConnectorWindow = new EClientConnector();

            // 1. Đăng ký Listener nhận bài tập TOÀN CỤC
            ClientStates.setOnAssignmentListReceivedListener(assignments -> {
                log.info("[Network] Nhận được dữ liệu bài tập từ Server.");
                if (exerciseController != null) {
                    SwingUtilities.invokeLater(() -> {
                        for (Assignment a : assignments) {
                            exerciseController.addAssignmentToModel(a); 
                        }
                    });
                } else {
                    log.error("[CRITICAL] ExerciseController chưa được khởi tạo để nhận bài tập!");
                }
            });

            // Lắng nghe sự kiện Establish (liên kết mã lần đầu)
            ClientStates.setOnEstablishListenerCallback(() -> {
                log.info("[Event] Nhận được tín hiệu Establish thành công.");
                MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
                if (memoryBox != null && "yesEstablished".equals(memoryBox.serverConnection)) {
                    connectSafe(memoryBox);
                }
            });

            if (isEstablished) {
                MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
                connectSafe(memoryBox);
            } else {
                log.info("[UI] Hiển thị cửa sổ Connector để người dùng nhập mã.");
                eClientConnectorWindow.display();
            }
        });
    }

    private void connectSafe(MemoryBox memoryBox) {
        log.info("[Connection] Đang thử kết nối tới Server: {}", memoryBox.server_IP);
        try {
            connectToServer(memoryBox);
        } catch (IOException e) {
            log.error("[Error] Kết nối thất bại: {}", e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Lỗi kết nối tới Server: " + memoryBox.server_IP + "\nVui lòng kiểm tra lại mạng!", 
                "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
            
            // Nếu lỗi, hiển thị lại cửa sổ connector để thử lại
            eClientConnectorWindow.display();
        }
    }

    private void connectToServer(MemoryBox memoryBox) throws IOException {
        // 2. Khởi tạo kết nối Network
        log.info("[Network] Khởi tạo Socket Client...");
        clientNetwork = new ClientNetwork(
                memoryBox.server_IP,
                Integer.parseInt(memoryBox.server_port)
        );

        // 3. Khởi tạo ExerciseController NGAY LẬP TỨC
        // Gắn controller này vào hệ thống trước khi gửi request xác thực
        log.info("[System] Khởi tạo ExerciseController...");
        exerciseController = new ExerciseController(clientNetwork);

        ClientStates.setOnConnectionListenerCallback(() -> {
            SwingUtilities.invokeLater(() -> {
                log.info("[UI] Đang hiển thị Client Screen...");
                Client_Screen clientScreen = new Client_Screen(memoryBox.server_IP, Integer.parseInt(memoryBox.server_port));
                clientScreen.setVisible(true);
                
                eClientConnectorWindow.undisplay();
                eClientWindow.display(memoryBox.server_IP, memoryBox.server_port);
                
                log.info("[Success] Kết nối Server thành công và UI đã sẵn sàng.");
            });
        });

        // 4. Gửi yêu cầu định danh (token) lên Server
        log.info("[Network] Gửi yêu cầu ConnectionRequest với token: {}...", memoryBox.token.substring(0, 8) + "***");
        clientNetwork.sendConnectionRequest(memoryBox.token);
    }

    private void copyDefaultMemoryBox() {
        try (InputStream is = getClass().getResourceAsStream("/localStorage/memoryBox.json")) {
            if (is == null) {
                log.error("[System] Không tìm thấy file mẫu trong resources.");
                MemoryBox newBox = new MemoryBox();
                GsonHelper.writeJsonFile(runtimeJsonFile.getPath(), newBox);
                return;
            }
            byte[] data = is.readAllBytes();
            try (FileOutputStream fos = new FileOutputStream(runtimeJsonFile)) {
                fos.write(data);
            }
            log.info("[System] Đã sao chép thành công file memoryBox.json mẫu.");
        } catch (IOException e) {
            log.error("[System] Lỗi nghiêm trọng khi tạo file bộ nhớ: ", e);
        }
    }
}