package org.example.client.controller;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LockController {

    private Robot robot;
    private volatile boolean locked = false;

    private Point lockMousePos;
    private JFrame lockFrame;

    private String serverIP;
    private int lockPort;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> unlockTask;

    public LockController(String serverIP, int lockPort) {
        this.serverIP = serverIP;
        this.lockPort = lockPort;

        try {
            robot = new Robot();
            scheduler = Executors.newSingleThreadScheduledExecutor();
            connectServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= CONNECT SERVER ================= */
    private void connectServer() {
        new Thread(() -> {
            try (Socket socket = new Socket(serverIP, lockPort);
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(socket.getInputStream()))) {

                String cmd;
                while ((cmd = in.readLine()) != null) {
                    handleCommand(cmd.trim().toUpperCase());
                }
            } catch (Exception e) {
                System.out.println("LockController: Mất kết nối Server");
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
        }
    }

    /* ================= LOCK (5 GIÂY) ================= */
    private void lockMachine() {
        if (locked) return;
        locked = true;

        lockMousePos = MouseInfo.getPointerInfo().getLocation();

        // Khóa chuột
        new Thread(() -> {
            while (locked) {
                robot.mouseMove(lockMousePos.x, lockMousePos.y);
                try { Thread.sleep(10); } catch (Exception ignored) {}
            }
        }, "Mouse-Lock-Thread").start();

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

        // ⏱️ TỰ ĐỘNG UNLOCK SAU 5 GIÂY
        if (unlockTask != null && !unlockTask.isDone()) {
            unlockTask.cancel(true);
        }

        unlockTask = scheduler.schedule(() -> {
            System.out.println("⏱️ Hết 5 giây → tự động UNLOCK");
            unlockMachine();
        }, 5, TimeUnit.SECONDS);
    }

    /* ================= UNLOCK ================= */
    private void unlockMachine() {
        if (!locked) return;
        locked = false;

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
}
