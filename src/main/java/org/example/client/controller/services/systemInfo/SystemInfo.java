package org.example.client.controller.services.systemInfo;

import java.io.File;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import org.example.common.objects.messages.SystemInfoResponseJSON;
import org.example.common.utils.gson.GsonHelper;

/**
 * Code lấy thông tin hệ thống của Client.
 *
 */
@SuppressWarnings("UnnecessaryLocalVariable")
public class SystemInfo {

    public String getSystemInfoJSON() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        // ===== CPU =====
        int cpuCores = osBean.getAvailableProcessors();
        double cpuLoad = osBean.getCpuLoad(); 

        // ===== RAM =====
        long totalRam = osBean.getTotalMemorySize();
        long freeRam = osBean.getFreeMemorySize();
        long usedRam = totalRam - freeRam;

        long totalRamMB = totalRam / (1024 * 1024);
        long freeRamMB = freeRam / (1024 * 1024);
        long usedRamMB = usedRam / (1024 * 1024);

        // ===== OS INFO =====
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");

        // ===== DISK =====
        StringBuilder diskInfo = new StringBuilder();
        File[] roots = File.listRoots();

        for (File root : roots) {
            long total = root.getTotalSpace() / (1024 * 1024 * 1024);
            long free = root.getFreeSpace() / (1024 * 1024 * 1024);
            long used = total - free;

            diskInfo.append(String.format(
                    "Disk %s: %d / %d GB used\n",
                    root.getAbsolutePath(),
                    used,
                    total
            ));
        }

        SystemInfoResponseJSON systemInfoResponseJSON = new SystemInfoResponseJSON();
        systemInfoResponseJSON.OS = osName + " " + osVersion + " (" + osArch + ") ";
        systemInfoResponseJSON.CPU_cores = String.valueOf(cpuCores);
        systemInfoResponseJSON.CPU_load = String.format("%.2f", cpuLoad * 100) + "%";
        systemInfoResponseJSON.RAM = usedRamMB + " / " + totalRamMB;
        systemInfoResponseJSON.Disk = diskInfo.toString();
        String jsonString = GsonHelper.toJson(systemInfoResponseJSON);

        return jsonString;
    }

}
