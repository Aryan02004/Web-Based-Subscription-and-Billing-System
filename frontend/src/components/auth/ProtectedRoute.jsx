import { Navigate, useLocation } from "react-router-dom";
import { getStoredUser, isAuthenticated } from "../../lib/api/client";

function ProtectedRoute({ children, requiredRole }) {
  const location = useLocation();
  const user = getStoredUser();

  if (!isAuthenticated()) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (requiredRole && user?.role !== requiredRole) {
    return (
      <Navigate
        to="/dashboard/organizations"
        replace
        state={{ message: "Super Admin access required." }}
      />
    );
  }

  return children;
}

export default ProtectedRoute;
