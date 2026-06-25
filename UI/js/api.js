/* =========================================
   api.js  –  Send Mail via Spring Boot backend
========================================= */

const API_BASE_URL = 'http://localhost:8080';


/**
 * Orchestrates the full "Send Mail" workflow:
 *   1. Validate form inputs
 *   2. Build multipart/form-data payload
 *   3. POST to /api/mail/send
 *   4. Show result toast
 */
async function sendMail() {

  /* ---- 1. Validate ---- */

  const errors = validateMailForm();

  if (errors.length > 0) {
    showValidationErrors(errors);

    // Scroll to the Outlook Configuration section
    const outlookSection = document.getElementById('outlookContent');
    if (outlookSection && outlookSection.style.display === 'none') {
      toggleSection('outlookContent', 'outlookArrow');
    }
    return;
  }

  showValidationErrors([]);

  /* ---- 2. Build payload ---- */

  const email    = document.getElementById('outlookEmail').value.trim();
  const password = document.getElementById('outlookPassword').value;
  const to       = document.getElementById('recipientEmail').value.trim();
  const cc       = document.getElementById('ccEmail').value.trim();
  const subject  = document.getElementById('mailSubject').value.trim();
  const htmlBody = generateMailBodyHtml();

  const formData = new FormData();
  formData.append('email',    email);
  formData.append('password', password);
  formData.append('to',       to);
  formData.append('subject',  subject);
  formData.append('htmlBody', htmlBody);

  if (cc) {
    formData.append('cc', cc);
  }

  getUploadedFiles().forEach(file => {
    formData.append('attachments', file, file.name);
  });

  /* ---- 3. POST to backend ---- */

  setLoadingState(true);

  try {

    const response = await fetch(`${API_BASE_URL}/api/mail/send`, {
      method: 'POST',
      body:   formData
      // Do NOT set Content-Type manually; browser sets it with boundary for multipart
    });

    let result = {};
    try {
      result = await response.json();
    } catch (_) {
      // Non-JSON error body (e.g. 500 HTML page)
    }

    if (response.ok) {
      showToast('Email sent successfully.', 'success');
    } else {
      const msg = result.message
        || 'Unable to send email. Please verify your Outlook credentials.';
      showToast(msg, 'error');
    }

  } catch (networkErr) {
    showToast(
      'Unable to reach the mail service. Please ensure the backend is running on localhost:8080.',
      'error'
    );
  } finally {
    setLoadingState(false);
  }
}
