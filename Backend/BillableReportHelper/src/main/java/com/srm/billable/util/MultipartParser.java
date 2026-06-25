package com.srm.billable.util;

import com.srm.billable.dto.DraftRequest;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses multipart/form-data from HTTP request.
 * Saves uploaded files to Windows temp directory.
 */
@Slf4j
public class MultipartParser {

    /**
     * Parses multipart form data from the request and creates a DraftRequest.
     *
     * @param exchange HTTP exchange from the handler
     * @param contentType Content-Type header value
     * @return Parsed DraftRequest with fields and file references
     */
    public static DraftRequest parse(HttpExchange exchange, String contentType) throws IOException {

        if (contentType == null || !contentType.contains("multipart/form-data")) {
            throw new IllegalArgumentException("Invalid Content-Type: " + contentType);
        }

        // Extract boundary from Content-Type header
        String boundary = extractBoundary(contentType);
        if (boundary == null || boundary.isEmpty()) {
            throw new IllegalArgumentException("Missing boundary in Content-Type");
        }

        DraftRequest request = new DraftRequest();
        List<File> attachments = new ArrayList<>();

        byte[] bodyBytes;
        try (InputStream is = exchange.getRequestBody()) {
            bodyBytes = is.readAllBytes();
        }

        parseFormData(bodyBytes, boundary, request, attachments);

        request.setAttachments(attachments);
        return request;
    }

    /**
     * Extracts the boundary string from Content-Type header.
     */
    private static String extractBoundary(String contentType) {
        if (contentType == null) return null;

        int idx = contentType.indexOf("boundary=");
        if (idx == -1) return null;

        String boundary = contentType.substring(idx + 9);
        if (boundary.startsWith("\"")) {
            boundary = boundary.substring(1);
        }
        if (boundary.endsWith("\"")) {
            boundary = boundary.substring(0, boundary.length() - 1);
        }
        return boundary.trim();
    }

    /**
     * Parses the multipart form data and populates request object and attachments list.
     */
    private static void parseFormData(byte[] bodyBytes, String boundary,
                                      DraftRequest request, List<File> attachments) throws IOException {

        String delimiter = "--" + boundary;
        String bodyText = new String(bodyBytes, StandardCharsets.ISO_8859_1);
        String[] rawParts = bodyText.split(delimiter);

        for (String rawPart : rawParts) {
            if (rawPart == null || rawPart.isBlank() || rawPart.equals("--") || rawPart.equals("--\r\n")) {
                continue;
            }

            String normalizedPart = rawPart;
            if (normalizedPart.startsWith("\r\n")) {
                normalizedPart = normalizedPart.substring(2);
            }
            if (normalizedPart.endsWith("\r\n")) {
                normalizedPart = normalizedPart.substring(0, normalizedPart.length() - 2);
            }

            int headerEnd = normalizedPart.indexOf("\r\n\r\n");
            if (headerEnd < 0) {
                continue;
            }

            String headerSection = normalizedPart.substring(0, headerEnd);
            String contentSection = normalizedPart.substring(headerEnd + 4);

            String fieldName = extractParameter(headerSection, "name");
            String fileName = extractParameter(headerSection, "filename");

            if (fieldName == null) {
                continue;
            }

            if (fileName != null && !fileName.isBlank()) {
                byte[] fileBytes = contentSection.getBytes(StandardCharsets.ISO_8859_1);
                saveFile(fileBytes, fileName, attachments);
            } else {
                populateField(request, fieldName, contentSection.trim());
            }
        }
    }

    /**
     * Extracts a parameter value from a header line.
     * E.g., "name=\"field\"" -> "field"
     */
    private static String extractParameter(String line, String paramName) {
        int start = line.indexOf(paramName + "=\"");
        if (start == -1) return null;

        start += paramName.length() + 2;
        int end = line.indexOf("\"", start);
        if (end == -1) return null;

        return line.substring(start, end);
    }

    /**
     * Reads content until the next boundary marker.
     */
    private static void saveFile(byte[] fileBytes, String fileName, List<File> attachments) throws IOException {
        String safeFileName = sanitizeFileName(fileName);

        // Keep file names exactly as uploaded for Outlook display,
        // but isolate each upload in a unique temp directory to avoid collisions.
        Path requestTempDir = Files.createTempDirectory("mbrh-");
        File tempFile = requestTempDir.resolve(safeFileName).toFile();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(fileBytes);
        }

        attachments.add(tempFile);
        log.info("Saved temporary attachment: {}", fileName);
    }

    private static String sanitizeFileName(String fileName) {
        String sanitized = fileName
                .replace("\\", "")
                .replace("/", "")
                .replace(":", "_")
                .replace("*", "_")
                .replace("?", "_")
                .replace("\"", "_")
                .replace("<", "_")
                .replace(">", "_")
                .replace("|", "_")
                .trim();

        return sanitized.isEmpty() ? "attachment.pdf" : sanitized;
    }

    /**
     * Populates a field in the DraftRequest based on field name.
     */
    private static void populateField(DraftRequest request, String fieldName, String value) {
        switch (fieldName) {
            case "recipient":
                request.setRecipient(value);
                break;
            case "cc":
                request.setCc(value);
                break;
            case "subject":
                request.setSubject(value);
                break;
            case "htmlBody":
                request.setHtmlBody(value);
                break;
        }
    }
}
