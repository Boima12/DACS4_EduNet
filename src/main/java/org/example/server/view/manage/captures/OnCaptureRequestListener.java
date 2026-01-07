package org.example.server.view.manage.captures;

@FunctionalInterface
public interface OnCaptureRequestListener {
    void onCaptureRequest(String client_name);
}

