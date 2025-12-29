package org.example.client.controller.services.lock;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.*;

public class LockController {

    private Robot robot;
    private volatile boolean locked = false;

    private Point lockMousePos;
    private JFrame lockFrame;

    private final String serverIP;
    private final int lockPort;

    private Thread mouseLockThread;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> unlockTask;

    public LockController(String serverIP, int lockPort) {
        this.serverIP = serverIP;
        this.lockPort = lockPort;

        try {
            robot = new Robot();
            connectServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= CONNECT SERVER ================= */
    private void connectServer() {
        new Thread(() -> {
            while (true) {
                try (Socket socket = new Socket(serverIP, lockPort);
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(socket.getInputStream()))) {

                    System.out.println("🔗 Connected to Lock Server");

                    String cmd;
                    while ((cmd = in.readLine()) != null) {
                        handleCommand(cmd.trim().toUpperCase());
                    }

                } catch (Exception e) {
                    System.out.println("❌ Mất kết nối Lock Server, thử lại sau 3s...");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {}
                }
            }
        }, "Lock-Client-Thread").start();
    }

    /* ================= HANDLE COMMAND ================= */
    private void handleCommand(String cmd) {
        switch (cmd) {
            case "LOCK":
                lockMachine();
                break;
            case "UNLOCK":
                unlockMachine();
                break;
            default:
                System.out.println("⚠️ Unknown command: " + cmd);
        }
    }

    /* ================= LOCK (5 GIÂY) ================= */
    private synchronized void lockMachine() {
        if (locked) return;
        locked = true;

        System.out.println("🔒 LOCK MACHINE");

        lockMousePos = MouseInfo.getPointerInfo().getLocation();

        // Khóa chuột
        mouseLockThread = new Thread(() -> {
            while (locked) {
                robot.mouseMove(lockMousePos.x, lockMousePos.y);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            }
        }, "Mouse-Lock-Thread");
        mouseLockThread.start();

        // Khóa màn hình
        SwingUtilities.invokeLater(() -> {
            lockFrame = new JFrame();
            lockFrame.setUndecorated(true);
            lockFrame.setAlwaysOnTop(true);
            lockFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            lockFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setBackground(Color.BLACK);
            lockFrame.setContentPane(panel);

            lockFrame.setVisible(true);
            lockFrame.requestFocus();
        });

        // ⏱️ AUTO UNLOCK SAU 5 GIÂY (CHỈ DEMO)
        if (unlockTask != null && !unlockTask.isDone()) {
            unlockTask.cancel(true);
        }

        unlockTask = scheduler.schedule(() -> {
            System.out.println("⏱️ Hết 5 giây → AUTO UNLOCK");
            unlockMachine();
        }, 5, TimeUnit.SECONDS);
    }

    /* ================= UNLOCK ================= */
    private synchronized void unlockMachine() {
        if (!locked) return;
        locked = false;

        System.out.println("🔓 UNLOCK MACHINE");

        if (unlockTask != null) {
            unlockTask.cancel(true);
        }

        SwingUtilities.invokeLater(() -> {
            if (lockFrame != null) {
                lockFrame.dispose();
                lockFrame = null;
            }
        });
    }

    /* ================= CLEANUP ================= */
    public void shutdown() {
        locked = false;

        if (unlockTask != null) unlockTask.cancel(true);
        scheduler.shutdownNow();

        unlockMachine();
    }
}
