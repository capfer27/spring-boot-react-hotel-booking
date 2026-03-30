import { HTTP_METHODS } from "./apiConfig";

const BOOKINGS_BASE = 'api/bookings';
const BOOKINGS_ALL = `${BOOKINGS_BASE}/all`
const BOOKINGS_CREATE = `/${BOOKINGS_BASE}/create`;
const BOOKINGS_REFERENCE = `${BOOKINGS_BASE}`
const BOOKINGS_UPDATE = `${BOOKINGS_BASE}/update`


export const BOOKING_ENDPOINTS = {
  GET_ALL: { url: `${BOOKINGS_ALL}`, method: HTTP_METHODS.GET },
  CREATE: { url: `${BOOKINGS_CREATE}`, method: HTTP_METHODS.POST },
  DETAILS: (referenceCode) => ({ url: `${BOOKINGS_REFERENCE}/${referenceCode}`, method: HTTP_METHODS.GET }),
  UPDATE: { url: `${BOOKINGS_UPDATE}`, method: HTTP_METHODS.PUT },
};