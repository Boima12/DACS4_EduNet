package org.example.common.utils.gui;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ImageHelper {
	public static ImageIcon getScaledIcon(String path, int w, int h) {
	    ImageIcon icon = new ImageIcon(ImageHelper.class.getResource(path));
	    Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
	    return new ImageIcon(img);
	}
}
