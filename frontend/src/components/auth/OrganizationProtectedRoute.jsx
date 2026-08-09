import { useEffect, useState } from "react";
import { Navigate, useLocation, useParams } from "react-router-dom";
import { getStoredUser, isAuthenticated } from "../../lib/api/client";
import { api } from "../../lib/api";

/**
 * OrganizationProtectedRoute: 
 * Validates that the current user has access to the specified organization
 * before rendering the child component. This prevents unauthorized access
 * even if a user tries to manipulate the URL route.
 */
function OrganizationProtectedRoute({ children }) {
  const location = useLocation();
  const { organizationId } = useParams();
  const [hasAccess, setHasAccess] = useState(null);
  const [loading, setLoading] = useState(true);
  const user = getStoredUser();

  useEffect(() => {
    const validateAccess = async () => {
      setLoading(true);

      // First check: User must be authenticated
      if (!isAuthenticated()) {
        setHasAccess(false);
        setLoading(false);
        return;
      }

      // Second check: Verify organization ID is valid
      if (!organizationId || isNaN(organizationId)) {
        setHasAccess(false);
        setLoading(false);
        return;
      }

      // Third check: Verify user has access to this organization by fetching it
      // This will trigger the backend validation that the user owns/belongs to the organization
      try {
        await api.organization.getById(organizationId);
        setHasAccess(true);
      } catch (error) {
        // User doesn't have access to this organization
        console.warn(`Access denied to organization ${organizationId}:`, error.message);
        setHasAccess(false);
      } finally {
        setLoading(false);
      }
    };

    validateAccess();
  }, [organizationId]);

  // Loading state: Show nothing while validating
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Validating access...</p>
        </div>
      </div>
    );
  }

  // If user is not authenticated, redirect to login
  if (!isAuthenticated()) {
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: location.pathname }}
      />
    );
  }

  // If user doesn't have access to this organization, redirect to their organizations list
  if (!hasAccess) {
    return (
      <Navigate
        to="/dashboard/organizations"
        replace
        state={{
          message: "You don't have access to this organization. Access has been denied.",
        }}
      />
    );
  }

  // User has access, render the protected content
  return children;
}

export default OrganizationProtectedRoute;
