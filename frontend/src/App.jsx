import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/common/ProtectedRoute';
import { AppLayout } from './components/layout/AppLayout';

import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { PurchasesPage } from './pages/PurchasesPage';
import { TransfersPage } from './pages/TransfersPage';
import { AssignmentsExpendituresPage } from './pages/AssignmentsExpendituresPage';
import { AssetsPage } from './pages/AssetsPage';
import { AuditLogsPage } from './pages/AuditLogsPage';
import { NotFoundPage } from './pages/NotFoundPage';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Login Route */}
          <Route path="/login" element={<LoginPage />} />

          {/* Protected Application Routes with Layout */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="purchases" element={<PurchasesPage />} />
            <Route path="transfers" element={<TransfersPage />} />
            <Route path="assignments-expenditures" element={<AssignmentsExpendituresPage />} />
            <Route path="assets" element={<AssetsPage />} />
            
            {/* ADMIN Only Audit Trail */}
            <Route
              path="audit-logs"
              element={
                <ProtectedRoute allowedRoles={['ADMIN']}>
                  <AuditLogsPage />
                </ProtectedRoute>
              }
            />
          </Route>

          {/* 404 Catch-All */}
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
