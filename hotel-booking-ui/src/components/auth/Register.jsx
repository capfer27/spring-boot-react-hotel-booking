import { useEffect, useRef, useState } from "react"
import { useNavigate } from "react-router-dom";
import { apiService } from "../../services/ApiService" 
import { HttpStatusCode } from "axios";
import { RoutePaths } from "../../constants/RoutePaths";
import { Link } from "react-router-dom";
// import '../../index-backup.css';


export const RegisterPage = () => {
    const navigate = useNavigate();
    const messageRef = useRef(null); // Used for auto-scroll

    const initialFormState = {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        phoneNumber: ''
    };

    const [formData, setFormData] = useState(initialFormState);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({type: '', text: '' });

     
    useEffect(() => {
        if (!message.text) {
            return;
        }

        // Use a small delay to ensure the DOM has rendered the message before scrolling
        const scrollTimer = setTimeout(() => {
            messageRef.current?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }, 100);

        if (message.type === 'error') {
            const timer = setTimeout(() => setMessage({ text: '', type: '' }), 5000);
            return () => { clearTimeout(timer); clearTimeout(scrollTimer); };
        }

        if (message.type === 'success') {
            const timer = setTimeout(() => navigate(RoutePaths.LOGIN), 2000);
            return () => { clearTimeout(timer); clearTimeout(scrollTimer); };
        }
    }, [message.text, message.type, navigate]);

    //  // Handle input change
    //  const handleInputChange = ({target: { name, value}}) => {
    //     setFormData( (prev) => ({... prev, [name]:value}));
    //  }

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

     const isFormValid = Object.values(formData).every((field) => {
        return field?.trim();
     });

     const handleSubmit = async (e) => {
        e.preventDefault();
        if (loading) {
            return; // PREVENT DOUBLE FORM SUBMISSION
        }

        setLoading(true);
        setMessage({ text: '', type: '' });

        if (!isFormValid) {
           setMessage({type: 'error', text: "Please fill all required fields"})
           setLoading(false);
           return;
        }

        try {
            const response = await apiService.registerUser(formData);
            // Safer check: Accept both 200 and 201
            if (response.statusCode === HttpStatusCode.Created || response.statusCode === HttpStatusCode.Ok) {
                setFormData(initialFormState); // Reset form immediately on success
                setMessage({ type: "success", text: "You have successfully registered." });
            } else {
                // Handle cases where the API returns a 200-level code but something is wrong
                setMessage({ type: "error", text: response.message || "Unexpected response from server" });
            }
        } catch (error) {
            console.error("Registration Error:", error); // Helpful for debugging
            setMessage({ 
                type: "error", 
                text: error?.response?.data?.message || error.message || "Connection to server failed" 
            });
        }
     };

         return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
            <div className="max-w-md w-full space-y-8 bg-white p-8 rounded-xl shadow-lg">
                <div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">Create your account</h2>
                    <p className="mt-2 text-center text-sm text-gray-600">Join Carl Hotel today</p>
                </div>

                 {/* MESSAGE BOX WITH REF FOR SCROLLING */}
                <div ref={messageRef}>
                    {message.text && (
                        <div className={`p-3 rounded-md text-sm text-center border animate-in fade-in duration-300 ${
                            message.type === 'success' 
                                ? "bg-green-50 text-green-700 border-green-200" 
                                : "bg-red-50 text-red-700 border-red-200"
                        }`}>
                            {message.text}
                        </div>
                    )}
                </div>
    

                <form className="space-y-4" onSubmit={handleSubmit}>
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
                        className="w-full flex items-center justify-center py-3 px-4 rounded-lg text-white bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 transition-all font-semibold"
                    >
                        {loading ? (
                            <>
                                {/* LOADING SPINNER */}
                                <svg className="animate-spin h-5 w-5 mr-3 text-white" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                                </svg>
                                Creating Account...
                            </>
                        ) : "Register"}
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