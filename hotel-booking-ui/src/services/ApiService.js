import axios from "axios";
import CryptoJS from "crypto-js";
import { BOOKING_ENDPOINTS } from "../constants/bookings";
import { ROOMS_ENDPOINTS } from "../constants/rooms";
import { USERS_ENDPOINTS } from "../constants/users";
import { OAUTH_ENDPOINTS } from "../constants/auth";
import { PAYMENTS_ENDPOINTS } from "../constants/payments";

/**
 * TODO: Refactor this code later after making things to work first.
 */
class ApiService {

    static ENCRYPTION_KEY = "capfer-secret-key";

    static TOKEN_KEY = "HOTEL_BOKING_TOKEN";

    static ROLE_KEY = "HOTEL_BOOKING_ROLE";

    // Define the private field with #
    // This cannot be accessed as apiService.#api from outside the class
    #api;

    constructor(baseURL) {
        // Create a reusable Axios instance
        api = axios.create({
            baseURL: baseURL,
            headers: {'Content-Type': 'application/json'}
        });

        // Interceptor to inject the token into every request
        // "set it and forget it," ensuring every call—even after 
        // a page refresh—automatically includes the token.
        // Add the dynamic interceptor
        this.#api.interceptors.request.use(
            (config) => {
                // Fetch dynamic data (e.g., from storage or a helper function)
                const token = ApiService.getToken();
                const language = localStorage.getItem('app_lang') || 'en';

                // Define public endpoints (that don't need a token)
                //const publicEndpoints = ['/login', '/register', '/'];
                
                const authEnpoints = [
                    USERS_ENDPOINTS.PROFILE.url,
                    USERS_ENDPOINTS.GET_BOOKINGS.url,
                    USERS_ENDPOINTS.DELETE.url,
                    ROOMS_ENDPOINTS.CREATE.url,
                    ROOMS_ENDPOINTS.UPDATE.url,
                    BOOKING_ENDPOINTS.CREATE.url,
                    BOOKING_ENDPOINTS.GET_ALL.url,
                    BOOKING_ENDPOINTS.UPDATE.url,
                    BOOKING_ENDPOINTS.DETAILS.url,
                    PAYMENTS_ENDPOINTS.CREATE.url,
                    PAYMENTS_ENDPOINTS.UPDATE.url
                ];

                const urlPatternMatch = [
                    ROOMS_ENDPOINTS.DELETE_PATTERN,
                    ROOMS_ENDPOINTS.SEARCH_PATTERN    
                ];

                // Check if the current request is public
                //const isPublic = publicEndpoints.some(endpoint => config.url.endsWith(endpoint));

                // Check if the current request is private
                const authRequired = authEnpoints.some(endpoint => config.url.endsWith(endpoint));
                const urlMatch = urlPatternMatch.some(endpoint => isUrlMatch(config.url, endpoint));

                // Inject headers dynamically
                if ( (authRequired && token) || (urlMatch && token) ) {
                    config.headers.Authorization = `Bearer ${token}`;
                }
                config.headers['Accept-Language'] = language;
                
                // Add custom dynamic timestamps headers
                config.headers['X-Request-Timestamp'] = Date.now();

                // Here we can set the X-Request-Idempotent-ID
                //config.headers['X-Request-Idempotent-ID'] = crypto.randomUUID;

                return config;
            },
            (error) => {
                return Promise.reject(error);
            }
        );
    }

    static encrypt(token) {
        return CryptoJS.AES.encrypt(token, this.ENCRYPTION_KEY.toString());
    }

    static decrypt(token) {
        const bytes = CryptoJS.AES.decrypt(token, this.ENCRYPTION_KEY);
        return bytes.toString(CryptoJS.enc.Utf8);
    }

    static saveToken(token) {
        const encryptedToken = this.encryptedToken(token);
        this.saveToLocalStorage(this.TOKEN_KEY, encryptedToken);
    }

    static getToken() {
        const token = this.getFromLocalStorage(this.TOKEN_KEY);
        if (!token?.trim()) {
            return null;
        }
        return this.decrypt(token);
    }

    static saveRole(role) {
        const encryptedRole = this.encrypt(role);
        this.saveToLocalStorage(this.ROLE_KEY, encryptedRole); 
    }

    static getRole() {
       const encryptedRole = this.getFromLocalStorage(this.ROLE_KEY);
       if (!encryptedRole?.trim()) {
           return null; 
       }
       return this.decrypt(encryptedRole);
    }

    static clearAuth() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.ROLE_KEY);
    }

    static getHeader() {
        const token = this.getToken();
        return {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    }

    static saveToLocalStorage(key, value) {
        localStorage.setItem(key, value);
    }

    static getFromLocalStorage(key) {
        return localStorage.getItem(key);
    }

    // Method to set the token globally for this instance
    // Set token after successfully registration
    setToken(token) {
        this.#api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }

    // Method to clear the token (for logout)
    clearToken() {
        delete this.#api.defaults.headers.common['Authorization'];
    }

    /**
     * AUTH AND USERS API METHODS
     */

    logout() {
        this.clearAuth();
        this.clearToken();
    }

    static isAuthenticated() {
        const token = this.getToken();
        return token !== null;
    }

    static isAdmin() {
        const role = this.getRole();
        return role === 'ADMIN';
    }

    static isAdmin() {
        const role = this.getRole();
        return role === 'CUSTOMER';
    }

    async registerUser(userData) {
        try {
            const response = await this.#api.post(OAUTH_ENDPOINTS.REGISTER.url, userData);
            return response.data;
        } catch (error) {
            // Extract the server's error message
            this.#handleError("Registration failed", error);
        }
    }

    async loginUser(userData) {
        try {
            const response = await this.#api.post(OAUTH_ENDPOINTS.LOGIN.url, userData);
            return response.data;
        } catch (error) {
            this.#handleError("Login failed", error);         
        }
    }

    async getUserProfile() {
        try {
            const response = await this.#api.get(USERS_ENDPOINTS.PROFILE.url);
            // , {headers: this.getHeader()}
            return response.data;
        } catch (error) {
            this.#handleError("Failed to retrieve the user profile", error);
        }
    }

     async getUserBookings() {
        try {
            const response = await this.#api.get(USERS_ENDPOINTS.GET_BOOKINGS.url);
            // { headers: this.getHeader()}
            return response.data;
        } catch (error) {
            this.#handleError("Failed to retrieve user bookings", error);
        }
    }

    async deleteAccount() {
        try {
            const response = await this.#api.delete(USERS_ENDPOINTS.DELETE.url);
            // , {   headers: this.getHeader()}
            return response.data;
        } catch (error) {
            this.#handleError("Failed to delete account", error);
        }
    }

    // Handle Rooms
    async addRoom(formData) {
        try {
            const response = this.#api.post(ROOMS_ENDPOINTS.CREATE.url, formData, {
                headers: {
                    //...this.getHeader(),
                    // Overrides for this call only
                    'Content-Type': 'multipart/form-data'
                }
            });
            return response.data
        } catch (error) {
            this.#handleError("Failed to add a room", error);
        }
    }

    async getRoomTypes() {
        try {
            const response = await this.#api.get(ROOMS_ENDPOINTS.GET_TYPES.url);
            return response.data
        } catch (error) {
            this.#handleError('Failed to get room types', error);
        }
    }

    async getAllRooms() {
        try {
            const response = await this.#api.get(ROOMS_ENDPOINTS.GET_ALL.url);
            return response.data
        } catch (error) {
            this.#handleError('Failed to retrieve all rooms', error);
        }
    }

    async getRoomById(roomId) {
        try {
            const response = await this.#api.get(ROOMS_ENDPOINTS.DETAILS(roomId).url);
            return response.data
        } catch (error) {
            this.#handleError('Failed to retrieve a specific room', error);
        }
    }

    async deleteRoom(roomId) {
        try {
            const response = await this.#api.delete(ROOMS_ENDPOINTS.DELETE(roomId).url);
            return response.data
        } catch (error) {
            this.#handleError('Failed to delete a room', error);
        }
    }

    async updateRoom(formData) {
        try {
            const response = this.#api.put(ROOMS_ENDPOINTS.UPDATE.url, formData, {
                headers: {
                   'Content-Type': 'multipart/form-data'
                }
            });
            return response.data
        } catch (error) {
            this.#handleError("Failed to update a room", error);
        }
    }

  /**
   * Get available rooms with dynamic filters
   * @param {Object} filters - { checkInDate, checkOutDate, roomType }
   * 
   * Example usage:
   *    const searchCriteria = {
   *         checkInDate: '2026-03-22',
   *         checkOutDate: '2026-04-05',
   *         roomType: 'SINGLE'
   *    };
   *    
   *    const rooms = await ApiService.getAvailableRooms(searchCriteria);
   */
    async getAvailableRooms(checkInDate, checkOutDate, roomType) {
       try {
            const response = await this.#api.get(ROOMS_ENDPOINTS.GET_AVAILABLE.url, {
                params: {
                    checkInDate,
                    checkOutDate,
                    roomType
                }
            });
            return response.data;
       } catch (error) {
            this.#handleError('Failed to retrieve available rooms', error);
       }     
    }

    // Handle Bookigns

    async getBookingByReference(reference) {
        try {
            const response = await this.#api.get(BOOKING_ENDPOINTS.DETAILS(reference).url)
            return response.data;
        } catch (error) {
            this.#handleError('Failed to get room by reference number', error);
        }
    }

    async bookRoom(bookingData) {
        try {
            const response = await this.#api.post(BOOKING_ENDPOINTS.CREATE.url, bookingData)
            return response.data;
        } catch (error) {
            this.#handleError('Failed to book a room', error);
        }
    }

    async getAllBookings() {
        try {
            const response = await this.#api.get(BOOKING_ENDPOINTS.GET_ALL.url)
            return response.data;
        } catch (error) {
            this.#handleError('Failed to retrieve all bookings', error);
        }
    }

    async updateBooking(bookingData) {
        try {
            const response = await this.#api.put(BOOKING_ENDPOINTS.UPDATE.url, bookingData)
            return response.data;
        } catch (error) {
            this.#handleError('Failed to update a booking', error);
        }
    }

    // handle payments

    async createPaymentIntent(data) {
        try {
            const response = await this.#api.post(PAYMENTS_ENDPOINTS.CREATE.url, data)
            return response.data;
        } catch (error) {
            this.#handleError('Failed to pay for a room', error);
        }
    }

    async updatePayment(data) {
        try {
            const response = await this.#api.post(PAYMENTS_ENDPOINTS.UPDATE.url, data)
            return response.data;
        } catch (error) {
            this.#handleError('Failed to update a payment', error);
        }
    }


    #handleError(message, error) {
        const result = `${message}: ${error?.data?.message}`;
        throw new Error(result);
    }

}

// Export a single instance (Singleton pattern)
const apiService = new ApiService('http:/localhost:9090/api');
//apiService.setToken(ApiService.getToken())
export default apiService;