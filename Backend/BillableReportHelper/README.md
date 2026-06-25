# Billable Report Helper

Windows Desktop Helper for creating Outlook email drafts with automatic PDF attachment.

## Overview

The Billable Report Helper is a lightweight Java application that:
- Listens on `http://localhost:8085`
- Receives multipart form data from the website frontend
- Uses Windows COM automation (JACOB) to create Outlook email drafts
- Automatically attaches PDF files
- Displays the draft in Outlook (without sending)
- Cleans up temporary files

## Architecture

```
src/main/java/com/srm/billable/
├── HelperApplication.java       # Entry point
├── config/
│   └── AppConfig.java           # Configuration constants
├── http/
│   ├── HttpServer.java          # Embedded HTTP server
│   ├── HealthHandler.java       # GET /health endpoint
│   └── DraftHandler.java        # POST /api/outlook/draft endpoint
├── outlook/
│   └── OutlookService.java      # Outlook COM automation
├── tray/
│   └── TrayManager.java         # System tray integration
├── util/
│   └── MultipartParser.java     # Multipart form data parsing
└── dto/
    ├── ApiResponse.java         # API response wrapper
    ├── HealthResponse.java      # Health check response
    └── DraftRequest.java        # Draft request DTO
```

## Requirements

- Java 17 or higher
- Maven 3.6+
- Microsoft Outlook Desktop (installed and registered)
- Windows OS

## Building

```bash
cd BillableReportHelper
mvn clean package
```

This generates `target/BillableReportHelper.jar`

## Running

```bash
java -jar BillableReportHelper.jar
```

The helper will:
1. Check Outlook availability
2. Start HTTP server on port 8085
3. Initialize system tray icon
4. Accept requests from the frontend

## API Endpoints

### Health Check
```
GET /health
```

Response:
```json
{
  "status": "UP",
  "version": "1.0.0"
}
```

### Create Outlook Draft
```
POST /api/outlook/draft
Content-Type: multipart/form-data
```

Fields:
- `recipient` (required): Recipient email address
- `cc` (optional): CC email address
- `subject` (required): Email subject
- `htmlBody` (required): Email body in HTML format
- `attachments` (required): PDF files (1 or more)

Response (Success):
```json
{
  "success": true,
  "message": "Outlook draft created successfully"
}
```

Response (Error):
```json
{
  "success": false,
  "error": "Error message here"
}
```

## Features

- **Lightweight**: Uses embedded HttpServer, minimal dependencies
- **COM Automation**: Direct Outlook integration via JACOB
- **Multipart Parser**: Custom parser for form data without external dependencies
- **Temporary Files**: Uploads stored in Windows temp directory, cleaned after use
- **System Tray**: Runs in background with system tray icon
- **Retry Logic**: 3 attempts with 500ms delay for COM operations
- **Logging**: SLF4J + Logback with file rotation
- **CORS Support**: Allows requests from any origin

## Dependencies

- **Lombok**: Code generation for getters/setters/constructors
- **JACOB**: Windows COM automation
- **SLF4J/Logback**: Logging
- **Gson**: JSON serialization

## Future Enhancements

The architecture supports:
- Auto-download from OpenAir
- Silent updates
- Multiple Outlook profiles
- Outlook signature detection
- Scheduled draft creation
- Dashboard UI
- Configuration file support

## Limitations

- Windows only (due to COM automation)
- Requires Outlook Desktop (not Outlook Web Access)
- Requires Outlook to be registered on the system
- No persistent storage or database

## License

Internal SRM Tool
