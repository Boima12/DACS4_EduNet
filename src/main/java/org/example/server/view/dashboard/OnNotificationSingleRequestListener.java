package org.example.server.view.dashboard;

@FunctionalInterface
public interface OnNotificationSingleRequestListener {
    void onNotificationSingleRequest(String client_name, String msg);
}
