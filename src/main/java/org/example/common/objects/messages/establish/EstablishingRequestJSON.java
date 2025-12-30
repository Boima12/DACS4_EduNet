package org.example.common.objects.messages.establish;

public class EstablishingRequestJSON {
    public String type;
    public String client_name;
    public int maLienKet;

    public EstablishingRequestJSON() {
        this.type = "establishingRequest";
        this.client_name = "";
        this.maLienKet = 0;
    }
}
