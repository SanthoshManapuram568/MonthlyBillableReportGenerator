package com.srm.billable;

import com.srm.billable.config.AppConfig;
import com.srm.billable.http.HttpServer;
import com.srm.billable.outlook.OutlookService;
import com.srm.billable.tray.TrayManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for the Billable Report Helper desktop application.
 *
 * Starts the HTTP server on port 8085 and initializes the system tray.
 */
@Slf4j
public class HelperApplication {

    public static void main(String[] args) {
        try {
            log.info("========================================");
            log.info("Billable Report Helper - v1.0.0");
            log.info("Starting on localhost:8085");
            log.info("========================================");

//            com.srm.billable.startup.StartupManager.registerIfNeeded();

            // Initialize Outlook service
            OutlookService outlookService = new OutlookService();
            outlookService.checkOutlookStatus();

            // Start HTTP server
            HttpServer httpServer = new HttpServer(outlookService);
            httpServer.start();

            // Initialize system tray
            TrayManager trayManager = new TrayManager(httpServer);
            trayManager.initialize();

            log.info("Application started successfully");

        } catch (Exception ex) {
            log.error("Failed to start application", ex);
            System.exit(1);
        }
    }
}
