# Billable Report Helper

Desktop companion application for the **Monthly Billable Report Generator**.

The helper runs locally on Windows, exposes a lightweight HTTP server, communicates with the hosted website over `localhost`, and creates Microsoft Outlook drafts with automatic PDF attachments.

---

# Overview

The Desktop Helper is responsible for:

- Running a local HTTP server (`127.0.0.1:8085`)
- Receiving requests from the hosted website
- Creating Outlook drafts using Microsoft Outlook Desktop
- Attaching uploaded PDF timesheets
- Opening the draft for user review
- Returning success/failure responses

---

# Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Desktop Application |
| Maven | Build Tool |
| Embedded HttpServer | Local REST API |
| JACOB | Outlook COM Automation |
| SLF4J | Logging |
| Logback | Logging Configuration |

---

# Project Structure

```
BillableReportHelper
│
├── src
│   └── main
│       ├── java
│       │   └── com.srm.billable
│       │       ├── HelperApplication.java
│       │       ├── config
│       │       ├── dto
│       │       ├── http
│       │       ├── outlook
│       │       ├── tray
│       │       └── util
│       │
│       └── resources
│           ├── application.properties
│           ├── logback.xml
│           └── icon.ico
│
├── target
│
├── pom.xml
│
└── README.md
```

---

# Prerequisites

Before building, ensure the following are installed:

- Java 17+
- Maven 3.8+
- Microsoft Outlook Desktop (Classic Outlook)
- Windows 10 / Windows 11

Verify installation:

```cmd
java -version
```

```cmd
mvn -version
```

```cmd
jpackage --version
```

---

# Running from IntelliJ

Run

```
HelperApplication.java
```

or

```
Run → HelperApplication
```

Expected output:

- HTTP Server starts
- System Tray icon appears
- Outlook detected
- Port 8085 listening

---

# Build Project

Open terminal inside

```
BillableReportHelper
```

Run

```cmd
mvn clean package
```

Expected output

```
BUILD SUCCESS
```

Generated files

```
target/

BillableReportHelper.jar

jacob-1.21-x64.dll
```

---

# Package MSI

## Install WiX Toolset

Download and install WiX Toolset v3.x.

Verify installation

```cmd
candle.exe
```

```cmd
light.exe
```

Both commands should execute without errors.

---

## Generate MSI

Navigate to project root

```cmd
cd D:\MonthlyBillableReportGenerator\BillableReportHelper
```

Run

```cmd
jpackage ^
--type msi ^
--name BillableReportHelper ^
--input target ^
--main-jar BillableReportHelper.jar ^
--main-class com.srm.billable.HelperApplication ^
--java-options "-Djacob.dll.path=$APPDIR\jacob-1.21-x64.dll" ^
--icon src\main\resources\icon.ico ^
--win-shortcut ^
--win-menu ^
--win-dir-chooser ^
--win-per-user-install ^
--vendor "SRM Tech" ^
--app-version 1.0.0
```

Output

```
BillableReportHelper-1.0.0.msi
```

---

# Install Helper

Double-click

```
BillableReportHelper-1.0.0.msi
```

Follow the installation wizard.

Default installation path

```
C:\Users\<username>\BillableReportHelper
```

(or the location selected during installation)

---

# Verify Installation

Start the application.

Expected behaviour

- System Tray icon appears
- Helper starts silently
- HTTP Server starts
- Outlook detection completes

---

# Verify Helper is Running

## Option 1 - Browser

Open

```
http://127.0.0.1:8085/health
```

Expected response

```json
{
  "status":"UP",
  "version":"1.0.0"
}
```

---

## Option 2 - Command Prompt

```cmd
netstat -ano | findstr :8085
```

Expected

```
TCP 127.0.0.1:8085 LISTENING
```

---

## Option 3 - Task Manager

Open

```
Task Manager
```

Look for

```
BillableReportHelper.exe
```

Status

```
Running
```

---

## Option 4 - PowerShell

```powershell
Get-Process | findstr BillableReportHelper
```

---

# API Endpoints

## Health Check

```
GET /health
```

Response

```json
{
  "status":"UP",
  "version":"1.0.0"
}
```

---

## Create Outlook Draft

```
POST /api/outlook/draft
```

Consumes

```
multipart/form-data
```

Fields

- recipient
- cc
- subject
- htmlBody
- attachments

---

# Logs

Location

```
%temp%\billable-helper-logs\
```

Open logs

```cmd
explorer %temp%\billable-helper-logs
```

---

# Troubleshooting

## Helper not running

Verify

```cmd
java -version
```

---

Verify

```cmd
netstat -ano | findstr :8085
```

---

## Port already in use

Check

```cmd
netstat -ano | findstr :8085
```

Kill process

```cmd
taskkill /PID <PID> /F
```

Restart helper.

---

## Outlook not detected

Ensure Microsoft Outlook Desktop (Classic Outlook) is installed.

Restart the helper.

---

## Health endpoint unavailable

Open

```
http://127.0.0.1:8085/health
```

If unavailable

- Helper not running
- Firewall blocking localhost
- Port conflict

---

# Development Workflow

```
Modify Source Code

↓

Run from IntelliJ

↓

Test Outlook Draft

↓

mvn clean package

↓

Generate MSI using jpackage

↓

Install MSI

↓

Test on Clean Machine
```

---

# Release Checklist

- Build successful
- Health endpoint working
- Outlook draft opens
- PDF attachments working
- Logs verified
- MSI generated
- Installation tested
- Uninstallation tested

---

# Versioning

Current Version

```
1.0.0
```

Future versions should update

- pom.xml
- jpackage command
- Health endpoint version
- README

---

# License

Internal SRM Utility

Copyright © 2026 Santhosh Kumar Manapuram
