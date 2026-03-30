import { NavLink, useNavigate } from "react-router-dom";
import { ApiService } from "../../services/ApiService";
import { RoutePaths } from "../../constants/RoutePaths";

const Navbar = () => {
    const isAuthenticated = ApiService.isAuthenticated();
    const isCustomer = ApiService.isCustomer();
    const isAdmin = ApiService.isAdmin();

    const navigate = useNavigate();

    const handleLogout = () => {
        const isLogout = window.confirm("Are you sure you want to logout?");
        if (isLogout) {
            ApiService.logout();
            navigate(RoutePaths.HOME);
        }
    }

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <NavLink to="/home">Carl Hotel</NavLink>
            </div>

            <ul className="navbar-ul">
                <li>
                    <NavLink to="/home">Home</NavLink>
                </li>
                <li>
                    <NavLink to="/rooms">Rooms</NavLink>
                </li>
                <li>
                    <NavLink to="/find-bookings">My Bookins</NavLink>
                </li>
                { isCustomer && 
                    <li>
                        <NavLink to="/profile">Profile</NavLink>
                    </li>
                }
                { isAdmin && 
                    <li>
                        <NavLink to="/admin">Admin</NavLink>
                    </li>
                }
                { !isAuthenticated && 
                    <li>
                        <NavLink to="/login">login</NavLink>
                    </li>
                }
                { !isAuthenticated && 
                    <li>
                        <NavLink to="/register">Register</NavLink>
                    </li>
                }
                { isAuthenticated && 
                    <li>
                        <NavLink onClick={handleLogout} to="/logout">Logout</NavLink>
                    </li>
                }
            </ul>
        </nav>
    );

}

export default Navbar;