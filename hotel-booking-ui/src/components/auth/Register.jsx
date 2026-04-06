import { useEffect, useRef, useState } from "react"
import { useNavigate } from "react-router-dom";
import { apiService } from "../../services/ApiService" 
import { HttpStatusCode } from "axios";
import { RoutePaths } from "../../constants/RoutePaths";
import { Link } from "react-router-dom";
import { validateRegisterV1 } from "../../utils/validationHelper";
import { MessageType } from "../../constants/MessageType";


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
    // Initialize field errors hook
    const [fieldError, setFieldErrors] = useState({}); 
     
    useEffect(() => {
        if (!message.text) {
            return;
        }

        // Use a small delay to ensure the DOM has rendered the message before scrolling
        const scrollTimer = setTimeout(() => {
            messageRef.current?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }, 1000);

        if (message.type === MessageType.ERROR) {
            const timer = setTimeout(() => setMessage({ text: '', type: '' }), 5000);
            return () => { clearTimeout(timer); clearTimeout(scrollTimer); };
        }

        if (message.type === MessageType.SUCCESS) {
            const timer = setTimeout(() => navigate(RoutePaths.LOGIN), 2000);
            return () => { clearTimeout(timer); clearTimeout(scrollTimer); };
        }
    }, [message.text, message.type, navigate]);

    useEffect(() => {
    // Only start validating once the user has at least started typing 
    // or if an error already exists (to avoid showing errors on a fresh page)
    if (Object.values(formData).some(val => val !== '') || Object.keys(fieldError).length > 0) {
        //validate(formData);
        const {isValid, newErrors } = validateRegisterV1(formData);
        setFieldErrors(newErrors);
        isValid
    }
}, [formData]); // <--- Watch the data changes directly

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });

        // Validate on the fly so errors disappear as clients fix them
        //validate(formData);
    };

    //  const isFormValid = Object.values(formData).every((field) => {
    //     return field?.trim();
    //  });

     const handleSubmit = async (e) => {
        e.preventDefault();

        const form = new FormData(this);
        console.log("Form: " + form);
        
        const { isValid } = validateRegisterV1(formData);
        if (!isValid) {
            setMessage({ type: MessageType.ERROR, text: "Please correct the highlighted errors." });
            return;
        }

        setLoading(true);

        try {
            const response = await apiService.registerUser(formData);
            // Safer check: Accept both 200 and 201
            if (response.statusCode === HttpStatusCode.Created || response.statusCode === HttpStatusCode.Ok) {
                setFormData(initialFormState); // Reset form immediately on success
                setMessage({ type: "success", text: "You have successfully registered." });
            } else {
                // Handle cases where the API returns a 200-level code but something is wrong
                setMessage({ type: MessageType.ERROR, text: response.message || "Unexpected response from server" });
            }
        } catch (error) {
            if (error?.cause?.statusCode === HttpStatusCode.Conflict) {
                setMessage({ 
                    type: MessageType.ERROR, 
                    text: "Invalid email, please try again."
                });
            } else {
                setMessage({ 
                    type: MessageType.ERROR, 
                    text: error?.response?.data?.message || error.message || "Connection to server failed" 
                });
            }
        } finally {
            setLoading(false);
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
                                ? "bg-green-50 text-green-700 border-green-300" 
                                : "bg-red-50 text-red-700 border-red-200"
                        }`}>
                            {message.text}
                        </div>
                    )}
                </div>
    

                <form className="space-y-4" onSubmit={handleSubmit}>
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-1">
                            <label className="text-sm font-medium text-gray-700">First Name</label>
                            <input
                                // required
                                name="firstName"
                                type="text"
                                // placeholder="First Name"
                                value={formData.firstName}
                                onChange={handleInputChange}
                                //onInput={handleInputChange} // Adds extra layer for autocomplete/paste
                                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                                    fieldError.firstName ? "border-red-500 focus:ring-red-200" : "border-gray-300 focus:ring-blue-500"
                                }`}
                            />
                            {/* Per-field error message */}
                            {fieldError.firstName && <p className="text-xs text-red-500 mt-1">{fieldError.firstName}</p>}
                        </div>
                        <div className="space-y-1">
                            <label className="text-sm font-medium text-gray-700">Last Name</label>
                            <input
                                // required
                                name="lastName"
                                type="text"
                                // placeholder="Last Name"
                                value={formData.lastName}
                                onChange={handleInputChange}
                                //onInput={handleInputChange} // Adds extra layer for autocomplete/paste
                                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                                    fieldError.lastName ? "border-red-500 focus:ring-red-200" : "border-gray-300 focus:ring-blue-500"
                                }`}
                            />
                            {/* Per-field error message */}
                            {fieldError.lastName && <p className="text-xs text-red-500 mt-1">{fieldError.lastName}</p>}
                        </div>
                    </div>

                    <div className="space-y-1">
                        <label className="text-sm font-medium text-gray-700">Email Address</label>
                        <input
                            // required
                            name="email"
                            type="email"
                            value={formData.email}
                            // placeholder="Email"
                            onChange={handleInputChange}
                            //onInput={handleInputChange} // Adds extra layer for autocomplete/paste
                            className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                                fieldError.email ? "border-red-500 focus:ring-red-200" : "border-gray-300 focus:ring-blue-500"
                            }`}
                        />
                        {/* Per-field error message */}
                        {fieldError.email && <p className="text-xs text-red-500 mt-1">{fieldError.email}</p>}
                    </div>


                    <div className="space-y-1">
                        <label className="text-sm font-medium text-gray-700">Phone Number</label>
                        <input
                            // required
                            name="phoneNumber"
                            type="tel"
                            value={formData.phoneNumber}
                            // placeholder="Phone Number"
                            onChange={handleInputChange}
                            //onInput={handleInputChange} // Adds extra layer for autocomplete/paste
                            className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                                fieldError.phoneNumber ? "border-red-500 focus:ring-red-200" : "border-gray-300 focus:ring-blue-500"
                            }`}
                        />
                        {/* Per-field error message */}
                        {fieldError.phoneNumber && <p className="text-xs text-red-500 mt-1">{fieldError.phoneNumber}</p>}
                    </div>

                   <div className="space-y-1">
                        <label className="text-sm font-medium text-gray-700">Password</label>
                        <input
                            // required
                            name="password"
                            type="password"
                            value={formData.password}
                            // placeholder="Password"
                            onChange={handleInputChange}
                            //onInput={handleInputChange} // Adds extra layer for autocomplete/paste
                            className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                                fieldError.password ? "border-red-500 focus:ring-red-200" : "border-gray-300 focus:ring-blue-500"
                            }`}
                        />
                        {/* Per-field error message */}
                        {fieldError.password && <p className="text-xs text-red-500 mt-1">{fieldError.password}</p>}
                    </div>

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