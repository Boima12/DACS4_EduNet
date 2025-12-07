package org.example.common.objects.messages;

public class EstablishingResponseJSON {
    public String type;
    public boolean approval;
    public String client_token;
    public String server_IP;
    public int server_port;

    public EstablishingResponseJSON() {
        this.type = "establishingResponse";
        this.approval = false;
        this.client_token = "";
        this.server_IP = "";
        this.server_port = 6060;
    }
}
