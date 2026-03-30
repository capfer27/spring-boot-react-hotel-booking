import { useLocation, Navigate } from "react-router-dom";
import { ApiService } from "./ApiService";
import { RoutePaths } from "../constants/RoutePaths";

export const CustomerRoute = ({element}) => {
 const location = useLocation();
  return ApiService.isAuthenticated() ? element : (<Navigate to={RoutePaths.LOGIN} replace state={{from: location}} />);
}

export const AdminRoute = ({element}) => {
    const location = useLocation();
    return ApiService.isAdmin() ? element : (<Navigate to={RoutePaths.LOGIN} replace state={{from: location}}/>)
}