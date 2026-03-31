import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { ApiService, apiService } from '../../services/ApiService';
import { RoutePaths } from '../../constants/RoutePaths';

export const LoginPage = () => {
    const navigate = useNavigate();
    const [credentials, setCredentials] = useState({ email: '', password: '' });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ text: '', type: '' });

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const data = await apiService.loginUser(credentials);
            // 1. Save Token & Role using your static methods
            ApiService.saveToken(data.token);
            ApiService.saveRole(data.role);
            
            // 2. Set instance token for immediate use
            apiService.setToken(data.token);

            setMessage({ text: "Login successful! Redirecting...", type: "success" });
            setTimeout(() => navigate(RoutePaths.HOME), 1500);
        } catch (err) {
            setMessage({ text: err.response?.data?.message || "Invalid email or password. Please try again.", type: "error" });
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
                        message.type === 'success' ? "bg-green-50 text-green-700" : "bg-red-50 text-red-700"
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
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Password</label>
                        <input 
                            type="password" 
                            required
                            className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                            onChange={(e) => setCredentials({...credentials, password: e.target.value})}
                        />
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
