/* =========================================
   mail.js  –  HTML body generation & preview
========================================= */

/**
 * Builds the styled HTML email body from the current billable table.
 * Mirrors the exact output of the existing sendEmail() inline function
 * so the backend sends the same content the user sees on screen.
 *
 * Depends on globals defined in the inline <script> block:
 *   - monthName   (string)
 *   - weeks       (array)
 *
 * @returns {string}  Complete HTML string ready for email.
 */
function generateMailBodyHtml() {

  const originalTable = document.getElementById('billableTable');
  const clonedTable   = originalTable.cloneNode(true);

  clonedTable.style.width           = '650px';
  clonedTable.style.borderCollapse  = 'collapse';
  clonedTable.style.tableLayout     = 'fixed';
  clonedTable.style.fontFamily      = 'Calibri';

  clonedTable.querySelectorAll('tr').forEach(row => {
    row.querySelectorAll('th, td').forEach((cell, index) => {
      cell.style.border   = '1px solid black';
      cell.style.padding  = '4px';
      cell.style.fontSize = '12px';

      if (index === 0) cell.style.width = '40px';
      if (index === 1) cell.style.width = '320px';
      if (index === 2) cell.style.width = '120px';
      if (index === 3) cell.style.width = '90px';
    });
  });

  return `
    <div style="font-family:Calibri; font-size:14px; color:#000;">

      <p>Hi Ravi,</p>

      <p>Greetings of the day.</p>

      <p>
        Please look into the billing details
        for the month of ${monthName}
        and attached the timesheet pdf.
      </p>

      ${clonedTable.outerHTML}

      <br>

      <p>Thanks,</p>

    </div>
  `;
}


/**
 * Copies the email HTML body to the clipboard (same as the old "Copy Mail Body" button).
 * Renamed from sendEmail() to previewMailBody() to match the updated UI label.
 */
async function previewMailBody() {

  try {

    const emailHTML = generateMailBodyHtml();

    const blob = new Blob([emailHTML], { type: 'text/html' });
    await navigator.clipboard.write([
      new ClipboardItem({ 'text/html': blob })
    ]);

    alert(
`Mail body copied successfully!\n\nOpen your reply mail and simply paste it.`
    );

  } catch (err) {
    alert('Unable to copy to clipboard. Please check browser permissions.');
  }
}
