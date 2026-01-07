package org.example.common.objects.messages.capture;

public class CaptureResponseJSON {
    public String type;
    public String imageName;
    public String imageData; // Base64 encoded image data

    public CaptureResponseJSON() {
        this.type = "captureResponse";
        this.imageName = "";
        this.imageData = "";
    }
}
