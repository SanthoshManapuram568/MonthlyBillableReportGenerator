/* =========================================
   outlook.js  –  Open Outlook Draft via Helper
========================================= */

/**
 * Main workflow: Validate → Show Progress → Post to Helper → Handle Result
 */
async function openOutlookDraft() {

  hideProgressModal();

  // 1. Check if helper is running
  if (!isHelperConnected()) {
    showToast('Desktop Helper is not running. Please start Billable Report Helper.', 'error');
    return;
  }

  // 2. Validate form
  const validation = validateOutlookForm();
  const { errors, warning } = validation;

  if (typeof saveEmailPreferences === 'function') {
    saveEmailPreferences();
  }

  if (typeof updateEmailSummary === 'function') {
    updateEmailSummary();
  }

  if (errors && errors.length > 0) {
    showValidationErrors(errors);
    return;
  }

  // 3. Check for missing weeks (show warning but allow continue)
  if (warning) {
    hideProgressModal();
    const confirmContinue = confirm(warning);
    if (!confirmContinue) {
      return;
    }
  }

  showValidationErrors([]); // Clear any previous errors

  // 4. Show progress modal and disable button
  showProgressModal(1);
  setButtonLoading(true);

  try {

    // 5. Build the payload
    updateProgressStep(1);
    const to      = document.getElementById('recipientEmail').value.trim().replace(/\s*,\s*/g, '; ');
    const cc      = document.getElementById('ccEmail').value.trim().replace(/\s*,\s*/g, '; ');
    const subject = document.getElementById('mailSubject').value.trim();
    const html    = generateMailBodyHtml();

    const formData = new FormData();
    formData.append('recipient', to);
    formData.append('subject',   subject);
    formData.append('htmlBody',  html);

    if (cc) {
      formData.append('cc', cc);
    }

    // 6. Add attachments
    updateProgressStep(2);
    getUploadedFiles().forEach(file => {
      formData.append('attachments', file, file.name);
    });

    // 7. POST to helper
    updateProgressStep(3);
    const response = await fetch(`${HELPER_URL}/api/outlook/draft`, {
      method: 'POST',
      body:   formData
      // Do NOT set Content-Type header; browser will set it with boundary
    });

    let result = {};
    try {
      result = await response.json();
    } catch (_) {
      // Non-JSON response
    }

    // 8. Handle response
    updateProgressStep(4);

    if (response.ok) {
      hideProgressModal();
      showToast('Outlook Draft created successfully. Please review the email and click Send.', 'success');
    } else {
      hideProgressModal();
      const msg = result.message || result.error || 
        'Unable to create Outlook draft. Please check the desktop helper.';
      showToast(msg, 'error');
    }

  } catch (networkErr) {
    hideProgressModal();
    showToast(
      'Unable to reach the desktop helper on localhost:8085. Please ensure Billable Report Helper is running.',
      'error'
    );
  } finally {
    setButtonLoading(false);
  }
}
