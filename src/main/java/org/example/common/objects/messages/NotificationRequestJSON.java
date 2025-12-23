package org.example.common.objects.messages;

public class NotificationRequestJSON {
    public String type;
    public String msg;

    public NotificationRequestJSON() {
        this.type = "notificationRequest";
        this.msg = "";
    }
}
