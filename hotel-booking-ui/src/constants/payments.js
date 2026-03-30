import { HTTP_METHODS } from "./apiConfig";

const PAYMENTS_BASE = '/payments';

export const PAYMENTS_ENDPOINTS = {
  CREATE: { url: `${PAYMENTS_BASE}/pay`, method: HTTP_METHODS.POST },
  UPDATE: { url: `${PAYMENTS_BASE}/update`, method: HTTP_METHODS.PUT },
};