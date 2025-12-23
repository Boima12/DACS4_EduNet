package org.example.server.model;

import java.io.IOException;

@FunctionalInterface
public interface OnServerStartListener {
    void onServerStart() throws IOException;
}
