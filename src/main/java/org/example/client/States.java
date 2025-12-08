package org.example.client;

import org.example.client.view.eClientConnector.OnEstablishListener;

public class States {

    // callbacks
    public static OnEstablishListener onEstablishListener;
    public static void setOnEstablishListenerCallback(OnEstablishListener callback) {
        onEstablishListener = callback;
    }

}
