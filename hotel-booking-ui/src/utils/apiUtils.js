/**
 * Checks if a response data object or array is considered "empty".
 * Handles null, undefined, empty strings, empty arrays, and empty objects.
 */
export const isEmptyResponse = (data) => {
  if (data === null || data === undefined) return true;

  // Check for empty strings or arrays
  if (typeof data === 'string' || Array.isArray(data)) {
    return data.length === 0;
  }

  // Check for empty objects
  if (typeof data === 'object') {
    return Object.keys(data).length === 0;
  }

  return false;
};
