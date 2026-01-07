package org.example.common.utils.system;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

@SuppressWarnings("UnnecessaryLocalVariable")
public class ScreenShotUtils {

	public static BufferedImage capture() throws Exception {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        Robot robot = new Robot();
        BufferedImage screen = robot.createScreenCapture(screenRect);

        return screen;
	}
	
    public static void captureAndSave(String filePath) throws Exception {
        BufferedImage screen = capture();

        File output = new File(filePath);
        output.getParentFile().mkdirs(); // tạo thư mục nếu chưa có

        ImageIO.write(screen, "png", output);
    }

    public static void openImageWithOSApp(String imagePath) {
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.out.println("Image file does not exist: " + imagePath);
            return;
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(imageFile);
            } catch (IOException e) {
                System.out.println("Failed to open image: " + e.getMessage());
            }
        } else {
            System.out.println("Desktop is not supported on this system.");
        }
    }
}
