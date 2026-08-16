import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchNotifications = async () => {
    try {
      const res = await api.get('/api/notifications');
      setNotifications(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleNotificationClick = async (notif) => {
    try {
      if (!notif.read) {
        // Mark as read in backend
        await api.put(`/api/notifications/${notif.id}/read`);
      }
      // Redirect to complaint details
      navigate(`/complaint/${notif.complaintId}`);
    } catch (err) {
      console.error('Failed to mark notification as read', err);
      // Fallback redirect
      navigate(`/complaint/${notif.complaintId}`);
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <h2 className="mb-2">Notifications Center</h2>
      
      <div className="glass-panel">
        {loading ? (
          <p className="text-muted">Loading notifications...</p>
        ) : notifications.length === 0 ? (
          <div className="text-center" style={{ padding: '2rem' }}>
            <p className="text-muted">No notifications raised yet.</p>
          </div>
        ) : (
          <div className="notification-list">
            {notifications.map(notif => (
              <div 
                key={notif.id}
                onClick={() => handleNotificationClick(notif)}
                className={`notification-item clickable ${!notif.read ? 'unread' : ''}`}
              >
                <span style={{ fontSize: '1.5rem' }}>
                  {!notif.read ? '🔵' : '⚪'}
                </span>
                <div className="notification-content">
                  <div className="notification-text" style={{ fontWeight: !notif.read ? 600 : 400 }}>
                    {notif.message}
                  </div>
                  <div className="notification-time">
                    {new Date(notif.createdAt).toLocaleDateString()} at {new Date(notif.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Notifications;
