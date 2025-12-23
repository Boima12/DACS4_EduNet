package org.example.client;

import org.example.client.view.eClient.OnConnectionListener;
import org.example.client.view.eClientConnector.OnEstablishListener;

/**
 * Client States, nơi trung chuyển các trạng thái và callback cho code client
 *
 */
public class ClientStates {

    // callbacks
    public static OnEstablishListener onEstablishListener;
    public static void setOnEstablishListenerCallback(OnEstablishListener callback) {
        onEstablishListener = callback;
    }

    public static OnConnectionListener onConnectionListener;
    public static void setOnConnectionListenerCallback(OnConnectionListener callback) {
        onConnectionListener = callback;
    }

}
