package org.example.common.objects.messages.systemInfo;

public class SystemInfoResponseJSON {
    public String type;
    public String OS;
    public String CPU_cores;
    public String CPU_load;
    public String RAM;
    public String Disk;

    public SystemInfoResponseJSON() {
        this.type = "systemInfoResponse";
        this.OS = "";
        this.CPU_cores = "";
        this.CPU_load = "";
        this.RAM = "";
        this.Disk = "";
    }
}