import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import api from '../api/axiosConfig';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [unreadCount, setUnreadCount] = useState(0);

  // Poll for unread notification count
  useEffect(() => {
    if (!user) return;

    const fetchUnreadCount = async () => {
      try {
        const res = await api.get('/api/notifications/unread-count');
        setUnreadCount(res.data);
      } catch (err) {
        console.error('Failed to fetch unread notification count', err);
      }
    };

    fetchUnreadCount();
    const interval = setInterval(fetchUnreadCount, 15000); // poll every 15s

    return () => clearInterval(interval);
  }, [user, location]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) return null;

  return (
    <nav className="navbar">
      <Link to="/" className="nav-brand">
        <span>⚡</span> SmartResolve AI
      </Link>

      <div className="nav-links">
        {user.role === 'ADMIN' && (
          <>
            <Link to="/admin" className={`nav-link ${location.pathname === '/admin' ? 'active' : ''}`}>
              Dashboard
            </Link>
            <Link to="/admin/complaints" className={`nav-link ${location.pathname === '/admin/complaints' ? 'active' : ''}`}>
              All Complaints
            </Link>
            <Link to="/admin/departments" className={`nav-link ${location.pathname === '/admin/departments' ? 'active' : ''}`}>
              Departments
            </Link>
          </>
        )}

        {user.role === 'TECHNICIAN' && (
          <Link to="/technician" className={`nav-link ${location.pathname === '/technician' ? 'active' : ''}`}>
            Assigned Tickets
          </Link>
        )}

        {user.role === 'USER' && (
          <>
            <Link to="/user" className={`nav-link ${location.pathname === '/user' ? 'active' : ''}`}>
              My Dashboard
            </Link>
            <Link to="/user/raise" className={`nav-link ${location.pathname === '/user/raise' ? 'active' : ''}`}>
              Raise Ticket
            </Link>
          </>
        )}

        <Link to="/notifications" className="nav-link flex-align" style={{ position: 'relative' }}>
          <span>🔔</span> Notifications
          {unreadCount > 0 && (
            <span style={{
              background: '#ef4444',
              color: 'white',
              borderRadius: '50%',
              padding: '0.1rem 0.4rem',
              fontSize: '0.7rem',
              position: 'absolute',
              top: '-5px',
              right: '-5px',
              fontWeight: 800
            }}>
              {unreadCount}
            </span>
          )}
        </Link>

        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginLeft: '1rem', borderLeft: '1px solid var(--border-color)', paddingLeft: '1.5rem' }}>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>{user.name}</div>
            <div style={{ fontSize: '0.75rem', color: '#8b5cf6', fontWeight: 700, textTransform: 'uppercase' }}>
              {user.role}
            </div>
          </div>
          <button onClick={handleLogout} className="btn btn-secondary" style={{ padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
