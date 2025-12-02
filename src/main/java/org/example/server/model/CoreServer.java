package org.example.server.model;


import org.example.server.view.login.Login;

import javax.swing.*;

/**
 * run view/Login.java to let user login
 *
 */
public class CoreServer {
    public CoreServer() {
        SwingUtilities.invokeLater(() -> {
            Login loginWindow = new Login();
            loginWindow.display();
        });
    }
}
