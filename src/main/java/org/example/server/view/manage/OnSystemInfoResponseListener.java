package org.example.server.view.manage;

@FunctionalInterface
public interface OnSystemInfoResponseListener {
    void onSystemInfoResponse(String os, String cpu_cores, String cpu_load, String ram, String inet_address, String disk_storages);
}
