package org.example.server;

import org.example.server.model.CoreServer;


/**
 * Initializes the CoreServer instance.
 *
 */
public class InitServer {
    public static void main(String[] args) {
        CoreServer coreServer = new CoreServer();
        System.out.println("CoreServer initialized: " + coreServer);
    }
}
