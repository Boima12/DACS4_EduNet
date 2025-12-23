package org.example.common.utils.gui;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Font;

/**
 * File hỗ trợ hiển thị các hộp thoại cảnh báo, thông báo, lỗi, xác nhận.
 *
 */
public class Alert {

    static {
        // Tuỳ chọn: chỉnh font chung cho JOptionPane
        UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 15));
        UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.PLAIN, 15));
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showWarning(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Chú ý",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static void showInfo(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static boolean showConfirm(String message) {
        int result = JOptionPane.showConfirmDialog(
                null,
                message,
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        return result == JOptionPane.YES_OPTION;
    }
}
