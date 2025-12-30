package org.example.common.objects.messages.notification;

public class NotificationRequestJSON {
    public String type;
    public String msg;

    public NotificationRequestJSON() {
        this.type = "notificationRequest";
        this.msg = "";
    }
}
