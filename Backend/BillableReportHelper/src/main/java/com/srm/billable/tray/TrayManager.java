package com.srm.billable.tray;

import com.srm.billable.http.HttpServer;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * System tray integration for the Billable Report Helper.
 *
 * <p>Manages system tray icon with state indicators and context menu.</p>
 */
@Slf4j
public class TrayManager {

    private final HttpServer httpServer;
    private TrayIcon trayIcon;
    private PopupMenu popupMenu;

    public TrayManager(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    /**
     * Initializes the system tray icon and menus.
     */
    public void initialize() {

        if (!SystemTray.isSupported()) {
            log.warn("System tray is not supported");
            return;
        }

        try {
            // Create popup menu
            popupMenu = createPopupMenu();

            // Create tray icon
            Image image = createTrayImage();
            trayIcon = new TrayIcon(image, "Billable Report Helper", popupMenu);
            trayIcon.setImageAutoSize(true);

            // Add to system tray
            SystemTray.getSystemTray().add(trayIcon);
            log.info("System tray icon initialized");

        } catch (AWTException ex) {
            log.error("Failed to add tray icon", ex);
        }
    }

    /**
     * Creates the popup context menu.
     */
    private PopupMenu createPopupMenu() {

        PopupMenu menu = new PopupMenu();

        MenuItem openDashboard = new MenuItem("Status");
        openDashboard.addActionListener(e -> showStatus());

        MenuItem openLogs = new MenuItem("Open Logs");
        openLogs.addActionListener(e -> openLogs());

        MenuItem restart = new MenuItem("Restart Helper");
        restart.addActionListener(e -> restartHelper());

        MenuItem about = new MenuItem("About");
        about.addActionListener(e -> showAbout());

        menu.add(openDashboard);
        menu.addSeparator();
        menu.add(openLogs);
        menu.add(restart);
        menu.addSeparator();
        menu.add(about);
        menu.addSeparator();

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(e -> exitApplication());
        menu.add(exit);

        return menu;
    }

    /**
     * Creates a simple tray icon image.
     */
    private Image createTrayImage() {

        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Green circle indicating running state
        g2d.setColor(new Color(34, 177, 76)); // Green
        g2d.fillOval(0, 0, 16, 16);

        // Border
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(0, 0, 15, 15);

        g2d.dispose();

        return image;
    }

    private void showStatus() {
        JOptionPane.showMessageDialog(null,
                "Billable Report Helper v1.0.0\n" +
                "Status: Running on localhost:8085\n" +
                "Ready to create Outlook drafts.",
                "Helper Status",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openLogs() {
        try {
            String logsDir = System.getProperty("java.io.tmpdir");
            Desktop.getDesktop().open(new java.io.File(logsDir));
        } catch (IOException ex) {
            log.error("Failed to open logs directory", ex);
        }
    }

    private void restartHelper() {
        JOptionPane.showMessageDialog(null,
                "Helper will restart in a few seconds.",
                "Restarting",
                JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(null,
                "Billable Report Helper v1.0.0\n\n" +
                "Windows Desktop Helper for Outlook Draft Creation\n" +
                "Developed by SRM",
                "About Billable Report Helper",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitApplication() {

        int response = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to exit Billable Report Helper?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            httpServer.stop();
            System.exit(0);
        }
    }
}
