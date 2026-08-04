import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import "./index.css";
import VerifyEmail from "./pages/VerifyEmail";
import OrgAdminDashboard from "./pages/OrgAdminDashboard";
import SuperAdmin from "./pages/SuperAdmin";
import ProtectedRoute from "./components/auth/ProtectedRoute";
import OrganizationDashboard from "./pages/OrganizationDashboard";
import PublicCheckout from "./pages/PublicCheckout";
import PaymentSuccess from "./pages/PaymentSuccess";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/home" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/verify-email" element={<VerifyEmail />} />
        <Route
          path="/dashboard/organizations"
          element={
            <ProtectedRoute>
              <OrgAdminDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard/organizations/:organizationId"
          element={
            <ProtectedRoute>
              <OrganizationDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard/super-admin"
          element={
            <ProtectedRoute requiredRole="SUPER_ADMIN">
              <SuperAdmin />
            </ProtectedRoute>
          }
        />
        <Route path="/public/org/:token" element={<PublicCheckout />} />
        <Route path="/public/success" element={<PaymentSuccess />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  </StrictMode>,
);
