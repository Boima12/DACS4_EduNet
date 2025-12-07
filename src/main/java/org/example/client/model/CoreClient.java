package org.example.client.model;

import javax.swing.SwingUtilities;
import java.io.*;
import java.nio.charset.StandardCharsets;

import org.example.client.controller.ClientNetwork;
import org.example.client.view.EClient;
import org.example.client.view.eClientConnector.EClientConnector;
import org.example.common.objects.MemoryBox;
import org.example.common.utils.gson.GsonHelper;
import org.example.common.utils.gui.Alert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreClient {

    private boolean isEstablished = false;

    private ClientNetwork clientNetwork;
    private EClient eClientWindow;
    private EClientConnector eClientConnectorWindow;

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
            eClientWindow = new EClient();
            eClientConnectorWindow = new EClientConnector();

            eClientConnectorWindow.setOnEstablishListenerCallback((String serverIP, String port, String maLienKet) -> {
            	askForServerApproval(serverIP, port, maLienKet);

                MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
                if (memoryBox != null && "yesEstablished".equals(memoryBox.serverConnection)) {
            		connectToServer();
                    eClientConnectorWindow.undisplay();
            		eClientWindow.display();
            	}
            });

            if (isEstablished) {
            	connectToServer();
                eClientWindow.display();
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

    private boolean askForServerApproval(String serverIP, String port, String maLienKet) {
        try {
            clientNetwork = new ClientNetwork(serverIP, Integer.parseInt(port));
            clientNetwork.send_establishingRequest(Integer.parseInt(maLienKet));
            return true;
        } catch (NumberFormatException nfe) {
            Alert.showError("Port or code format is invalid.");
            log.error("Invalid number format in askForServerApproval", nfe);
            return false;
        } catch (IOException ioe) {
            Alert.showError("Không thể kết nối tới server: " + ioe.getMessage());
            log.error("Failed to open socket for establishing request", ioe);
            return false;
        }
    }

    private void connectToServer() {
    	MemoryBox memoryBox = GsonHelper.readJsonFile(runtimeJsonFile.getPath(), MemoryBox.class);
        log.info("memoryBox: {} {}:{}", memoryBox.token, memoryBox.server_IP, memoryBox.server_port);	// remove this later

    	// TODO implement network establish with server

    }
}