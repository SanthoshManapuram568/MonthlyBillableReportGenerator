/* =========================================
   validation.js  –  Front-end form validation
========================================= */

/**
 * Validates all fields required to open Outlook draft.
 * @returns {{errors:string[], warning:string|null}}
 */
function validateOutlookForm() {

  const errors = [];

  const to       = (document.getElementById('recipientEmail')?.value  || '').trim();
  const subject  = (document.getElementById('mailSubject')?.value     || '').trim();
  const files    = getUploadedFiles();

  if (!to || !isValidEmailList(to)) {
    errors.push('Please enter valid recipient email address(es). Use comma-separated values for multiple recipients.');
  }

  const cc = (document.getElementById('ccEmail')?.value || '').trim();
  if (cc && !isValidEmailList(cc)) {
    errors.push('Please enter valid CC email address(es), comma-separated, or leave the CC field empty.');
  }

  if (!subject) {
    errors.push('Please enter a subject.');
  }

  if (files.length === 0) {
    errors.push('Please upload at least one PDF timesheet before opening the draft.');
  }

  // Check for missing week files
  const missingWeeks = checkMissingWeeks(files);
  if (missingWeeks.length > 0) {
    const warning = `Missing files: ${missingWeeks.join(', ')}. Continue anyway?`;
    // This will be handled by caller if they want to show a confirm dialog
    return { errors, warning };
  }

  return { errors, warning: null };
}


/**
 * Detects which expected week files are missing based on current month.
 * @param {File[]} files
 * @returns {string[]}  List of missing week files
 */
function checkMissingWeeks(files) {

  if (!files || files.length === 0) return [];

  // Get current month abbreviation
  const currentDate = new Date();
  const monthAbbr = currentDate.toLocaleString('default', { month: 'short' }).toUpperCase();

  // Expected weeks (most months have 4-5 weeks)
  const expectedWeeks = ['Week1', 'Week2', 'Week3', 'Week4', 'Week5'];

  // Get uploaded filenames
  const uploadedFileNames = files.map(f => f.name.toUpperCase());

  // Check which weeks are missing
  const missing = [];
  for (const week of expectedWeeks) {
    const expectedName = `${monthAbbr}-${week}.PDF`;
    if (!uploadedFileNames.some(name => name.includes(expectedName))) {
      missing.push(`${monthAbbr}-${week}.pdf`);
    }
  }

  return missing;
}


/**
 * Flashes relevant section boxes red for 3 seconds with an error note.
 * Pass an empty array to clear all existing notes.
 * @param {string[]} errors
 */
function showValidationErrors(errors) {

  // Clear any existing notes first
  document.querySelectorAll('.section-error-note').forEach(function(el) {
    el.remove();
  });
  document.querySelectorAll('.section-error').forEach(function(el) {
    el.classList.remove('section-error');
  });

  if (!errors || errors.length === 0) {
    return;
  }

  // Separate errors by which section they belong to
  var timesheetErrors = [];
  var emailErrors     = [];

  errors.forEach(function(msg) {
    var lower = msg.toLowerCase();
    if (lower.includes('pdf') || lower.includes('timesheet') || lower.includes('attachment')) {
      timesheetErrors.push(msg);
    } else {
      emailErrors.push(msg);
    }
  });

  if (timesheetErrors.length > 0) {
    flashSection('timesheetSection', 'timesheetArrow', timesheetErrors);
  }

  if (emailErrors.length > 0) {
    flashSection('emailSection', 'emailArrow', emailErrors);
  }
}

/**
 * Applies a red border to a section box, ensures it is collapsed,
 * inserts an error note above the section header, and auto-clears after 3s.
 */
function flashSection(sectionId, arrowId, errorMessages) {

  var sectionBox = document.getElementById(sectionId);
  if (!sectionBox) return;

  // Ensure section is collapsed so the red border is visible
  var content = sectionBox.querySelector('.section-content');
  var arrow   = document.getElementById(arrowId);
  if (content && content.style.display !== 'none') {
    content.style.display = 'none';
    if (arrow) arrow.classList.add('collapsed-arrow');
  }

  // Add red border
  sectionBox.classList.add('section-error');

  // Insert error note just inside the top of the section box
  var note = document.createElement('div');
  note.className = 'section-error-note';
  note.innerHTML = '&#9888; ' + errorMessages.map(function(e) {
    return escapeHtml(e);
  }).join(' &bull; ');
  sectionBox.insertBefore(note, sectionBox.firstChild);

  // Scroll section into view
  sectionBox.scrollIntoView({ behavior: 'smooth', block: 'nearest' });

  // Auto-clear after 3 seconds
  setTimeout(function() {
    sectionBox.classList.remove('section-error');
    if (note.parentNode) {
      note.parentNode.removeChild(note);
    }
  }, 3000);
}


/* ---- Helpers ---- */

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidEmailList(value) {
  const emails = value
    .split(',')
    .map(item => item.trim())
    .filter(Boolean);

  if (emails.length === 0) {
    return false;
  }

  return emails.every(isValidEmail);
}

