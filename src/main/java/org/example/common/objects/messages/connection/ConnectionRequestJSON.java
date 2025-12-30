package org.example.common.objects.messages.connection;

public class ConnectionRequestJSON {
    public String type;
    public String client_token;

    public ConnectionRequestJSON() {
        this.type = "connectionRequest";
        this.client_token = "";
    }
}
