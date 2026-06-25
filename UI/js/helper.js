/* =========================================
   helper.js  –  Desktop Helper Health Check
========================================= */

const HELPER_URL = 'http://localhost:8085';
let helperConnected = false;

/**
 * Checks if the desktop helper is running by calling GET /health
 */
async function checkHelperHealth() {

  updateHelperStatus('checking');

  try {

    const response = await fetch(`${HELPER_URL}/health`, {
      method: 'GET',
      mode: 'cors'
    });

    if (response.ok) {
      const data = await response.json();
      helperConnected = data.status === 'UP';

      if (helperConnected) {
        updateHelperStatus('connected');
      } else {
        updateHelperStatus('not-running');
      }
    } else {
      updateHelperStatus('not-running');
    }

  } catch (err) {
    // Helper not running or network error (ERR_CONNECTION_REFUSED etc.)
    updateHelperStatus('not-running');
  }
}

/**
 * Returns whether the helper is currently connected.
 * @returns {boolean}
 */
function isHelperConnected() {
  return helperConnected;
}
