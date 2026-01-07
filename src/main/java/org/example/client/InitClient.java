package org.example.client;

import org.example.client.model.CoreClient;

/**
 * Entry file của client
 *
 */
public class InitClient {
    public static void main(String[] args) {
        CoreClient coreClient = new CoreClient();
        coreClient.start();
    } 
}


