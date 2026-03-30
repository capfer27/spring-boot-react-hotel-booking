import { Routes } from 'react-router-dom'
import Navbar from './components/common/Navbar'
import Footer from './components/common/Footer'
import TWNavbar from './components/common/tailwind/TWNavbar'
import TWFooter from './components/common/tailwind/TWFooter'
import { ApiService } from './services/ApiService'
import { useNavigate, Navigate } from 'react-router-dom'
import { Route } from 'react-router-dom'
import { RoutePaths } from './constants/RoutePaths'
import { RegisterPage } from './components/auth/Register';

const App = () => {

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

      <div className="flex flex-col min-h-screen bg-gray-50">
        {/* Pass all auth props to Navbar */}
        <TWNavbar 
          isAuthenticated={isAuthenticated}
          isAdmin={isAdmin}
          isCustomer={isCustomer}
          handleLogout={handleLogout}
        />

         {/* Main Content Area */}
      <main className="grow">
        <Routes>
          <Route path={RoutePaths.REGISTER} element={<RegisterPage />} />
          {/* <Route path={RoutePaths.REGISTER} element={<Navigate to={RegisterPage} />} /> */}
          {/* <Route path="/home" element={<Home />} /> */}
          {/* Add your other routes here */}
        </Routes>
      </main>

        
        <TWFooter />
      </div>
  );
}

export default App
