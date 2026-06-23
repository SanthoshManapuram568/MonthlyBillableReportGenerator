# Monthly Billable Report Generator

An internal web-based utility for generating monthly billable reports, calculating billable hours, tracking holidays, leaves, and extra hours, and preparing formatted reports for billing submissions.

---

## Features

### Holiday Management
- Interactive holiday calendar.
- Weekends automatically excluded.
- Multiple holiday selection support.
- Automatic holiday summary generation.
- Billable days adjusted based on selected holidays.

### Leave & Extra Hours Tracking
- Weekly leave tracking.
- Weekly extra hours tracking.
- Automatic billable hours recalculation.
- Leave and extra hours summary generation.

### Monthly Billable Report Generation
Automatically calculates:

- Total Working Days
- Total Days Worked
- Total Leave Days
- Extra Hours Worked
- Weekly Billable Hours
- Total Monthly Billable Hours

### Copy Table Feature
- Generates a compact report table.
- Optimized for Outlook pasting as the exact table.
- Preserves formatting and borders.

### Copy Mail Body Feature
- Generates a ready-to-send email body.
- Includes monthly billing summary.
- Includes formatted report table.
- Compatible with Outlook.

### Refresh Protection
- Warns users before refreshing or closing the page.
- Prevents accidental loss of entered data.

---

# Technology Stack

| Technology | Purpose |
|------------|----------|
| HTML5 | UI Structure |
| CSS3 | Styling |
| Bootstrap 5.3 | Responsive Layout |
| JavaScript (Vanilla) | Business Logic |

---

# Getting Started

## Option 1: Web

1. Open the website : https://srmbillablereportgenerator.netlify.app/
2. Any modern browser is compatible.

## Option 2: Run Directly

1. Download the project
2. Open `index.html` in any modern browser:
   - Chrome
   - Edge
   - Firefox

No installation required.

---

## Option 3: Run via VS Code

### Prerequisites

- Visual Studio Code
- Live Server Extension

### Steps

1. Open project folder in VS Code.
2. Right-click `index.html`.
3. Select:

```text
Open with Live Server
```

4. Application launches automatically.

---

# How To Use

## Step 1: Select Holidays

Expand the **Holidays** section.

Enable:

```text
Are there any holidays this month?
```

### Actions

- Click holiday dates from the calendar.
- Selected holidays will be highlighted.
- Holiday summary updates automatically.

---

## Step 2: Enter Leaves & Extra Hours

Expand:

```text
Leaves & Extra Hours
```

Enable:

```text
Did you apply any leaves?
```

For each week:

- Enter Leave Days
- Enter Extra Hours

### Example

| Week | Leaves | Extra Hours |
|--------|---------|------------|
| Week 1 | 1 | 2 |
| Week 2 | 0 | 4 |
| Week 3 | 2 | 0 |

---

## Step 3: Review Billable Report

The table is automatically generated and updated whenever:

- Holidays are selected
- Leave days are modified
- Extra hours are entered

---

## Step 4: Copy Report Table

Click:

```text
Copy Table
```

Then:

1. Open Outlook and paste the generated  table.
2. Paste using:

```text
Ctrl + V
```

The formatted billable report table will be pasted.

---

## Step 5: Copy Email Body

Click:

```text
Copy Mail Body
```

Then:

1. Open Outlook.
2. Create a new email or reply.
3. Paste using:

```text
Ctrl + V
```

A fully formatted billing email will be generated.

---

# Billable Hours Calculation

## Weekly Formula

```text
Billable Hours =
((Working Days - Holidays - Leaves) × 8)
+ Extra Hours
```

### Constants

```text
1 Working Day = 8 Hours
```

---

## Example Calculation

### Week Details

```text
Working Days = 5
Holidays = 1
Leaves = 1
Extra Hours = 2
```

### Calculation

```text
((5 - 1 - 1) × 8) + 2
= 26 Hours
```

---

# Generated Output

The report contains:

1. Total Number of Working Days
2. Number of Days Worked
3. Number of Leave Days
4. Extra Hours Charged
5. Work Location
6. Weekly Billable Hours
7. Total Monthly Billable Hours
8. OpenAir PDF Attachment Reminder (User needs to add the Timesheet PDF's explicitly)

---

# Screens Included

- Holiday Calendar
- Weekly Leave Input
- Extra Hours Input
- Billable Report Table
- Email Body Generation

---

# Author

**Santhosh Kumar Manapuram**

Senior Software Engineer

Internal SRM Utility for Monthly Billable Report Generation.

---

# License

This project is intended for internal organizational use.

Copyright © Santhosh Kumar Manapuram
