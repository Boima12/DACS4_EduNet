package org.example.common.utils.gui;

import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

/**
 * File giúp load icon cho các component giao diện
 *
 */
public class ImageHelper {
	public static ImageIcon getScaledIcon(String path, int w, int h) {
	    java.net.URL resource = ImageHelper.class.getResource(path);
	    if (resource == null) {
	        System.err.println("Classpath resource not found: " + path);
	        return null;
	    }

	    ImageIcon icon = new ImageIcon(resource);
	    Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
	    return new ImageIcon(img);
	}

	public static ImageIcon getScaledIconFromFile(String absolutePath, int w, int h) {
	    File imageFile = new File(absolutePath);
	    if (!imageFile.exists()) {
	        System.err.println("Image file not found: " + absolutePath);
	        return null;
	    }

	    ImageIcon icon = new ImageIcon(absolutePath);
	    Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
	    return new ImageIcon(img);
	}
}
