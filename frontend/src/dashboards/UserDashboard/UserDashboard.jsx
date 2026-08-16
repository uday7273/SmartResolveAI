import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api/axiosConfig';

const UserDashboard = () => {
  const [complaints, setComplaints] = useState([]);
  const [stats, setStats] = useState({ total: 0, open: 0, resolved: 0 });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchComplaints = async () => {
      try {
        const response = await api.get('/api/complaints');
        setComplaints(response.data);
        
        // Calculate in-memory stats for the user
        const total = response.data.length;
        const open = response.data.filter(c => c.status !== 'RESOLVED' && c.status !== 'CLOSED' && c.status !== 'REJECTED').length;
        const resolved = response.data.filter(c => c.status === 'RESOLVED' || c.status === 'CLOSED').length;
        
        setStats({ total, open, resolved });
      } catch (err) {
        console.error('Failed to load user complaints', err);
      } finally {
        setLoading(false);
      }
    };

    fetchComplaints();
  }, []);

  const getPriorityBadgeClass = (priority) => {
    return `badge badge-${priority.toLowerCase()}`;
  };

  const getStatusBadgeClass = (status) => {
    return `badge badge-${status.toLowerCase()}`;
  };

  return (
    <div>
      <div className="flex-between mb-2">
        <div>
          <h2>Resident Dashboard</h2>
          <p className="text-muted">Raise and track your maintenance requests</p>
        </div>
        <Link to="/user/raise" className="btn btn-primary">
          <span>+</span> File New Complaint
        </Link>
      </div>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-title">Total Raised</div>
          <div className="stat-value">{stats.total}</div>
        </div>
        <div className="stat-card stat-glow-pending">
          <div className="stat-title">Pending Tickets</div>
          <div className="stat-value">{stats.open}</div>
        </div>
        <div className="stat-card stat-glow-resolved">
          <div className="stat-title">Resolved/Closed</div>
          <div className="stat-value">{stats.resolved}</div>
        </div>
      </div>

      <div className="glass-panel">
        <h3 className="mb-1">My Service Requests</h3>
        {loading ? (
          <p className="text-muted">Loading tickets...</p>
        ) : complaints.length === 0 ? (
          <div className="text-center" style={{ padding: '2rem' }}>
            <p className="text-muted mb-1">You haven't filed any complaints yet.</p>
            <Link to="/user/raise" className="btn btn-primary">
              Raise Your First Ticket
            </Link>
          </div>
        ) : (
          <div className="complaint-list">
            {complaints.map((complaint) => (
              <div 
                key={complaint.id} 
                className="glass-panel complaint-card" 
                onClick={() => navigate(`/complaint/${complaint.id}`)}
                style={{ background: 'rgba(255,255,255,0.01)', border: '1px solid rgba(255,255,255,0.05)' }}
              >
                <div className="complaint-card-header">
                  <div>
                    <h3>{complaint.title}</h3>
                    <span className="badge-open" style={{ fontSize: '0.75rem', fontWeight: 600, color: '#3b82f6', background: 'rgba(59,130,246,0.1)', padding: '0.1rem 0.5rem', borderRadius: '4px', marginTop: '0.25rem', display: 'inline-block' }}>
                      Category: {complaint.category}
                    </span>
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <span className={getPriorityBadgeClass(complaint.priority)}>{complaint.priority}</span>
                    <span className={getStatusBadgeClass(complaint.status)}>{complaint.status}</span>
                  </div>
                </div>
                
                <p className="complaint-card-desc">{complaint.description}</p>
                
                {complaint.aiSummary && (
                  <div style={{ background: 'rgba(16,185,129,0.04)', padding: '0.5rem 0.75rem', borderRadius: '8px', borderLeft: '3px solid #10b981', margin: '0.75rem 0', fontSize: '0.85rem' }}>
                    <span className="ai-badge" style={{ marginRight: '0.5rem' }}>AI Summary</span>
                    {complaint.aiSummary}
                  </div>
                )}

                <div className="complaint-card-meta">
                  <span>Filed on: {new Date(complaint.createdAt).toLocaleDateString()} at {new Date(complaint.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                  {complaint.assignedTo ? (
                    <span>Assigned to: <strong>{complaint.assignedTo.name}</strong></span>
                  ) : (
                    <span className="text-muted">Awaiting technician assignment</span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default UserDashboard;
