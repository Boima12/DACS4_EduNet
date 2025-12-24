package org.example.server;

import org.example.server.model.CoreServer;

import java.io.IOException;


/**
 * Entry file của server
 *
 */
public class InitServer {
    public static void main(String[] args) throws IOException {
        CoreServer coreServer = new CoreServer();
        coreServer.start(); 
    }  
}
