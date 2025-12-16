package org.example.server.model;

@FunctionalInterface
public interface OnServerCloseListener {
    void onServerClose();
}
