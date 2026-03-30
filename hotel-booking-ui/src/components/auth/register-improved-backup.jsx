import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { apiService } from '../../services/ApiService';
import { RoutePaths } from '../../constants/RoutePaths';

export const RegisterPage = () => {
    const navigate = useNavigate();
    const messageRef = useRef(null);

    const initialFormState = {
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        password: ''
    };

    const [formData, setFormData] = useState(initialFormState);
    const [fieldError, setFieldError] = useState({});
    const [validFields, setValidFields] = useState({});
    const [message, setMessage] = useState({ text: '', type: '' });
    const [loading, setLoading] = useState(false);

    // 1. DEBOUNCED VALIDATION HOOK
    // This replaces the old validate() function for real-time feedback
    useEffect(() => {
        const handler = setTimeout(() => {
            const errors = {};
            const success = {};

            // Email Validation
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (formData.email) {
                if (emailRegex.test(formData.email)) success.email = true;
                else errors.email = "Invalid email format";
            }

            // Russian Phone Validation
            const phoneRegex = /^(?:\+7|8)[\s\-]?\(?\d{3}\)?[\s\-]?\d{3}[\s\-]?\d{2}[\s\-]?\d{2}$/;
            if (formData.phoneNumber) {
                if (phoneRegex.test(formData.phoneNumber)) success.phoneNumber = true;
                else errors.phoneNumber = "Invalid Russian format (+7/8...)";
            }

            // Password Validation
            if (formData.password) {
                if (formData.password.length >= 8) success.password = true;
                else errors.password = "Must be at least 8 characters";
            }

            // Names (Simple check)
            if (formData.firstName.trim().length > 1) success.firstName = true;
            if (formData.lastName.trim().length > 1) success.lastName = true;

            setFieldError(errors);
            setValidFields(success);
        }, 900); // 500ms delay

        return () => clearTimeout(handler);
    }, [formData]);

    // 2. STATUS & REDIRECT HOOK
    useEffect(() => {
        if (!message.text) return;
        messageRef.current?.scrollIntoView({ behavior: 'smooth', block: 'center' });

        if (message.type === 'success') {
            const timer = setTimeout(() => navigate(RoutePaths.LOGIN), 2000);
            return () => clearTimeout(timer);
        }
    }, [message, navigate]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // FINAL GUARD: Ensure all fields are valid before API call
        const requiredFields = ['firstName', 'lastName', 'email', 'phoneNumber', 'password'];
        const allValid = requiredFields.every(field => validFields[field] === true);

        if (!allValid) {
            setMessage({ text: "Please fix the errors before submitting.", type: "error" });
            return;
        }

        setLoading(true);
        try {
            await apiService.registerUser(formData);
            setMessage({ text: "Account created! Redirecting...", type: "success" });
            setFormData(initialFormState);
        } catch (err) {
            setMessage({ text: err.response?.data?.message || "Registration failed", type: "error" });
        } finally {
            setLoading(false);
        }
    };

    // Reusable Input Component for cleaner JSX
    const FormField = ({ label, name, type = "text", placeholder }) => (
        <div className="space-y-1 relative">
            <label className="block text-sm font-medium text-gray-700">{label}</label>
            <div className="relative">
                <input
                    name={name}
                    type={type}
                    value={formData[name]}
                    onChange={handleInputChange}
                    placeholder={placeholder}
                    className={`w-full px-3 py-2 border rounded-lg outline-none transition-all pr-10 ${
                        fieldError[name] ? "border-red-500 focus:ring-red-200" : 
                        validFields[name] ? "border-green-500 focus:ring-green-200" : "border-gray-300 focus:ring-blue-200"
                    } focus:ring-2`}
                />
                {validFields[name] && (
                    <span className="absolute right-3 top-2 text-green-500 text-lg">✔</span>
                )}
            </div>
            {fieldError[name] && <p className="text-xs text-red-500">{fieldError[name]}</p>}
        </div>
    );

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
            <div className="max-w-md w-full bg-white p-8 rounded-xl shadow-lg border border-gray-100">
                <div ref={messageRef}>
                    {message.text && (
                        <div className={`p-3 mb-4 rounded-md text-sm text-center ${
                            message.type === 'success' ? "bg-green-50 text-green-700" : "bg-red-50 text-red-700"
                        }`}>
                            {message.text}
                        </div>
                    )}
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <FormField label="First Name" name="firstName" placeholder="John" />
                        <FormField label="Last Name" name="lastName" placeholder="Doe" />
                    </div>
                    <FormField label="Email" name="email" type="email" placeholder="john@example.com" />
                    <FormField label="Phone" name="phoneNumber" type="tel" placeholder="+7..." />
                    <FormField label="Password" name="password" type="password" placeholder="••••••••" />
                    
                    <button 
                        disabled={loading}
                        className="w-full bg-blue-600 text-white py-3 rounded-lg font-bold hover:bg-blue-700 disabled:bg-blue-300 transition-all flex justify-center"
                    >
                        {loading ? "Creating Account..." : "Create Account"}
                    </button>
                </form>
            </div>
        </div>
    );
};