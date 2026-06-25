package com.srm.billable.http;

import com.srm.billable.outlook.OutlookService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Embedded HTTP server on port 8085.
 * Handles requests from the website frontend.
 */
@Slf4j
public class HttpServer {

    private final int PORT = 8085;
    private com.sun.net.httpserver.HttpServer server;
    private final OutlookService outlookService;

    public HttpServer(OutlookService outlookService) {
        this.outlookService = outlookService;
    }

    /**
     * Starts the HTTP server and registers request handlers.
     */
    public void start() throws IOException {

        server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        // Register endpoints
        server.createContext("/health", new HealthHandler());
        server.createContext("/api/outlook/draft", new DraftHandler(outlookService));

        server.setExecutor(null); // Default executor
        server.start();

        log.info("HTTP Server started on port {}", PORT);
    }

    /**
     * Stops the HTTP server gracefully.
     */
    public void stop() {
        if (server != null) {
            server.stop(5);
            log.info("HTTP Server stopped");
        }
    }
}
