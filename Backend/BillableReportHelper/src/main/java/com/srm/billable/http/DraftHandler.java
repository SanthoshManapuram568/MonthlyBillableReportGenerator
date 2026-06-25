package com.srm.billable.http;

import com.google.gson.Gson;
import com.srm.billable.dto.ApiResponse;
import com.srm.billable.dto.DraftRequest;
import com.srm.billable.outlook.OutlookService;
import com.srm.billable.util.MultipartParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handler for POST /api/outlook/draft endpoint.
 * Parses multipart/form-data and creates Outlook draft.
 */
@Slf4j
public class DraftHandler implements HttpHandler {

    private static final Gson gson = new Gson();
    private final OutlookService outlookService;

    public DraftHandler(OutlookService outlookService) {
        this.outlookService = outlookService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Add CORS headers
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        // Handle OPTIONS pre-flight
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
            return;
        }

        if (!exchange.getRequestMethod().equals("POST")) {
            sendJsonError(exchange, 405, "Method not allowed");
            return;
        }

        try {
            log.info("Draft creation request received");

            // Parse multipart form data
            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            DraftRequest request = MultipartParser.parse(exchange, contentType);

            if (request == null || request.getRecipient() == null || request.getRecipient().isEmpty()) {
                sendJsonError(exchange, 400, "Missing required fields: recipient, subject, htmlBody, attachments");
                return;
            }

            if (request.getAttachments() == null || request.getAttachments().isEmpty()) {
                sendJsonError(exchange, 400, "At least one attachment is required");
                return;
            }

            // Create Outlook draft
            outlookService.createDraft(request);

            ApiResponse response = ApiResponse.success("Outlook draft created successfully");
            sendJsonResponse(exchange, 200, response);

        } catch (Exception ex) {
            log.error("Error creating draft", ex);
            sendJsonError(exchange, 500, "Failed to create Outlook draft: " + ex.getMessage());
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, ApiResponse response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        String json = gson.toJson(response);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void sendJsonError(HttpExchange exchange, int statusCode, String message) throws IOException {
        ApiResponse response = ApiResponse.error(message);
        sendJsonResponse(exchange, statusCode, response);
    }
}
