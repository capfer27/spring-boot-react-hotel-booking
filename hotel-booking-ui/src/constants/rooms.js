import  { HTTP_METHODS } from "./apiConfig";

const ROOMS_BASE = 'api/rooms';

export const ROOMS_ENDPOINTS = {
  GET_ALL: { url: `${ROOMS_BASE}/all`, method: HTTP_METHODS.GET },
  CREATE: { url: `/${ROOMS_BASE}/add`, method: HTTP_METHODS.POST },
  DETAILS: (roomId) => ({ url: `${ROOMS_BASE}/${roomId}`, method: HTTP_METHODS.GET }),
  UPDATE: { url: `${ROOMS_BASE}/update`, method: HTTP_METHODS.PUT },
  DELETE: (roomId) => ({url: `${BASE_URL}/delete/${roomId})}`, method: HTTP_METHODS.DELETE }),
  
 // These are used ONLY by the interceptor for pattern matching
  DELETE_PATTERN:  `${ROOMS_BASE}/delete/:id`,
  SEARCH_PATTERN: `${ROOMS_BASE}/search/:term`,

  GET_AVAILABLE: {url: `${ROOMS_BASE}/available`, method: HTTP_METHODS.GET},
  GET_TYPES: {url: `${ROOMS_BASE}/types`, method: HTTP_METHODS.GET},
  SEARCH: (searchTerm) => ({url: `${ROOMS_BASE}/search/${searchTerm}`, method: HTTP_METHODS.GET}),
};