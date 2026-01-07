package org.example.server.view.manage.captures;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;

import org.example.common.utils.gui.ImageHelper;

public class CaptureMenu_item extends JPanel {
	
    private static final Color SELECTED_BG = new Color(209, 237, 248);
    private static final Color UNSELECTED_BG = new Color(251, 251, 251);
	
    private JLabel lbl_item_icon;
	private String pathToImage;
	private String imageName;
    private OnCaptureMenuItemSelected onCaptureMenuItemSelectedCallback;

    public interface OnCaptureMenuItemSelected {
        void onSelected(CaptureMenu_item panel);
        void onDeselected();
    }

	public CaptureMenu_item(String pathToImage, String imageName, OnCaptureMenuItemSelected onCaptureMenuItemSelectedCallback, ArrayList<JPanel> captureMenu_itemList) {
		this.pathToImage = pathToImage;
		this.imageName = imageName;
        this.onCaptureMenuItemSelectedCallback = onCaptureMenuItemSelectedCallback;
		
        setPreferredSize(new Dimension(160, 145));
        setLayout(null);
        setBackground(UNSELECTED_BG);
        
        lbl_item_icon = new JLabel();
        lbl_item_icon.setBounds(10, 10, 125, 85);
        updateIcon();
        add(lbl_item_icon);

        JLabel lbl_item_name = new JLabel(imageName, SwingConstants.CENTER);
        lbl_item_name.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lbl_item_name.setBounds(10, 105, 140, 20);
        add(lbl_item_name);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSelection();
            }
        });

        captureMenu_itemList.add(this);
	}
	
	private void updateIcon() {
		lbl_item_icon.setIcon(ImageHelper.getScaledIconFromFile(pathToImage, 125, 85));
	}

    public boolean isSelected() {
        return getBackground().equals(SELECTED_BG);
    }

    public void toggleSelection() {
        if (isSelected()) {
            deselect();
        } else {
            select();
        }
    }

    public void select() {
        setBackground(SELECTED_BG);
        updateIcon();
        if (onCaptureMenuItemSelectedCallback != null) {
            onCaptureMenuItemSelectedCallback.onSelected(this);
        }
    }

    public void deselect() {
        setBackground(UNSELECTED_BG);
        if (onCaptureMenuItemSelectedCallback != null) {
            onCaptureMenuItemSelectedCallback.onDeselected();
        }
    }

    public String getPathToImage() {
        return pathToImage;
    }
}
