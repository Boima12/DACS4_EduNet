package org.example.server.controller.services.lock;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LockController {

    private ServerSocket serverSocket;
    private final List<Socket> clients = new CopyOnWriteArrayList<>();
    private volatile boolean running = false;
    private volatile boolean isLocked = false;
    private final int id_port_lock;

    public LockController(int id_port_lock) {
        this.id_port_lock = id_port_lock;
    }

    /* ================= START SERVER ================= */
    public void start() {
        if (running) return;
        running = true;

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(id_port_lock);
                System.out.println("🔒 Lock Server listening on port " + id_port_lock);

                while (running) {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    System.out.println("Client connected: " + client.getInetAddress());
                }
            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }, "Lock-Server-Thread").start();
    }

    /* ================= STOP SERVER ================= */
    public void stop() {
        running = false;
        try {
            for (Socket c : clients) c.close();
            clients.clear();
            if (serverSocket != null) serverSocket.close();
        } catch (Exception ignored) {}
    }

    /* ================= SEND ================= */
    public void sendCommandToAll(String cmd) {
        for (Socket client : clients) {
            try {
                BufferedWriter out =
                        new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
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

    /* ================= LOCK LOGIC ================= */
    public void lock() {
        sendCommandToAll("LOCK");
        isLocked = true;
    }

    public void unlock() {
        sendCommandToAll("UNLOCK");
        isLocked = false;
    }

    public void toggleLock() {
        if (isLocked) unlock();
        else lock();
    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getButtonText() {
        return isLocked ? "Mở khóa" : "Khóa máy";
    }
}
