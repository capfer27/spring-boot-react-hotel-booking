import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom";
import { apiService } from "../../services/ApiService" 
import { HttpStatusCode } from "axios";
import { RoutePaths } from "../../constants/RoutePaths";
import { Link } from "react-router-dom";
// import '../../index-backup.css';


export const RegisterPage = () => {
     const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        phoneNumber: ""
     });

     const [message, setMessage] = useState({
        type: "",
        text: ""
     });

    const navigate = useNavigate();
     
    // Handles message clearing
    useEffect(() => {
        if (!message.text) {
            return;
        }

        if (message.type === 'error') {
            const errorTimer = setTimeout(() => {
                setMessage({ text: '', type: '' });
            }, 5000);
            return () => clearTimeout(errorTimer);
        }

        if (message.type === 'success') {
            // Clear the message after 5 seconds
            const timer = setTimeout(() => {
                setMessage({text: '', type: ''})
            }, 2000);

            return () => clearTimeout(timer);
        }

    }, [message.text, message.type, navigate]); // Only reruns if these data change.

     const [loading, setLoading] = useState(false);

     // Handle input change
     const handleInputChange = ({target: { name, value}}) => {
        setFormData( (prev) => ({... prev, [name]:value}));
     }

     const isFormValid = Object.values(formData).every((field) => {
        return field?.trim();
     });

     const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        if (!isFormValid) {
           setMessage({type: "error", text: "Please fill all required fields"})
           return;
        }

        try {
            const response = await apiService.registerUser(formData);
            if (response.statusCode === HttpStatusCode.Created) {
                setMessage({type: "success", text: "You have successfully registered."});
            }
        } catch (error) {
            setMessage({type: "error", text: error?.response?.data?.message || error.message})
        } finally {
            setLoading(false);
        }
     };

         return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8 bg-white p-8 rounded-xl shadow-lg border border-gray-100">
                <div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">Create your account</h2>
                    <p className="mt-2 text-center text-sm text-gray-600">Join Carl Hotel today</p>
                </div>

                {message.text && (
                    <div className={`p-3 rounded-md text-sm text-center border ${
                        message.type === 'success' 
                         ? "bg-green-50 text-green-700 border-green-200" 
                         : "bg-red-50 text-red-700 border-red-200"
                        }`}>
                        {message.text}
                    </div>
                )}
    

                <form className="mt-8 space-y-4" onSubmit={handleSubmit}>
                    <div className="grid grid-cols-2 gap-4">
                        <input
                            name="firstName"
                            type="text"
                            required
                            placeholder="First Name"
                            className="appearance-none rounded-lg block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                            onChange={handleInputChange}
                        />
                        <input
                            name="lastName"
                            type="text"
                            required
                            placeholder="Last Name"
                            className="appearance-none rounded-lg block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                            onChange={handleInputChange}
                        />
                    </div>

                    <input
                        name="email"
                        type="email"
                        required
                        placeholder="Email address"
                        className="appearance-none rounded-lg block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        onChange={handleInputChange}
                    />

                    <input
                        name="phoneNumber"
                        type="tel"
                        required
                        placeholder="Phone Number"
                        className="appearance-none rounded-lg block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        onChange={handleInputChange}
                    />

                    <input
                        name="password"
                        type="password"
                        required
                        placeholder="Password"
                        className="appearance-none rounded-lg block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        onChange={handleInputChange}
                    />

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-blue-300 transition-colors"
                    >
                        {loading ? "Registering..." : "Register"}
                    </button>
                </form>

                <div className="text-center mt-4">
                    <p className="text-sm text-gray-600">
                        Already have an account?{" "}
                        <Link to={RoutePaths.LOGIN} className="font-medium text-blue-600 hover:text-blue-500">
                            Login here
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};