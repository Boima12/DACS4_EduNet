package org.example.common.objects.messages.connection;

public class ConnectionResponseJSON {
    public String type;
    public boolean isLinked;

    public ConnectionResponseJSON() {
        this.type = "connectionResponse";
        this.isLinked = false;
    }
}
