<!-- Initial Form Component Backup -->
export const RegisterSimple = () => {
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
           setTimeout(() => {
                setMessage({});
           }, 5000);
           return;     
        }

        try {
            const response = await apiService.registerUser(formData);
            if (response.status === HttpStatusCode.Created) {
                setMessage({type: "success", text: "You have successfully registered."});
                setTimeout(() => {
                    navigate(RoutePaths.LOGIN)
                }, 3000);
            }
        } catch (error) {
            setMessage({type: "error", text: error?.response?.data?.message || error.message})
            setTimeout(() => {
                setMessage({});
            }, 5000);
        }
     };

     return (
        <div className="auth-container">
            {message.text && (<p className={`${message.type}-message`}>{message.text}</p>)}

            <h2>Register</h2>
            <form action="post" onSubmit={handleSubmit}>
                {["firstName", "lastName", "email", "phoneNumber", "password"].map((field) => {
                    <div className="form-group" key={field}>
                        <label htmlFor={field}>{field.replace(/([A-Z])/g, " $1").trim()}: </label>
                        <input 
                            type={field === 'email' ? 'email' : 'text'} 
                            name={field} 
                            value={formData[field]} 
                            onChange={handleInputChange}
                            required
                        />
                    </div>
                })
                }

                <button type="submit">Register</button>
            </form>
            <p className="register-link"> Already have na account? <a href={RoutePaths.LOGIN}>Login</a></p>
        </div>
     );
};