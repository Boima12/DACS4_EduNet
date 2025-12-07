package org.example.common.objects;

public class MemoryBox {
    public String serverConnection;
    public String token;
    public String server_IP;
    public String server_port;
    
    public MemoryBox() {
        this.serverConnection = "notEstablished";
        this.token = "";
        this.server_IP = "";
        this.server_port = "";
    }
}
