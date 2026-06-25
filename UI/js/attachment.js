/* =========================================
   attachment.js  –  PDF Drag-Drop Upload
========================================= */

const MAX_FILES = 20;

/** @type {File[]} */
let uploadedFiles = [];


/* ---- Initialise ---- */

document.addEventListener('DOMContentLoaded', function () {

  const dropZone  = document.getElementById('dropZone');
  const fileInput = document.getElementById('pdfFileInput');

  if (!dropZone || !fileInput) return;

  dropZone.addEventListener('click',     ()  => fileInput.click());
  dropZone.addEventListener('dragover',  handleDragOver);
  dropZone.addEventListener('dragleave', handleDragLeave);
  dropZone.addEventListener('drop',      handleDrop);
  fileInput.addEventListener('change',   handleFileSelect);
});


/* ---- Drag event handlers ---- */

function handleDragOver(e) {
  e.preventDefault();
  document.getElementById('dropZone').classList.add('drag-over');
}

function handleDragLeave() {
  document.getElementById('dropZone').classList.remove('drag-over');
}

function handleDrop(e) {
  e.preventDefault();
  document.getElementById('dropZone').classList.remove('drag-over');

  const pdfs = Array.from(e.dataTransfer.files)
    .filter(f => f.type === 'application/pdf' || f.name.toLowerCase().endsWith('.pdf'));

  addFiles(pdfs);
}

function handleFileSelect(e) {
  addFiles(Array.from(e.target.files));
  e.target.value = ''; // allow re-selecting same file
}


/* ---- File management ---- */

function addFiles(files) {

  const available = MAX_FILES - uploadedFiles.length;
  const toAdd     = files.slice(0, available);

  toAdd.forEach(file => {
    // Prevent duplicate filenames
    if (!uploadedFiles.find(f => f.name === file.name)) {
      uploadedFiles.push(file);
    }
  });

  if (files.length > available) {
    showToast(
      `Maximum ${MAX_FILES} files allowed. Only the first ${available} file(s) were added.`,
      'error'
    );
  }

  renderFileList();
}

/**
 * Removes an uploaded file by index and re-renders the list.
 * Called from inline onclick in the rendered HTML.
 * @param {number} index
 */
function removeFile(index) {
  uploadedFiles.splice(index, 1);
  renderFileList();
}

/**
 * Returns the current list of uploaded PDF File objects.
 * @returns {File[]}
 */
function getUploadedFiles() {
  return uploadedFiles;
}


/* ---- Render ---- */

function renderFileList() {

  const container = document.getElementById('fileList');
  const statsEl   = document.getElementById('fileStats');
  const countEl   = document.getElementById('fileCount');
  const sizeEl    = document.getElementById('totalSize');

  container.innerHTML = '';

  uploadedFiles.forEach((file, index) => {

    const item = document.createElement('div');
    item.className = 'file-item d-flex align-items-center justify-content-between';

    item.innerHTML = `
      <div class="d-flex align-items-center gap-2">
        <span style="font-size:18px; color:#0d6efd;">&#128196;</span>
        <span class="file-name">${escapeHtml(file.name)}</span>
        <span class="file-size">(${formatFileSize(file.size)})</span>
      </div>
      <button type="button"
              class="btn btn-sm btn-outline-danger"
              onclick="removeFile(${index})"
              title="Remove file">
        &times;
      </button>
    `;

    container.appendChild(item);
  });

  if (uploadedFiles.length > 0) {
    statsEl.classList.remove('hidden');
    countEl.textContent = uploadedFiles.length;
    const totalBytes    = uploadedFiles.reduce((sum, f) => sum + f.size, 0);
    sizeEl.textContent  = formatFileSize(totalBytes);
  } else {
    statsEl.classList.add('hidden');
  }

  updateTimesheetSummary();
}

function updateTimesheetSummary() {
  const summary = document.getElementById('timesheetSummary');
  if (!summary) return;

  if (uploadedFiles.length === 0) {
    summary.innerHTML = '';
    summary.classList.add('hidden');
    return;
  }

  const list = uploadedFiles
    .map(file => `&#10003; ${escapeHtml(file.name)}`)
    .join('<br>');

  summary.innerHTML = list;
  summary.classList.remove('hidden');
}


/* ---- Utilities ---- */

function formatFileSize(bytes) {
  if (bytes < 1024)             return bytes + ' B';
  if (bytes < 1024 * 1024)      return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.appendChild(document.createTextNode(str));
  return div.innerHTML;
}
