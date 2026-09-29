import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export function ProtectedRoute({ children, allowedRoles }) {
  const { isAuthenticated, user } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && allowedRoles.length > 0 && !allowedRoles.includes(user?.role)) {
    return (
      <div className="page-container" style={{ textAlign: 'center', paddingTop: '4rem' }}>
        <div className="card" style={{ maxWidth: '500px', margin: '0 auto' }}>
          <h2 style={{ color: 'var(--danger)', marginBottom: '0.75rem' }}>Access Denied</h2>
          <p style={{ marginBottom: '1.5rem' }}>
            You do not have administrative permission to view this resource.
          </p>
          <button className="btn btn-primary" onClick={() => window.history.back()}>
            Go Back
          </button>
        </div>
      </div>
    );
  }

  return children;
}
