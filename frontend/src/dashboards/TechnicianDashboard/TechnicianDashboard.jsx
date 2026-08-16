import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const TechnicianDashboard = () => {
  const [complaints, setComplaints] = useState([]);
  const [stats, setStats] = useState({ total: 0, inProgress: 0, resolved: 0 });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchAssignedComplaints = async () => {
      try {
        const response = await api.get('/api/technician/complaints');
        setComplaints(response.data);
        
        // Calculate stats
        const total = response.data.length;
        const inProgress = response.data.filter(c => c.status === 'IN_PROGRESS').length;
        const resolved = response.data.filter(c => c.status === 'RESOLVED' || c.status === 'CLOSED').length;
        
        setStats({ total, inProgress, resolved });
      } catch (err) {
        console.error('Failed to load technician complaints', err);
      } finally {
        setLoading(false);
      }
    };

    fetchAssignedComplaints();
  }, []);

  const getPriorityBadgeClass = (priority) => {
    return `badge badge-${priority.toLowerCase()}`;
  };

  const getStatusBadgeClass = (status) => {
    return `badge badge-${status.toLowerCase()}`;
  };

  return (
    <div>
      <div className="mb-2">
        <h2>Technician Portal</h2>
        <p className="text-muted">Review, diagnose, and resolve your assigned service requests</p>
      </div>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-title">Total Assigned</div>
          <div className="stat-value">{stats.total}</div>
        </div>
        <div className="stat-card stat-glow-pending">
          <div className="stat-title">Active (In Progress)</div>
          <div className="stat-value">{stats.inProgress}</div>
        </div>
        <div className="stat-card stat-glow-resolved">
          <div className="stat-title">Completed (Resolved)</div>
          <div className="stat-value">{stats.resolved}</div>
        </div>
      </div>

      <div className="glass-panel">
        <h3 className="mb-1">My Task List</h3>
        {loading ? (
          <p className="text-muted">Loading assigned tickets...</p>
        ) : complaints.length === 0 ? (
          <div className="text-center" style={{ padding: '2rem' }}>
            <p className="text-muted">No complaints are currently assigned to you.</p>
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
                    <span className="badge-open" style={{ fontSize: '0.75rem', fontWeight: 600, color: '#a78bfa', background: 'rgba(124,58,237,0.1)', padding: '0.1rem 0.5rem', borderRadius: '4px', marginTop: '0.25rem', display: 'inline-block' }}>
                      Category: {complaint.category}
                    </span>
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <span className={getPriorityBadgeClass(complaint.priority)}>{complaint.priority}</span>
                    <span className={getStatusBadgeClass(complaint.status)}>{complaint.status}</span>
                  </div>
                </div>
                
                <p className="complaint-card-desc">{complaint.description}</p>
                
                {complaint.aiSuggestedResponse && (
                  <div style={{ background: 'rgba(59,130,246,0.04)', padding: '0.5rem 0.75rem', borderRadius: '8px', borderLeft: '3px solid #3b82f6', margin: '0.75rem 0', fontSize: '0.85rem' }}>
                    <span className="ai-badge" style={{ marginRight: '0.5rem', background: 'linear-gradient(135deg, #3b82f6, #8b5cf6)' }}>AI Suggested response</span>
                    {complaint.aiSuggestedResponse}
                  </div>
                )}

                <div className="complaint-card-meta">
                  <span>Assigned on: {new Date(complaint.updatedAt).toLocaleDateString()}</span>
                  <span>Raised by: <strong>{complaint.createdBy.name}</strong></span>
                  <span>Phone: {complaint.createdBy.phone || 'N/A'}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default TechnicianDashboard;
