import { Routes } from 'react-router-dom';
import TWNavbar from './components/common/tailwind/TWNavbar';
import TWFooter from './components/common/tailwind/TWFooter';
import { apiService, ApiService } from './services/ApiService';
import { useNavigate } from 'react-router-dom';
import { Route } from 'react-router-dom';
import { RoutePaths } from './constants/RoutePaths';
import { RegisterPage } from './components/auth/Register';
import { LoginPage } from './components/auth/LoginPage';
import HomePage from './components/home/HomePage';

const App = () => {
  const isAuthenticated = ApiService.isAuthenticated();
  const isCustomer = ApiService.isCustomer();
  const isAdmin = ApiService.isAdmin();

  const navigate = useNavigate();

  const handleLogout = () => {
    const isLogout = window.confirm('Are you sure you want to logout?');
    if (isLogout) {
      apiService.logout();
      navigate(RoutePaths.HOME);
    }
  };

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
          <Route path={RoutePaths.LOGIN} element={<LoginPage />} />
          {/* <Route path={RoutePaths.ROOMS} element={<RoomsPage />} /> */}
          <Route path={RoutePaths.HOME} element={<HomePage />} />
          {/* <Route exact path={RoutePaths.HOME} element={<HomePageStart />} /> */}

          {/* Add your other routes here */}
        </Routes>
      </main>

      <TWFooter />
    </div>
  );
};

export default App;
