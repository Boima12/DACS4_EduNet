package org.example.common.objects;

public class MemoryBox {
    public String serverConnection;
    public String client_name;
    public String token;
    public String server_IP;
    public String server_port;
    
    public MemoryBox() {
        this.serverConnection = "notEstablished";
        this.client_name = "";
        this.token = "";
        this.server_IP = "";
        this.server_port = "";
    }
}
