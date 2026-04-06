import React, { useState } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { ApiService, apiService } from '../../services/ApiService';
import { RoutePaths } from '../../constants/RoutePaths';
import { MessageType } from '../../constants/MessageType';
import { validateLogin } from '../../utils/validationHelper';
import { HttpStatusCode } from 'axios';

export const LoginPage = () => {
    const navigate = useNavigate();
    const [credentials, setCredentials] = useState({ email: '', password: '' });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ text: '', type: '' });
     const [fieldErrors, setFieldErrors] = useState({});
    
    const {state} = useLocation()
    // Redirect the user from where he came or to home page.
    const redirectPath = state?.from?.pathname || RoutePaths.HOME;

    const handleLogin = async (e) => {
        e.preventDefault();

        const {isValid, errors } = validateLogin(credentials)

        if (!isValid) {
            setFieldErrors(errors)
            return;
        }

        setLoading(true);
         setFieldErrors({});
        try {
            const { statusCode, token, role } = await apiService.loginUser(credentials);

            if (statusCode === HttpStatusCode.Ok) {
                // Save Token & Role using your static methods
                ApiService.saveToken(token);
                ApiService.saveRole(role);

                // Set instance token for immediate use
                apiService.setToken(token);
                setMessage({ text: "Login successful! Redirecting...", type: MessageType.SUCCESS });
                //setTimeout(() => navigate(RoutePaths.HOME), 1500);
                navigate(redirectPath, {replace: true});
            } else {
                setMessage({ text: err.response?.data?.message || "Invalid email or password. Please try again.", type: MessageType.ERROR });
            }
        } catch (err) {
            setMessage({ text: err.response?.data?.message || "Invalid email or password. Please try again.", type: MessageType.ERROR });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
            <div className="max-w-md w-full bg-white p-8 rounded-xl shadow-lg border border-gray-100">
                <h2 className="text-2xl font-bold text-center text-gray-900 mb-6">Login to Carl Hotel</h2>
                
                {message.text && (
                    <div className={`p-3 mb-4 rounded-md text-sm text-center ${
                        message.type === MessageType.SUCCESS ? "bg-green-50 text-green-700" : "bg-red-50 text-red-700"
                    }`}>
                        {message.text}
                    </div>
                )}

                <form onSubmit={handleLogin} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Email</label>
                        <input 
                            type="email" 
                            required
                            className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                            onChange={(e) => setCredentials({...credentials, email: e.target.value})}
                        />
                        {fieldErrors.email && <p className="text-xs text-red-500 mt-1">{fieldErrors.email}</p>}
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Password</label>
                        <input 
                            type="password" 
                            required
                            className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                            onChange={(e) => setCredentials({...credentials, password: e.target.value})}
                        />
                        {fieldErrors.password && <p className="text-xs text-red-500 mt-1">{fieldErrors.password}</p>}
                    </div>
                    <button 
                        disabled={loading}
                        className="w-full bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700 disabled:bg-blue-300 transition-all"
                    >
                        {loading ? "Verifying..." : "Sign In"}
                    </button>
                </form>
                <p className="mt-4 text-center text-sm text-gray-600">
                    New here? <Link to={RoutePaths.REGISTER} className="text-blue-600 font-medium">Create account</Link>
                </p>
            </div>
        </div>
    );
};
