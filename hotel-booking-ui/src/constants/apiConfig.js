// Useful if you switch between dev and production
export const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:9090';
export const BASE_HEADERS = { 'Content-Type': 'application/json' };
export const FORM_HEADERS =  {'Content-Type': 'multipart/form-data'};

export const HTTP_METHODS = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  DELETE: 'DELETE',
};
