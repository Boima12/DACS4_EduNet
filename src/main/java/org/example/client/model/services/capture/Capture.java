package org.example.client.model.services.capture;

import org.example.common.utils.system.CryptUtils;
import org.example.common.utils.system.ScreenShotUtils;

import java.awt.image.BufferedImage;

public class Capture {
    public String getScreenShotEncodedBase64() throws Exception {
        BufferedImage screenshot = ScreenShotUtils.capture();
        return CryptUtils.encodeToBase64(screenshot, "png");
    }
}
