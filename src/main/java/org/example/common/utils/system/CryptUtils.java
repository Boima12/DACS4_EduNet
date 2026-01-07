package org.example.common.utils.system;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class CryptUtils {

    // Convert BufferedImage to Base64 string
    public static String encodeToBase64(BufferedImage image, String format) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    // Convert Base64 string back to BufferedImage
    public static BufferedImage decodeFromBase64(String base64String) throws Exception {
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(inputStream);
        }
    }
}
