package org.example.client.model;

import javax.swing.SwingUtilities;
import java.io.*;
import java.nio.charset.StandardCharsets;

import org.example.client.ClientStates;
import org.example.client.controller.ClientNetwork;
import org.example.client.controller.services.exercise.ExerciseController;
import org.example.client.view.eClient.EClient;
import org.example.client.view.eClientConnector.EClientConnector;
import org.example.common.objects.MemoryBox;
import org.example.common.objects.services.exercise.Assignment;
import org.example.common.utils.gson.GsonHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trung tâm chỉ huy cho cả Client
 *
 */
@SuppressWarnings({"DataFlowIssue", "FieldCanBeLocal"})
public class CoreClient {

    private boolean isEstablished = false;  

    private ClientNetwork clientNetwork;
    private EClient eClientWindow;
    private EClientConnector eClientConnectorWindow;
    private ExerciseController exerciseController;

    private static final Logger log = LoggerFactory.getLogger(CoreClient.class);
    private final File runtimeJsonFile = new File("localStorage/memoryBox.json");

    public CoreClient() {
        MemoryBox memoryBox; 

        // if runtimeJsonFile not exists -> copy from resources
        if (!runtimeJsonFile.exists()) { 
            copyDefaultMemoryBox();
        }

        // check if memoryBox.json "serverConnection": "yesEstablished", then this client is established with server
        memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
        if (memoryBox != null && "yesEstablished".equals(memoryBox.serverConnection)) {
            isEstablished = true;
        }
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            eClientConnectorWindow = new EClientConnector();

            ClientStates.setOnEstablishListenerCallback(() -> {
                // delay 500ms to wait for previous connection close
                Thread delayThread = new Thread(() -> {
                    try { Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
                    if (memoryBox != null && "yesEstablished".equals(memoryBox.serverConnection)) {
                        try {
                            connectToServer();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                delayThread.setName("First establish thread");
                delayThread.setDaemon(true);
                delayThread.start();
            });

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

            // Nếu đã có memoryBox.json và đã kết nối từ trước
            if (isEstablished) {
                try {
                    connectToServer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                eClientConnectorWindow.display();
            }
        });
    }

    private void copyDefaultMemoryBox() {
        try (InputStream is = getClass().getResourceAsStream("/localStorage/memoryBox.json")) {
            if (is == null) {
                log.error("Cannot find default memoryBox.json in resources!");
                return;
            }

            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            try (FileOutputStream fos = new FileOutputStream(runtimeJsonFile)) {
                fos.write(json.getBytes(StandardCharsets.UTF_8));
            }

            log.info("Created new runtime memoryBox.json");
        } catch (IOException e) {
            log.error("Failed copying default memoryBox.json", e);
        }
    }

    private void connectToServer() throws IOException {
    	MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
        clientNetwork = new ClientNetwork(memoryBox.server_IP, Integer.parseInt(memoryBox.server_port));
        clientNetwork.send_connectionRequest(memoryBox.token);

        SwingUtilities.invokeLater(() -> {
           ClientStates.setOnConnectionListenerCallback(() -> {
//                Client_Screen clientScreen = new Client_Screen(memoryBox.server_IP, Integer.parseInt(memoryBox.server_port));
//                clientScreen.setVisible(true);

                eClientWindow = new EClient(memoryBox.server_IP, Integer.parseInt(memoryBox.server_port));

                eClientConnectorWindow.undisplay();
                eClientWindow.display();
           });
        });

        exerciseController = new ExerciseController(clientNetwork);
    }
}