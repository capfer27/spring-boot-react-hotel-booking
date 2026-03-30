import { HTTP_METHODS } from "./apiConfig";

const USERS_BASE = 'api/users';
const USERS_ALL = `${USERS_BASE}/all`;
const USERS_ACCOUNT = `${USERS_BASE}/account`;
const USERS_UPDATE = `${USERS_BASE}/update`;
const USERS_DELETE = `${USERS_BASE}/delete`;
const USERS_BOOKINGS = `${USERS_BASE}/bookings`;

export const USERS_ENDPOINTS = {
  GET_ALL: { url: `${USERS_ALL}`, method: HTTP_METHODS.GET },
  PROFILE: {url: `${USERS_ACCOUNT}`, method: HTTP_METHODS.GET},
  DELETE: { url: `${USERS_DELETE}`, method: HTTP_METHODS.DELETE },
  UPDATE: { url: `${USERS_UPDATE}`, method: HTTP_METHODS.PUT },
  GET_BOOKINGS: {url: `${USERS_BOOKINGS}`, method: HTTP_METHODS.GET},
};