//package org.example.server.controller;
//
//import java.io.BufferedWriter;
//import java.io.OutputStreamWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//public class LockController {
//
//    private final List<Socket> clients = new ArrayList<>();
//
//    public void startServer(int port) {
//        new Thread(() -> {
//            try (ServerSocket serverSocket = new ServerSocket(port)) {
//                System.out.println("Lock Server listening on port " + port);
//
//                while (true) {
//                    Socket client = serverSocket.accept();
//                    System.out.println("Client connected: " + client.getInetAddress());
//                    synchronized (clients) {
//                        clients.add(client);
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }, "Lock-Server-Thread").start();
//    }
//
//    /* ================= SEND COMMAND ================= */
//
//    public void sendCommandToAll(String cmd) {
//        synchronized (clients) {
//            clients.removeIf(Socket::isClosed);
//
//            for (Socket client : clients) {
//                try {
//                    BufferedWriter out = new BufferedWriter(
//                        new OutputStreamWriter(client.getOutputStream()));
//                    out.write(cmd);
//                    out.newLine();
//                    out.flush();
//                } catch (Exception e) {
//                    try { client.close(); } catch (Exception ignored) {}
//                }
//            }
//        }
//        System.out.println("Sent command: " + cmd);
//    }
//}


package org.example.server.controller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LockController {

    private ServerSocket serverSocket;
    private final List<Socket> clients = new CopyOnWriteArrayList<>();
    private boolean running = false;

    /* ================= START SERVER ================= */
    public void startServer(int port) {
        if (running) return; // tránh mở nhiều lần
        running = true;

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("🔒 Lock Server listening on port " + port);

                while (true) {
                    Socket client = serverSocket.accept();
                    System.out.println("Client connected: " + client.getInetAddress());
                    clients.add(client);
                }
            } catch (Exception e) {
                System.err.println("Lock Server stopped!");
                e.printStackTrace();
            }
        }, "Lock-Server-Thread").start();
    }

    /* ================= SEND TO ALL ================= */
    public void sendCommandToAll(String cmd) {
        for (Socket client : clients) {
            try {
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(client.getOutputStream()));
                out.write(cmd);
                out.newLine();
                out.flush();
            } catch (Exception e) {
                try { client.close(); } catch (Exception ignored) {}
                clients.remove(client);
            }
        }
        System.out.println("Sent command: " + cmd);
    }
}
