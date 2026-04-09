import { NavLink } from 'react-router-dom';
import { RoutePaths } from '../../../constants/RoutePaths';

const TWNavbar = ({ isAuthenticated, isAdmin, isCustomer, handleLogout }) => {
  // Utility for active link styling
  const navLinkClass = ({ isActive }) =>
    `px-3 py-2 rounded-md text-sm font-medium transition-colors ${
      isActive
        ? 'bg-blue-700 text-white'
        : 'text-gray-300 hover:bg-gray-700 hover:text-white'
    }`;

  return (
    <nav className="bg-gray-900 border-b border-gray-800 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand/Logo */}
          <div className="shrink-0">
            <NavLink
              to={RoutePaths.HOME}
              className="text-white font-bold text-xl tracking-tight"
            >
              Carl <span className="text-blue-500">Hotel</span>
            </NavLink>
          </div>

          {/* Navigation Links */}
          <div className="hidden md:block">
            <ul className="flex items-center space-x-4">
              <li>
                <NavLink to={RoutePaths.HOME} className={navLinkClass}>
                  Home
                </NavLink>
              </li>
              <li>
                <NavLink to={RoutePaths.ROOMS} className={navLinkClass}>
                  Rooms
                </NavLink>
              </li>
              <li>
                <NavLink to={RoutePaths.FIND_BOOKING} className={navLinkClass}>
                  My Bookings
                </NavLink>
              </li>

              {isCustomer && (
                <li>
                  <NavLink to={RoutePaths.PROFILE} className={navLinkClass}>
                    Profile
                  </NavLink>
                </li>
              )}

              {isAdmin && (
                <li>
                  <NavLink to={RoutePaths.ADMIN} className={navLinkClass}>
                    Admin
                  </NavLink>
                </li>
              )}

              {/* Auth Group */}
              <div className="flex items-center space-x-2 ml-4 border-l border-gray-700 pl-4">
                {!isAuthenticated ? (
                  <>
                    <li>
                      <NavLink to={RoutePaths.LOGIN} className={navLinkClass}>
                        Login
                      </NavLink>
                    </li>
                    <li>
                      <NavLink
                        to={RoutePaths.REGISTER}
                        className={navLinkClass}
                      >
                        Register
                      </NavLink>
                    </li>
                  </>
                ) : (
                  <li>
                    <button
                      onClick={handleLogout}
                      className="px-3 py-2 rounded-md text-sm font-medium text-red-400 hover:bg-red-900/30 hover:text-red-300 transition-colors cursor-pointer"
                    >
                      Logout
                    </button>
                  </li>
                )}
              </div>
            </ul>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default TWNavbar;
