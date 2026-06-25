# 📊 Monthly Billable Report Generator

A lightweight enterprise utility for generating monthly billable reports, calculating billable hours, and creating Microsoft Outlook drafts with automatic PDF timesheet attachments.

This application was built to eliminate manual calculations, repetitive copy-paste operations, and repetitive Outlook email preparation performed every month for billing submissions.

---

# ✨ Why this project?

Preparing monthly billable reports usually involves:

- Calculating billable hours manually
- Tracking holidays and leaves
- Adjusting extra hours
- Creating Outlook emails
- Copying formatted tables
- Attaching multiple OpenAir PDF timesheets

This utility automates the entire process and reduces it to just a few clicks.

---

# 🚀 Features

## 📅 Holiday Management

- Interactive holiday calendar
- Weekend exclusion
- Multiple holiday selection
- Automatic holiday summary
- Dynamic billable day calculation

---

## 📝 Leave & Extra Hours

- Weekly leave tracking
- Weekly extra hour tracking
- Automatic billable hour recalculation
- Live summary generation

---

## 📈 Monthly Billable Report

Automatically calculates:

- Total Working Days
- Total Days Worked
- Total Leave Days
- Extra Hours
- Weekly Billable Hours
- Total Monthly Billable Hours

---

## 📋 Outlook Integration

Generate Outlook Draft with:

- HTML formatted report
- Recipient
- CC
- Subject
- Automatic PDF attachments
- Outlook Draft Preview

No SMTP.

No Microsoft Graph.

No Outlook credentials.

Uses the user's already authenticated Microsoft Outlook Desktop.

---

## 📄 OpenAir Timesheet Upload

- Drag & Drop PDF upload
- Multiple file upload
- File validation
- Attachment preview
- Automatic attachment to Outlook Draft

---

## 🔒 Safe by Design

- No passwords stored
- No cloud email services
- No SMTP configuration
- No Azure configuration
- No Microsoft Graph permissions
- Temporary files are deleted automatically

---

# 🏗 Architecture

```
                    Hosted Website
                (Netlify / GitHub Pages)
                          │
                          │
            HTTP (localhost only)
                          │
                          ▼
              BillableReportHelper
             (Windows Desktop Helper)
                          │
                          ▼
            Microsoft Outlook Desktop
                          │
                          ▼
          Outlook Draft with PDF Attachments
```

---

# 📁 Project Structure

```
MonthlyBillableReportGenerator
│
├── index.html
├── css
├── js
│
├── assets
│
└── BillableReportHelper
    ├── src
    ├── pom.xml
    ├── README.md
    └── target
```

---

# ⚙️ Technology Stack

## Frontend

- HTML5
- CSS3
- Bootstrap 5
- Vanilla JavaScript

## Desktop Helper

- Java 17
- Maven
- Embedded HTTP Server
- JACOB (COM Automation)
- SLF4J
- Logback

## Desktop Integration

- Microsoft Outlook Desktop
- Windows COM Automation
- Windows System Tray

---

# 🚀 Installation

## Step 1

Open the hosted website.

```
https://your-netlify-url.netlify.app
```

---

## Step 2

Download and install

```
BillableReportHelper
```

The Desktop Helper:

- Starts automatically
- Runs in the Windows System Tray
- Listens on

```
http://127.0.0.1:8085
```

---

## Step 3

Generate your monthly report.

---

## Step 4

Upload weekly OpenAir PDF timesheets.

---

## Step 5

Click

```
Open Outlook Draft
```

The Desktop Helper will automatically:

- Create Outlook Draft
- Insert HTML report
- Attach all selected PDFs
- Open Outlook

Simply review and click **Send**.

---

# 💻 User Workflow

```
Generate Billable Report
          │
          ▼
Upload OpenAir PDFs
          │
          ▼
Open Outlook Draft
          │
          ▼
Desktop Helper
          │
          ▼
Outlook Draft Opens
          │
          ▼
Review & Send
```

---

# 🔒 Security

This project does **not** use:

- SMTP
- Microsoft Graph API
- Azure App Registration
- Outlook Passwords
- OAuth Tokens

All Outlook interactions happen locally using the authenticated Outlook Desktop application.

---

# 📌 System Requirements

- Windows 10 / Windows 11
- Microsoft Outlook Desktop
- Desktop Helper Installed

---

# 📸 Screenshots

> Add screenshots here.

Suggested screenshots:

- Dashboard
- Holiday Calendar
- Leave Tracking
- Billable Report
- PDF Upload
- Outlook Draft

---

# 🛣 Roadmap

## Version 1.0

- ✅ Billable Report Generation
- ✅ Outlook Draft Creation
- ✅ Automatic PDF Attachments
- ✅ Desktop Helper
- ✅ Health Check

## Version 1.1

- Auto-start Desktop Helper
- Better Installer
- Remember User Preferences
- Improved UI

## Version 2.0

- OpenAir Integration
- Automatic Timesheet Download
- Auto Updates
- Outlook Signature Detection

---

# 👨‍💻 Author

**Santhosh Kumar Manapuram**

Senior Software Engineer

Built to simplify monthly billing activities and eliminate repetitive manual work.

---

# 📄 License

This project is intended for internal organizational use.

Copyright © 2026 Santhosh Kumar Manapuram
