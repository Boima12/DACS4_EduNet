package org.example.server.view.manage;

@FunctionalInterface
public interface OnSystemInfoRequestListener {
    void onSystemInfoRequest(String client_name);
}
