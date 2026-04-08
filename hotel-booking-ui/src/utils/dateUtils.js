/**
 * Converts "M/D/YYYY" to "YYYY-MM-DD" or "YYYY/MM/DD"
 * @param {string} dateStr - Input date like "4/8/2026"
 * @param {string} separator - "/" or "-" (default is "-")
 * @returns {string} Formatted date
 */
export const formatToBackendDate = (dateStr, separator = '-') => {
  if (!dateStr) return '';

  // Split the string by the slash
  const [month, day, year] = dateStr.split('/');

  // Use padStart to ensure 2 digits (e.g., "4" -> "04")
  const mm = month.padStart(2, '0');
  const dd = day.padStart(2, '0');

  // Return in desired order
  return `${year}${separator}${mm}${separator}${dd}`;
};
