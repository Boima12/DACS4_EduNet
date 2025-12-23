package org.example.server.view.dashboard;

import org.example.common.utils.gui.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * A custom JPanel representing a single client item in the dashboard.
 * This panel displays a client icon and name, with selection functionality.
 *
 * tóm lại file này là để hiển thị 1 client cho client_dashboard trong dashboard.java
 */
@SuppressWarnings({"FieldMayBeFinal", "JavadocBlankLines"})
public class Client_dashboard_JPanel extends JPanel {
    private static final Color SELECTED_BG = new Color(209, 237, 248);
    private static final Color UNSELECTED_BG = new Color(251, 251, 251);

    private String clientName;
    private boolean isConnected;
    private JLabel lbl_item_icon;
    private OnClientItemSelected selectionCallback;

    /**
     * Callback interface for when a client item is selected/deselected
     */
    public interface OnClientItemSelected {
        void onSelected(Client_dashboard_JPanel panel, String name, boolean isConnected);
        void onDeselected();
    }

    /**
     * Constructor for Client_dashboard_JPanel
     * @param name The name/address of the client
     * @param isConnected Whether the client is currently connected
     * @param selectionCallback Callback to handle selection events
     * @param client_dashboard_JPanelList List to track all client panels
     */
    public Client_dashboard_JPanel(String name, boolean isConnected, OnClientItemSelected selectionCallback, ArrayList<JPanel> client_dashboard_JPanelList) {
        this.clientName = name;
        this.isConnected = isConnected;
        this.selectionCallback = selectionCallback;

        setPreferredSize(new Dimension(125, 135));
        setLayout(null);
        setBackground(UNSELECTED_BG);

        lbl_item_icon = new JLabel();
        lbl_item_icon.setBounds(25, 10, 75, 75);
        updateIcon();
        add(lbl_item_icon);

        JLabel lbl_item_name = new JLabel(name, SwingConstants.CENTER);
        lbl_item_name.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lbl_item_name.setBounds(10, 95, 105, 20);
        add(lbl_item_name);

        // Add mouse listener for selection
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSelection();
            }
        });

        // Add this panel to the tracking list
        client_dashboard_JPanelList.add(this);
    }

    /**
     * Toggle the selection state of this client item
     */
    public void toggleSelection() {
        if (isSelected()) {
            deselect();
        } else {
            select();
        }
    }

    /**
     * Select this client item
     */
    public void select() {
        setBackground(SELECTED_BG);
        updateIcon();
        if (selectionCallback != null) {
            selectionCallback.onSelected(this, clientName, isConnected);
        }
    }

    /**
     * Deselect this client item
     */
    public void deselect() {
        setBackground(UNSELECTED_BG);
        if (selectionCallback != null) {
            selectionCallback.onDeselected();
        }
    }

    /**
     * Check if this panel is currently selected
     */
    public boolean isSelected() {
        return getBackground().equals(SELECTED_BG);
    }

    /**
     * Update the icon size based on selection state
     */
    private void updateIcon() {
        if (isConnected) {
            lbl_item_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop.png", 75, 75));
        } else {
            lbl_item_icon.setIcon(ImageHelper.getScaledIcon("/images/desktop_off.png", 75, 75));
        }
    }

    /**
     * Get the client name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Get the connection status
     */
    public boolean isClientConnected() {
        return isConnected;
    }

    /**
     * Update the connection status and refresh the icon
     */
    public void setConnected(boolean connected) {
        this.isConnected = connected;
        updateIcon();
    }
}
