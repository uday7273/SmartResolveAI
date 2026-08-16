import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import UserDashboard from './dashboards/UserDashboard/UserDashboard';
import RaiseComplaint from './dashboards/UserDashboard/RaiseComplaint';
import TechnicianDashboard from './dashboards/TechnicianDashboard/TechnicianDashboard';
import AdminDashboard from './dashboards/AdminDashboard/AdminDashboard';
import AdminDepartments from './dashboards/AdminDashboard/AdminDepartments';
import ComplaintDetails from './pages/ComplaintDetails';
import Notifications from './pages/Notifications';

// Protected Route Wrapper with Role Checks
const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="text-center" style={{ padding: '4rem' }}>
        <p className="text-muted">Restoring active session...</p>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    const defaultPaths = {
      'ADMIN': '/admin',
      'TECHNICIAN': '/technician',
      'USER': '/user'
    };
    return <Navigate to={defaultPaths[user.role] || '/'} replace />;
  }

  return children;
};

// Default Route Redirector based on Active Session Role
const HomeRedirector = () => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="text-center" style={{ padding: '4rem' }}>
        <p className="text-muted">Restoring session...</p>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  const defaultPaths = {
    'ADMIN': '/admin',
    'TECHNICIAN': '/technician',
    'USER': '/user'
  };

  return <Navigate to={defaultPaths[user.role] || '/login'} replace />;
};

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="app-container">
          <Navbar />
          <main className="main-content">
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* Protected Home Route */}
              <Route path="/" element={<HomeRedirector />} />

              {/* USER Role Protected Routes */}
              <Route 
                path="/user" 
                element={
                  <ProtectedRoute allowedRoles={['USER']}>
                    <UserDashboard />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/user/raise" 
                element={
                  <ProtectedRoute allowedRoles={['USER']}>
                    <RaiseComplaint />
                  </ProtectedRoute>
                } 
              />

              {/* TECHNICIAN Role Protected Routes */}
              <Route 
                path="/technician" 
                element={
                  <ProtectedRoute allowedRoles={['TECHNICIAN']}>
                    <TechnicianDashboard />
                  </ProtectedRoute>
                } 
              />

              {/* ADMIN Role Protected Routes */}
              <Route 
                path="/admin" 
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <AdminDashboard />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/admin/complaints" 
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <AdminDashboard />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/admin/departments" 
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <AdminDepartments />
                  </ProtectedRoute>
                } 
              />

              {/* Shared Protected Routes */}
              <Route 
                path="/complaint/:id" 
                element={
                  <ProtectedRoute>
                    <ComplaintDetails />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/notifications" 
                element={
                  <ProtectedRoute>
                    <Notifications />
                  </ProtectedRoute>
                } 
              />

              {/* Catch-all Wildcard */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;
