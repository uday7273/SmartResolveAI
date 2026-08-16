import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import api from '../api/axiosConfig';

const ComplaintDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [complaint, setComplaint] = useState(null);
  const [comments, setComments] = useState([]);
  const [history, setHistory] = useState([]);
  const [technicians, setTechnicians] = useState([]);
  
  const [newComment, setNewComment] = useState('');
  const [selectedTech, setSelectedTech] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchComplaintDetails = async () => {
    try {
      const compRes = await api.get(`/api/complaints/${id}`);
      setComplaint(compRes.data);
      
      const commRes = await api.get(`/api/complaints/${id}/comments`);
      setComments(commRes.data);

      const histRes = await api.get(`/api/complaints/${id}/history`);
      setHistory(histRes.data);

      if (compRes.data.assignedTo) {
        setSelectedTech(compRes.data.assignedTo.id);
      }
    } catch (err) {
      console.error(err);
      setError('Failed to fetch complaint details or access denied.');
    } finally {
      setLoading(false);
    }
  };

  const fetchTechnicians = async () => {
    if (user && user.role === 'ADMIN') {
      try {
        const res = await api.get('/api/admin/technicians');
        setTechnicians(res.data);
      } catch (err) {
        console.error('Failed to load technicians list', err);
      }
    }
  };

  useEffect(() => {
    fetchComplaintDetails();
    fetchTechnicians();
  }, [id, user]);

  const handleStatusChange = async (newStatus) => {
    setError('');
    setSuccess('');
    try {
      const res = await api.put(`/api/complaints/${id}`, null, {
        params: { status: newStatus }
      });
      setComplaint(res.data);
      setSuccess(`Ticket status updated to ${newStatus}`);
      
      // Refresh history and comments
      const histRes = await api.get(`/api/complaints/${id}/history`);
      setHistory(histRes.data);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to update status.');
    }
  };

  const handleAssignTechnician = async () => {
    if (!selectedTech) return;
    setError('');
    setSuccess('');
    try {
      const res = await api.put(`/api/admin/complaints/${id}/assign`, null, {
        params: { technicianId: selectedTech }
      });
      setComplaint(res.data);
      setSuccess(`Ticket successfully assigned to ${res.data.assignedTo.name}`);
      
      const histRes = await api.get(`/api/complaints/${id}/history`);
      setHistory(histRes.data);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to assign technician.');
    }
  };

  const handlePriorityOverride = async (newPrio) => {
    setError('');
    setSuccess('');
    try {
      const res = await api.put(`/api/admin/complaints/${id}/priority`, null, {
        params: { priority: newPrio }
      });
      setComplaint(res.data);
      setSuccess(`Priority changed to ${newPrio}`);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to change priority.');
    }
  };

  const handleCategoryOverride = async (newCat) => {
    setError('');
    setSuccess('');
    try {
      const res = await api.put(`/api/admin/complaints/${id}/category`, null, {
        params: { category: newCat }
      });
      setComplaint(res.data);
      setSuccess(`Category changed to ${newCat}`);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to change category.');
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    setError('');
    try {
      const res = await api.post(`/api/complaints/${id}/comments`, { comment: newComment });
      setComments(prev => [...prev, res.data]);
      setNewComment('');
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to post comment.');
    }
  };

  const getPriorityBadgeClass = (priority) => {
    return `badge badge-${priority.toLowerCase()}`;
  };

  const getStatusBadgeClass = (status) => {
    return `badge badge-${status.toLowerCase()}`;
  };

  if (loading) {
    return <div className="text-center" style={{ padding: '4rem' }}><p className="text-muted">Loading request details...</p></div>;
  }

  if (error && !complaint) {
    return (
      <div className="glass-panel text-center" style={{ margin: '4rem auto', maxWidth: '600px' }}>
        <div className="alert alert-danger">{error}</div>
        <button onClick={() => navigate(-1)} className="btn btn-secondary">Go Back</button>
      </div>
    );
  }

  return (
    <div>
      <div className="flex-between mb-2">
        <button onClick={() => navigate(-1)} className="btn btn-secondary">
          ← Back
        </button>
        <div>
          <span className="text-muted" style={{ marginRight: '1rem' }}>Ticket ID: #{complaint.id}</span>
          <span className={getStatusBadgeClass(complaint.status)}>{complaint.status}</span>
        </div>
      </div>

      {success && <div className="alert alert-success">{success}</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="complaint-details-layout">
        {/* Main Details Panel */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          <div className="glass-panel">
            <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.5rem' }}>
              <div>
                <h2>{complaint.title}</h2>
                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem', flexWrap: 'wrap' }}>
                  <span className="badge-open" style={{ color: '#3b82f6', background: 'rgba(59,130,246,0.1)', padding: '0.2rem 0.5rem', borderRadius: '4px', fontSize: '0.8rem', fontWeight: 600 }}>
                    Category: {complaint.category}
                  </span>
                  <span className={getPriorityBadgeClass(complaint.priority)}>
                    Priority: {complaint.priority}
                  </span>
                </div>
              </div>
              <div className="text-right" style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                <div>Raised By: <strong>{complaint.createdBy.name}</strong></div>
                <div>Email: {complaint.createdBy.email}</div>
                <div>Date: {new Date(complaint.createdAt).toLocaleString()}</div>
              </div>
            </div>

            <div style={{ background: 'rgba(255,255,255,0.02)', padding: '1.5rem', borderRadius: '12px', border: '1px solid var(--border-color)', minHeight: '150px', whiteSpace: 'pre-wrap' }}>
              {complaint.description}
            </div>

            {/* AI Analysis Result */}
            {(complaint.aiSummary || complaint.aiSuggestedResponse) && (
              <div style={{ marginTop: '2rem', background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.08), rgba(59, 130, 246, 0.08))', border: '1px solid rgba(16, 185, 129, 0.2)', padding: '1.5rem', borderRadius: '14px' }}>
                <div className="flex-align mb-1">
                  <span className="ai-badge">AI ANALYZER INSIGHTS</span>
                  <span style={{ fontSize: '0.8rem', color: '#10b981', fontWeight: 600 }}>Active Auto-Classification</span>
                </div>
                {complaint.aiSummary && (
                  <div style={{ marginBottom: '1rem' }}>
                    <div style={{ fontSize: '0.8rem', fontWeight: 700, color: '#34d399', textTransform: 'uppercase', marginBottom: '0.25rem' }}>Auto-Generated Summary</div>
                    <div style={{ fontSize: '0.95rem' }}>{complaint.aiSummary}</div>
                  </div>
                )}
                {complaint.aiSuggestedResponse && (
                  <div>
                    <div style={{ fontSize: '0.8rem', fontWeight: 700, color: '#3b82f6', textTransform: 'uppercase', marginBottom: '0.25rem' }}>Suggested Action / Diagnostic Response</div>
                    <div style={{ fontSize: '0.95rem', fontStyle: 'italic', background: 'rgba(0,0,0,0.2)', padding: '0.75rem 1rem', borderRadius: '8px' }}>
                      {complaint.aiSuggestedResponse}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Comments Section */}
          <div className="glass-panel">
            <h3 className="mb-1">Discussion Thread</h3>
            <div className="comments-container">
              <div className="comments-list">
                {comments.length === 0 ? (
                  <p className="text-muted" style={{ padding: '1rem 0' }}>No comments posted yet. Start the discussion below.</p>
                ) : (
                  comments.map(c => (
                    <div key={c.id} className="comment-bubble">
                      <div className="comment-author-meta">
                        <span className="comment-author-name">{c.user.name} ({c.user.role})</span>
                        <span>{new Date(c.createdAt).toLocaleString()}</span>
                      </div>
                      <div style={{ fontSize: '0.95rem' }}>{c.comment}</div>
                    </div>
                  ))
                )}
              </div>

              {complaint.status !== 'CLOSED' ? (
                <form onSubmit={handleAddComment} style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1.5rem', display: 'flex', gap: '0.75rem' }}>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Type a message or updates..."
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    required
                  />
                  <button type="submit" className="btn btn-primary" style={{ padding: '0 1.5rem' }}>
                    Send
                  </button>
                </form>
              ) : (
                <p className="text-muted text-center" style={{ fontSize: '0.85rem', background: 'rgba(255,255,255,0.02)', padding: '0.5rem', borderRadius: '6px' }}>
                  Discussion closed. This ticket is CLOSED.
                </p>
              )}
            </div>
          </div>
        </div>

        {/* Sidebar Action Panel */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          {/* Quick Actions based on Role */}
          <div className="glass-panel">
            <h3 className="mb-1">Status Controls</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem' }}>
              
              {/* USER Controls */}
              {user.role === 'USER' && (
                <>
                  {complaint.status === 'RESOLVED' ? (
                    <button 
                      onClick={() => handleStatusChange('CLOSED')} 
                      className="btn btn-primary"
                      style={{ width: '100%' }}
                    >
                      ✔ Close Request (Mark Solved)
                    </button>
                  ) : complaint.status === 'CLOSED' ? (
                    <p className="text-success" style={{ fontWeight: 600, fontSize: '0.9rem' }}>🎉 Request is successfully closed.</p>
                  ) : (
                    <p className="text-muted" style={{ fontSize: '0.9rem' }}>
                      Awaiting technician to inspect and resolve your issue. You will be notified immediately when progress is recorded.
                    </p>
                  )}
                </>
              )}

              {/* TECHNICIAN Controls */}
              {user.role === 'TECHNICIAN' && (
                <>
                  {complaint.status === 'ASSIGNED' && (
                    <button 
                      onClick={() => handleStatusChange('IN_PROGRESS')} 
                      className="btn btn-primary"
                      style={{ width: '100%' }}
                    >
                      Accept & Start Investigation
                    </button>
                  )}
                  {complaint.status === 'IN_PROGRESS' && (
                    <button 
                      onClick={() => handleStatusChange('RESOLVED')} 
                      className="btn btn-primary"
                      style={{ width: '100%', background: 'linear-gradient(135deg, #10b981, #059669)' }}
                    >
                      ✔ Mark As Resolved
                    </button>
                  )}
                  {complaint.status === 'RESOLVED' && (
                    <p className="text-muted" style={{ fontSize: '0.9rem' }}>
                      You have resolved this issue. Waiting for user closure check.
                    </p>
                  )}
                  {complaint.status === 'CLOSED' && (
                    <p className="text-muted" style={{ fontSize: '0.9rem' }}>
                      This request has been verified and closed by the resident.
                    </p>
                  )}
                </>
              )}

              {/* ADMIN Controls */}
              {user.role === 'ADMIN' && (
                <>
                  <div className="form-group" style={{ marginBottom: '0.5rem' }}>
                    <label className="form-label">Assign Technician</label>
                    <select
                      className="form-control"
                      value={selectedTech}
                      onChange={(e) => setSelectedTech(e.target.value)}
                    >
                      <option value="">-- Select Staff --</option>
                      {technicians.map(t => (
                        <option key={t.id} value={t.id}>{t.name}</option>
                      ))}
                    </select>
                    <button 
                      onClick={handleAssignTechnician} 
                      className="btn btn-secondary" 
                      style={{ width: '100%', marginTop: '0.5rem', padding: '0.5rem' }}
                      disabled={!selectedTech}
                    >
                      Assign Staff
                    </button>
                  </div>

                  <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1rem', display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                    <div className="form-group">
                      <label className="form-label">Override Category</label>
                      <select
                        className="form-control"
                        value={complaint.category}
                        onChange={(e) => handleCategoryOverride(e.target.value)}
                      >
                        <option value="NETWORK">NETWORK</option>
                        <option value="ELECTRICITY">ELECTRICITY</option>
                        <option value="PLUMBING">PLUMBING</option>
                        <option value="CLEANING">CLEANING</option>
                        <option value="MAINTENANCE">MAINTENANCE</option>
                        <option value="COMPUTER">COMPUTER</option>
                        <option value="SECURITY">SECURITY</option>
                        <option value="OTHER">OTHER</option>
                      </select>
                    </div>

                    <div className="form-group">
                      <label className="form-label">Override Priority</label>
                      <select
                        className="form-control"
                        value={complaint.priority}
                        onChange={(e) => handlePriorityOverride(e.target.value)}
                      >
                        <option value="LOW">LOW</option>
                        <option value="MEDIUM">MEDIUM</option>
                        <option value="HIGH">HIGH</option>
                        <option value="CRITICAL">CRITICAL</option>
                      </select>
                    </div>

                    <div className="form-group">
                      <label className="form-label">Force Status Transition</label>
                      <select
                        className="form-control"
                        value={complaint.status}
                        onChange={(e) => handleStatusChange(e.target.value)}
                      >
                        <option value="OPEN">OPEN</option>
                        <option value="ASSIGNED">ASSIGNED</option>
                        <option value="IN_PROGRESS">IN_PROGRESS</option>
                        <option value="RESOLVED">RESOLVED</option>
                        <option value="CLOSED">CLOSED</option>
                        <option value="REJECTED">REJECTED</option>
                      </select>
                    </div>
                  </div>
                </>
              )}
            </div>
          </div>

          {/* Audit History Timeline */}
          <div className="glass-panel">
            <h3 className="mb-1">Audit Trail</h3>
            <div className="timeline">
              {history.map(h => (
                <div key={h.id} className="timeline-item">
                  <div className="timeline-dot"></div>
                  <div className="timeline-meta">
                    {new Date(h.changedAt).toLocaleDateString()} at {new Date(h.changedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </div>
                  <div className="timeline-body">
                    Status changed to <strong style={{ color: '#3b82f6' }}>{h.newStatus}</strong> by <strong>{h.changedBy.name}</strong>
                    {h.oldStatus && (
                      <span className="text-muted" style={{ display: 'block', fontSize: '0.8rem' }}>
                        (Transitioned from {h.oldStatus})
                      </span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default ComplaintDetails;
