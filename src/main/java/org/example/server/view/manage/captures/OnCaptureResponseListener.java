package org.example.server.view.manage.captures;

@FunctionalInterface
public interface OnCaptureResponseListener {
    void onCaptureResponse(String pathToImage, String imageName);
}
