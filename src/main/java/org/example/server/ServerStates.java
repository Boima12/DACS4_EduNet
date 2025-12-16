package org.example.server;

import org.example.server.model.OnServerCloseListener;
import org.example.server.model.OnServerStartListener;
import org.example.server.view.dashboard.LienKetModal;
import org.example.server.view.dashboard.OnClient_dashboardConnectedListener;
import org.example.server.view.dashboard.OnClient_dashboardDisconnectedListener;
import org.example.server.view.dashboard.OnClient_dashboardNewClientListener;

public class ServerStates {
	
	public static int MA_LIEN_KET = 1;

    // UI states
    public static LienKetModal lkModal = null;
	
    // callbacks
    public static OnServerStartListener onServerStartListener;
    public static void setOnServerStartListenerCallback(OnServerStartListener callback) {
    	onServerStartListener = callback;
    }

    public static OnServerCloseListener onServerCloseListener;
    public static void setOnServerCloseListenerCallback(OnServerCloseListener callback) {
    	onServerCloseListener = callback;
    }

    public static OnClient_dashboardNewClientListener onClient_dashboardNewClientListener;
    public static void setOnClient_dashboardNewClientListenerCallback(OnClient_dashboardNewClientListener callback) {
    	onClient_dashboardNewClientListener = callback;
    }
    
    public static OnClient_dashboardConnectedListener onClient_dashboardConnectedListener;
    public static void setOnClient_dashboardConnectedCallback(OnClient_dashboardConnectedListener callback) {
    	onClient_dashboardConnectedListener = callback;
    }

    public static OnClient_dashboardDisconnectedListener onClient_dashboardDisconnectedListener;
    public static void setOnClient_dashboardDisconnectedCallback(OnClient_dashboardDisconnectedListener callback) {
    	onClient_dashboardDisconnectedListener = callback;
    }
}
