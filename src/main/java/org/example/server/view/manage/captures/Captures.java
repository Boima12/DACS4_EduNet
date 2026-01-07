package org.example.server.view.manage.captures;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.Color;
import java.util.ArrayList;

import org.example.common.utils.gui.Alert;
import org.example.common.utils.gui.NotificationInputModal;
import org.example.common.utils.gui.RoundedBorder;
import org.example.common.utils.gui.WrapLayout;
import org.example.common.utils.system.ScreenShotUtils;
import org.example.server.ServerStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings({"FieldCanBeLocal", "Convert2MethodRef", "PatternVariableCanBeUsed"})
public class Captures {

	private JFrame frame;
    private static JFrame parentFrame;
    private static JPanel panel_captureMenu;
    public static ArrayList<JPanel> captureMenu_itemList = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(Captures.class);

    private static String clientName;
    private static String currentSelectedImagePath;

	/**
	 * Create the application.
	 */
	public Captures(JFrame parentFrame, String clientName) {
        Captures.parentFrame = parentFrame;
        Captures.clientName = clientName;
		initialize();	// line này dùng để bật WindowBuilder, nếu comment line này sẽ tối ưu ứng dụng nhưng không thể sài Eclipse windowBuilder trong file này

        ServerStates.setOnCaptureResponseListenerCallback((pathToImage, imageName) -> {
            panel_captureMenu.add(createCaptureMenu_item(pathToImage, imageName));
            panel_captureMenu.revalidate();
            panel_captureMenu.repaint();
        });

        setupCaptureMenu();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 965, 407);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(build());
	}
	
	public static JPanel build() {
		JPanel panel = new JPanel();
		panel.setBackground(new Color(251, 251, 251));
		panel.setBounds(0, 0, 951, 370);
		panel.setLayout(null);
		
		JPanel panel_captureOptions = new JPanel();
		panel_captureOptions.setLayout(null);
//		panel_captureOptions.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel_captureOptions.setBackground(new Color(251, 251, 251));
		panel_captureOptions.setBounds(0, 0, 951, 55);
		panel.add(panel_captureOptions);
		
		JButton btn_capture = new JButton("Yêu cầu chụp ảnh");
		btn_capture.setForeground(Color.BLACK);
		btn_capture.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_capture.setBorder(new RoundedBorder(8));
		btn_capture.setBackground(new Color(251, 251, 251));
		btn_capture.setBounds(10, 10, 150, 35);
		btn_capture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCaptureRequest();
			}
		});
		panel_captureOptions.add(btn_capture);
		
		JButton btn_viewImage = new JButton("Xem ảnh");
		btn_viewImage.setForeground(Color.BLACK);
		btn_viewImage.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_viewImage.setBorder(new RoundedBorder(8));
		btn_viewImage.setBackground(new Color(251, 251, 251));
		btn_viewImage.setBounds(180, 10, 150, 35);
		btn_viewImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				onViewSelectedImage();
			}
		});
		panel_captureOptions.add(btn_viewImage);
		
		JButton btn_deleteCapture = new JButton("Xóa ảnh");
		btn_deleteCapture.setForeground(Color.BLACK);
		btn_deleteCapture.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_deleteCapture.setBorder(new RoundedBorder(8));
		btn_deleteCapture.setBackground(new Color(251, 251, 251));
		btn_deleteCapture.setBounds(350, 10, 150, 35);
		btn_deleteCapture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onDeleteSelectedImage();
			}
		});
		panel_captureOptions.add(btn_deleteCapture);
		
		JButton btn_notification = new JButton("Thông báo");
		btn_notification.setForeground(Color.BLACK);
		btn_notification.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_notification.setBorder(new RoundedBorder(8));
		btn_notification.setBackground(new Color(251, 251, 251));
		btn_notification.setBounds(520, 10, 150, 35);
		btn_notification.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onNotificationSingle();
			}
		});
		panel_captureOptions.add(btn_notification);
		
		JButton btn_refresh = new JButton("Làm mới");
		btn_refresh.setForeground(Color.BLACK);
		btn_refresh.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_refresh.setBorder(new RoundedBorder(8));
		btn_refresh.setBackground(new Color(251, 251, 251));
		btn_refresh.setBounds(791, 10, 150, 35);
		btn_refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setupCaptureMenu();
			}
		});
		panel_captureOptions.add(btn_refresh);
		
		panel_captureMenu = new JPanel();
		panel_captureMenu.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
		panel_captureMenu.setBackground(new Color(251, 251, 251));
		panel_captureMenu.setBounds(0, 53, 951, 317);
		JScrollPane panel_captureMenuScrollPane = new JScrollPane(panel_captureMenu);
		panel_captureMenuScrollPane.setBorder(null);
		panel_captureMenuScrollPane.setBounds(0, 53, 951, 317);
		panel_captureMenuScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_captureMenuScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(panel_captureMenuScrollPane);
        
        return panel;
	}

    public static CaptureMenu_item createCaptureMenu_item(String pathToImage, String imageName) {
        return new CaptureMenu_item(pathToImage, imageName,
            new CaptureMenu_item.OnCaptureMenuItemSelected() {
                @Override
                public void onSelected(CaptureMenu_item panel) {
                    // Deselect other items
                    for (JPanel item : captureMenu_itemList) {
                        if (item != panel && item instanceof CaptureMenu_item) {
                            ((CaptureMenu_item) item).deselect();
                        }
                    }

                    currentSelectedImagePath = pathToImage;
                }

                @Override
                public void onDeselected() {
                    currentSelectedImagePath = null;
                }
            },
            captureMenu_itemList
        );
    }

    private static void setupCaptureMenu() {
        // clear all panel_captureMenu items first
        panel_captureMenu.removeAll();
        captureMenu_itemList.clear();

        // Load all images from serverLocalStorage/captures/<clientName>/
        String capturesPath = Paths.get("serverLocalStorage", "captures", clientName).toString();
        File capturesDir = new File(capturesPath);

        log.info("Looking for captures in: {}", capturesDir.getAbsolutePath());

        if (capturesDir.exists() && capturesDir.isDirectory()) {
            File[] imageFiles = capturesDir.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                return lowerName.endsWith(".png") || lowerName.endsWith(".jpg") ||
                       lowerName.endsWith(".jpeg") || lowerName.endsWith(".gif");
            });

            if (imageFiles != null && imageFiles.length > 0) {
                for (File imageFile : imageFiles) {
                    String imagePath = imageFile.getAbsolutePath();
                    String imageName = imageFile.getName();

//                    log.info("imagePath: {} - imageName: {}'", imagePath, imageName);

                    panel_captureMenu.add(createCaptureMenu_item(imagePath, imageName));
                }
                log.info("Loaded {} image(s) for client '{}'", imageFiles.length, clientName);
            } else {
                log.info("No images found for client '{}'", clientName);
            }
        } else {
            log.warn("Captures directory does not exist for client '{}': {}", clientName, capturesPath);
        }

        panel_captureMenu.revalidate();
        panel_captureMenu.repaint();
        log.info("panel_captureMenu refreshed.");
    }

    public static void onCleanup() {
        File capturesRoot = Paths.get("serverLocalStorage", "captures").toFile();
        if (!capturesRoot.exists()) {
            log.info("Captures root does not exist, nothing to clean: {}", capturesRoot.getAbsolutePath());
            return;
        }

        File[] children = capturesRoot.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }

        captureMenu_itemList.clear();
        if (panel_captureMenu != null) {
            panel_captureMenu.removeAll();
            panel_captureMenu.revalidate();
            panel_captureMenu.repaint();
        }

        log.info("Cleaned all captures under {}", capturesRoot.getAbsolutePath());
    }

    private static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] nested = file.listFiles();
            if (nested != null) {
                for (File f : nested) {
                    deleteRecursively(f);
                }
            }
        }
        if (!file.delete()) {
            log.warn("Failed to delete {}", file.getAbsolutePath());
        }
    }

    private static void onCaptureRequest() {
        if (ServerStates.onCaptureRequestListener != null) ServerStates.onCaptureRequestListener.onCaptureRequest(clientName);
    }

    private static void onNotificationSingle() {
        NotificationInputModal modal = new NotificationInputModal(parentFrame);
        String message = modal.showDialog();

        if (message != null && !message.isEmpty()) {
            if (ServerStates.onNotificationSingleRequestListener != null) ServerStates.onNotificationSingleRequestListener.onNotificationSingleRequest(clientName, message);
        }
    }

    private static void onViewSelectedImage() {
        ScreenShotUtils.openImageWithOSApp(currentSelectedImagePath);
    }

    private static void onDeleteSelectedImage() {
        if (currentSelectedImagePath == null || currentSelectedImagePath.isEmpty()) {
            Alert.showInfo("Không có ảnh nào được chọn để xóa.");
            return;
        }

        File imageFile = new File(currentSelectedImagePath);
        if (!imageFile.exists()) {
            log.warn("Selected capture does not exist on disk: {}", currentSelectedImagePath);
            return;
        }

        if (imageFile.delete()) {
            log.info("Deleted capture: {}", currentSelectedImagePath);
            currentSelectedImagePath = null;

            CaptureMenu_item toRemove = null;
            for (JPanel item : captureMenu_itemList) {
                if (item instanceof CaptureMenu_item) {
                    CaptureMenu_item captureItem = (CaptureMenu_item) item;
                    if (imageFile.getAbsolutePath().equals(captureItem.getPathToImage())) {
                        toRemove = captureItem;
                        break;
                    }
                }
            }

            if (toRemove != null) {
                captureMenu_itemList.remove(toRemove);
                panel_captureMenu.remove(toRemove);
                panel_captureMenu.revalidate();
                panel_captureMenu.repaint();
            }
        } else {
            log.warn("Failed to delete capture: {}", currentSelectedImagePath);
        }
    }
}
