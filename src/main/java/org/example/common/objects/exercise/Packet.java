package org.example.common.objects.exercise;

import java.io.Serializable;

public class Packet implements Serializable {

    private static final long serialVersionUID = 1L;

    public PacketType type;
    public Object data;

    // Metadata (khuyến nghị)
    public String sender;      // client_name hoặc "server"
    public long timestamp;     // System.currentTimeMillis()

    // Constructor rỗng (BẮT BUỘC cho Gson)
    public Packet() {
    }

    public Packet(PacketType type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public Packet(PacketType type, Object data, String sender) {
        this.type = type;
        this.data = data;
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
    }
}
