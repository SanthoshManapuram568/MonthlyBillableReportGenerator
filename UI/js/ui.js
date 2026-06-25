/* =========================================
   ui.js  –  Toast, Spinner, Progress Modal
========================================= */

let progressModalInstance = null;
const LS_RECIPIENT_KEY = 'mbrg_recipient_email';
const LS_CC_KEY = 'mbrg_cc_email';

/**
 * Displays a Bootstrap toast notification.
 * @param {string} message  Body text to display.
 * @param {'success'|'error'|'info'} type  Visual style.
 */
function showToast(message, type) {

  const toastEl     = document.getElementById('notificationToast');
  const toastHeader = document.getElementById('toastHeader');
  const toastTitle  = document.getElementById('toastTitle');
  const toastBody   = document.getElementById('toastMessage');
  const toastClose  = document.getElementById('toastClose');

  toastBody.textContent = message;

  // Reset header classes
  toastHeader.className = 'toast-header';
  toastClose.className  = 'btn-close';

  if (type === 'success') {
    toastHeader.classList.add('bg-success', 'text-white');
    toastClose.classList.add('btn-close-white');
    toastTitle.textContent = '✓ Success';
  } else if (type === 'info') {
    toastHeader.classList.add('bg-info', 'text-white');
    toastClose.classList.add('btn-close-white');
    toastTitle.textContent = 'ℹ Info';
  } else {
    toastHeader.classList.add('bg-danger', 'text-white');
    toastClose.classList.add('btn-close-white');
    toastTitle.textContent = '✕ Error';
  }

  const toast = new bootstrap.Toast(toastEl, { delay: 6000 });
  toast.show();
}


/**
 * Shows the progress modal and marks a step as active.
 * @param {number} stepNumber  1-4
 */
function showProgressModal(stepNumber) {
  const modalEl = document.getElementById('progressModal');
  if (!modalEl) return;

  progressModalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
  progressModalInstance.show();
  updateProgressStep(stepNumber);
}

/**
 * Hides the progress modal.
 */
function hideProgressModal() {
  const modalEl = document.getElementById('progressModal');
  if (!modalEl) return;

  const modal = progressModalInstance || bootstrap.Modal.getInstance(modalEl);

  if (modal) {
    modal.hide();
  }

  // Fallback cleanup in case Bootstrap state gets out of sync on rapid errors.
  modalEl.classList.remove('show');
  modalEl.style.display = 'none';
  modalEl.setAttribute('aria-hidden', 'true');
  document.body.classList.remove('modal-open');
  document.body.style.removeProperty('padding-right');

  var backdrops = document.querySelectorAll('.modal-backdrop');
  for (var i = 0; i < backdrops.length; i++) {
    backdrops[i].remove();
  }
}

/**
 * Updates the progress indicator for a given step.
 * Marks previous steps as completed, current as active.
 * @param {number} stepNumber  1-4
 */
function updateProgressStep(stepNumber) {
  for (let i = 1; i <= 4; i++) {
    const step = document.getElementById(`step${i}`);
    const indicator = step.querySelector('.step-indicator');

    if (i < stepNumber) {
      step.classList.remove('active');
      step.classList.add('completed');
      indicator.textContent = '✓';
    } else if (i === stepNumber) {
      step.classList.remove('completed');
      step.classList.add('active');
      indicator.textContent = '⏳';
    } else {
      step.classList.remove('completed', 'active');
      indicator.textContent = '⏳';
    }
  }
}


/**
 * Switches the button between loading and normal state.
 * @param {boolean} loading
 */
function setButtonLoading(loading) {

  const btn     = document.getElementById('openDraftBtn');
  const spinner = document.getElementById('draftSpinner');
  const label   = btn.querySelector('.btn-text');

  if (loading) {
    btn.disabled          = true;
    spinner.style.display = 'inline-block';
    label.textContent     = 'Opening...';
  } else {
    btn.disabled          = false;
    spinner.style.display = 'none';
    label.textContent     = 'Open Outlook Draft';
  }
}


/**
 * Displays or hides the helper connection status messages.
 * @param {'connected'|'checking'|'not-running'} status
 */
function updateHelperStatus(status) {

  const helperStatus = document.getElementById('helperStatus');
  const helperNotRunning = document.getElementById('helperNotRunning');

  // Hide all first
  helperStatus.style.display = 'none';
  helperNotRunning.style.display = 'none';

  if (status === 'checking') {
    helperStatus.style.display = 'block';
  } else if (status === 'not-running') {
    helperNotRunning.style.display = 'block';
  }
  // 'connected' = hide all messages
}


/**
 * Populates the mail subject field with the current month/year on page load.
 * Relies on globals `monthName` and `currentYear` from the inline script.
 */
document.addEventListener('DOMContentLoaded', function () {

  const subjectEl = document.getElementById('mailSubject');
  const recipientEl = document.getElementById('recipientEmail');
  const ccEl = document.getElementById('ccEmail');

  if (
    subjectEl &&
    typeof monthName  !== 'undefined' &&
    typeof currentYear !== 'undefined'
  ) {
    subjectEl.value = `Monthly Billable Report - ${monthName} ${currentYear}`;
  }

  // Restore previously saved recipient/cc values.
  if (recipientEl) {
    recipientEl.value = localStorage.getItem(LS_RECIPIENT_KEY) || '';
    recipientEl.addEventListener('blur', function() {
      saveEmailPreferences();
      updateEmailSummary();
    });
  }

  if (ccEl) {
    ccEl.value = localStorage.getItem(LS_CC_KEY) || '';
    ccEl.addEventListener('blur', function() {
      saveEmailPreferences();
      updateEmailSummary();
    });
  }

  if (subjectEl) {
    subjectEl.addEventListener('blur', updateEmailSummary);
  }

  // Populate summary from any restored values.
  updateEmailSummary();

  // Check helper health on page load
  checkHelperHealth();
});

function saveEmailPreferences() {
  const recipientEl = document.getElementById('recipientEmail');
  const ccEl = document.getElementById('ccEmail');

  if (recipientEl) {
    localStorage.setItem(LS_RECIPIENT_KEY, recipientEl.value.trim());
  }

  if (ccEl) {
    localStorage.setItem(LS_CC_KEY, ccEl.value.trim());
  }
}

function updateEmailSummary() {
  const summary     = document.getElementById('emailSummary');
  if (!summary) return;

  const to      = (document.getElementById('recipientEmail')?.value || '').trim();
  const cc      = (document.getElementById('ccEmail')?.value        || '').trim();
  const subject = (document.getElementById('mailSubject')?.value    || '').trim();

  if (!to && !subject) {
    summary.innerHTML = '';
    summary.classList.add('hidden');
    return;
  }

  const lines = [];
  if (to)      lines.push('<b>To:</b> '      + escapeHtml(to));
  if (cc)      lines.push('<b>CC:</b> '      + escapeHtml(cc));

  summary.innerHTML = lines.join('&nbsp;&nbsp;|&nbsp;&nbsp;');
  summary.classList.remove('hidden');
}

// escapeHtml helper may already exist in attachment.js (shared scope),
// but define a local guard here so ui.js works standalone.
if (typeof escapeHtml === 'undefined') {
  function escapeHtml(str) {
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(str));
    return d.innerHTML;
  }
}


/**
 * Scrolls an element into view with smooth behavior.
 * @param {string} elementId
 */
function scrollToElement(elementId) {
  const el = document.getElementById(elementId);
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }
}

