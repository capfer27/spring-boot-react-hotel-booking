import { HTTP_METHODS } from "./apiConfig";

const BASE_AUTH = '/auth';
const OAUTH_REGISTER = `${BASE_AUTH}/register`;
const OAUTH_LOGIN = `${BASE_AUTH}/login`;

export const OAUTH_ENDPOINTS = {
    REGISTER: { url: OAUTH_REGISTER, method: HTTP_METHODS.POST },
    LOGIN: { url: OAUTH_LOGIN, method: HTTP_METHODS.POST }
}