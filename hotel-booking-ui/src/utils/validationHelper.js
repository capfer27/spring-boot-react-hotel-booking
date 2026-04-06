/**
 * Centralized Validation Logic
 */

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const RU_PHONE_REGEX = /^(?:\+7|8)[\s\-]?\(?\d{3}\)?[\s\-]?\d{3}[\s\-]?\d{2}[\s\-]?\d{2}$/;

export const validateRegisterV2 = (data) => {
    let errors = {};

    if (!data.firstName?.trim()) errors.firstName = "First name is required";
    if (!data.lastName?.trim()) errors.lastName = "Last name is required";
    
    if (!data.email) errors.email = "Email is required";
    else if (!EMAIL_REGEX.test(data.email)) errors.email = "Invalid email format";

    if (!data.phoneNumber) errors.phoneNumber = "Phone number is required";
    else if (!RU_PHONE_REGEX.test(data.phoneNumber)) errors.phoneNumber = "Invalid Russian format";

    if (!data.password || data.password.length < 8) {
        errors.password = "Password must be at least 8 characters";
    }

    return {
        isValid: Object.keys(errors).length === 0,
        errors
    };
};

export const validateRegisterV1 = (formData) => {
        let newErrors = {};

        // First Name & Last Name
        if (!formData.firstName.trim()) newErrors.firstName = "First name is required";
        if (!formData.lastName.trim()) newErrors.lastName = "Last name is required";

        // Email Regex
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!formData.email) {
            newErrors.email = "Email is required";
        } else if (!emailRegex.test(formData.email)) {
            newErrors.email = "Invalid email format";
        }

        // Phone Number (e.g., 10+ digits)
        const phoneRegex = /^\+?[1-9]\d{9,14}$/;
        if (!formData.phoneNumber) {
            newErrors.phoneNumber = "Phone number is required";
        } else if (!phoneRegex.test(formData.phoneNumber)) {
            newErrors.phoneNumber = "Invalid phone number";
        }

        // Password Length
        if (!formData.password) {
            newErrors.password = "Password is required";
        } else if (formData.password.length < 8) {
            newErrors.password = "Password must be at least 8 characters";
        }

        //setFieldErrors(newErrors);
        const isValid = Object.keys(newErrors).length === 0; // Returns true if no errors
        return {
            isValid,
            newErrors
        }
};

export const validateLogin = (data) => {
    let errors = {};

    if (!data.email){
        errors.email = "Email is required";
    } else if (!EMAIL_REGEX.test(data.email)) {
        errors.email = "Invalid email format";
    }

    if (!data.password) {
        errors.password = "Password is required";
    }

    return {
        isValid: Object.keys(errors).length === 0,
        errors
    };
};
