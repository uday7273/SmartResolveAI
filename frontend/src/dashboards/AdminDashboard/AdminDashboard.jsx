import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const AdminDashboard = () => {
  const [complaints, setComplaints] = useState([]);
  const [stats, setStats] = useState(null);
  const [sortBy, setSortBy] = useState('createdAt');
  const [direction, setDirection] = useState('desc');
  const [usePriorityQueue, setUsePriorityQueue] = useState(false);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchStats = async () => {
    try {
      const response = await api.get('/api/admin/dashboard');
      setStats(response.data);
    } catch (err) {
      console.error('Failed to load dashboard stats', err);
    }
  };

  const fetchComplaints = async () => {
    setLoading(true);
    try {
      let response;
      if (usePriorityQueue) {
        response = await api.get('/api/admin/complaints/prioritized');
      } else {
        response = await api.get('/api/admin/complaints', {
          params: { sortBy, direction }
        });
      }
      setComplaints(response.data);
    } catch (err) {
      console.error('Failed to load complaints', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  useEffect(() => {
    fetchComplaints();
  }, [sortBy, direction, usePriorityQueue]);

  const handleSortChange = (e) => {
    setSortBy(e.target.value);
    setUsePriorityQueue(false);
  };

  const handleDirectionToggle = () => {
    setDirection(prev => prev === 'asc' ? 'desc' : 'asc');
    setUsePriorityQueue(false);
  };

  const handlePqToggle = () => {
    setUsePriorityQueue(prev => !prev);
  };

  const getPriorityBadgeClass = (priority) => {
    return `badge badge-${priority.toLowerCase()}`;
  };

  const getStatusBadgeClass = (status) => {
    return `badge badge-${status.toLowerCase()}`;
  };

  return (
    <div>
      <div className="mb-2">
        <h2>Admin Command Center</h2>
        <p className="text-muted">Monitor system stats, review AI classifications, and dispatch technicians</p>
      </div>

      {/* Stats Summary Panel */}
      {stats && (
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-title">Total Tickets</div>
            <div className="stat-value">{stats.totalComplaints}</div>
          </div>
          <div className="stat-card stat-glow-pending">
            <div className="stat-title">Unresolved</div>
            <div className="stat-value">{stats.pendingComplaints}</div>
          </div>
          <div className="stat-card stat-glow-resolved">
            <div className="stat-title">Avg Resolution</div>
            <div className="stat-value">{stats.averageResolutionTimeInHours} hr</div>
          </div>
          <div className="stat-card stat-glow-critical">
            <div className="stat-title">Critical Tickets</div>
            <div className="stat-value" style={{ color: '#ef4444' }}>{stats.criticalComplaints}</div>
          </div>
        </div>
      )}

      {/* Visual Distribution Charts */}
      {stats && (
        <div className="charts-grid mb-2">
          {/* Categories bar chart */}
          <div className="glass-panel">
            <h3 className="mb-1">Complaints by Category</h3>
            <div className="bar-chart-container" style={{ marginTop: '1rem' }}>
              {Object.entries(stats.complaintsByCategory).map(([category, count]) => {
                const percentage = stats.totalComplaints > 0 
                  ? (count / stats.totalComplaints) * 100 
                  : 0;
                return (
                  <div key={category} className="chart-bar-row">
                    <div className="chart-bar-label">
                      <span>{category}</span>
                      <strong>{count}</strong>
                    </div>
                    <div className="chart-bar-outer">
                      <div className="chart-bar-inner" style={{ width: `${percentage}%` }}></div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Priorities bar chart */}
          <div className="glass-panel">
            <h3 className="mb-1">Complaints by Priority</h3>
            <div className="bar-chart-container" style={{ marginTop: '1rem' }}>
              {Object.entries(stats.complaintsByPriority).map(([priority, count]) => {
                const percentage = stats.totalComplaints > 0 
                  ? (count / stats.totalComplaints) * 100 
                  : 0;
                let color = '#3b82f6';
                if (priority === 'CRITICAL') color = '#ef4444';
                else if (priority === 'HIGH') color = '#f59e0b';
                else if (priority === 'LOW') color = '#6b7280';
                
                return (
                  <div key={priority} className="chart-bar-row">
                    <div className="chart-bar-label">
                      <span>{priority}</span>
                      <strong>{count}</strong>
                    </div>
                    <div className="chart-bar-outer">
                      <div 
                        className="chart-bar-inner" 
                        style={{ width: `${percentage}%`, background: color }}
                      ></div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/* Complaints List and Filters */}
      <div className="glass-panel">
        <div className="flex-between mb-1" style={{ flexWrap: 'wrap', gap: '1rem' }}>
          <h3>All Service Tickets</h3>
          
          <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
            {/* Toggle DSA Priority Queue */}
            <button 
              onClick={handlePqToggle} 
              className={`btn ${usePriorityQueue ? 'btn-primary' : 'btn-secondary'}`}
              style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}
            >
              ⚡ Priority Queue (DSA)
            </button>

            {!usePriorityQueue && (
              <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                <span className="text-muted" style={{ fontSize: '0.85rem' }}>Sort By:</span>
                <select 
                  className="form-control" 
                  value={sortBy} 
                  onChange={handleSortChange}
                  style={{ width: '130px', padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}
                >
                  <option value="createdAt">Created Date</option>
                  <option value="priority">Priority</option>
                  <option value="status">Status</option>
                  <option value="updatedDate">Updated Date</option>
                </select>
                <button 
                  onClick={handleDirectionToggle} 
                  className="btn btn-secondary"
                  style={{ padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}
                >
                  {direction === 'asc' ? '▲' : '▼'}
                </button>
              </div>
            )}
          </div>
        </div>

        {usePriorityQueue && (
          <div style={{ background: 'rgba(124, 58, 237, 0.08)', padding: '0.75rem 1rem', borderRadius: '8px', borderLeft: '4px solid #7c3aed', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
            <strong>DSA PriorityQueue Mode Enabled:</strong> Tickets are currently ordered in memory using a custom Java Comparator. 
            Prioritization follows: <strong>CRITICAL &gt; HIGH &gt; MEDIUM &gt; LOW</strong>, with ties broken by older registration timestamp first.
          </div>
        )}

        {loading ? (
          <p className="text-muted">Loading tickets...</p>
        ) : complaints.length === 0 ? (
          <p className="text-muted">No tickets found in the system.</p>
        ) : (
          <div className="custom-table-container">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>Ticket</th>
                  <th>Category</th>
                  <th>Priority</th>
                  <th>Status</th>
                  <th>Created By</th>
                  <th>Assigned Tech</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {complaints.map((complaint) => (
                  <tr 
                    key={complaint.id} 
                    className="clickable"
                    onClick={() => navigate(`/complaint/${complaint.id}`)}
                  >
                    <td>
                      <div style={{ fontWeight: 600 }}>{complaint.title}</div>
                      <div className="text-muted" style={{ fontSize: '0.8rem', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', maxWidth: '300px' }}>
                        {complaint.description}
                      </div>
                    </td>
                    <td>
                      <span className="badge-open" style={{ fontSize: '0.75rem', fontWeight: 600, color: '#3b82f6', background: 'rgba(59,130,246,0.1)', padding: '0.2rem 0.5rem', borderRadius: '4px' }}>
                        {complaint.category}
                      </span>
                    </td>
                    <td>
                      <span className={getPriorityBadgeClass(complaint.priority)}>{complaint.priority}</span>
                    </td>
                    <td>
                      <span className={getStatusBadgeClass(complaint.status)}>{complaint.status}</span>
                    </td>
                    <td>{complaint.createdBy.name}</td>
                    <td>
                      {complaint.assignedTo ? (
                        <span style={{ fontWeight: 500 }}>{complaint.assignedTo.name}</span>
                      ) : (
                        <span className="text-muted" style={{ fontStyle: 'italic' }}>Unassigned</span>
                      )}
                    </td>
                    <td>{new Date(complaint.createdAt).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;
