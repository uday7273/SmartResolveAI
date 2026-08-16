import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';

const RaiseComplaint = () => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim() || !description.trim()) {
      setError('Please fill in both title and description.');
      return;
    }

    setIsSubmitting(true);
    setError('');

    try {
      const response = await api.post('/api/complaints', { title, description });
      const createdComplaint = response.data;
      navigate(`/complaint/${createdComplaint.id}`);
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message || 
        'Failed to raise complaint. Please try again.'
      );
      setIsSubmitting(false);
    }
  };

  return (
    <div style={{ maxWidth: '700px', margin: '0 auto' }}>
      <div className="mb-2">
        <h2>File Service Request</h2>
        <p className="text-muted">Explain your issue. Our AI-driven router will instantly prioritize and assign it.</p>
      </div>

      <div className="glass-panel">
        {error && <div className="alert alert-danger">{error}</div>}

        {isSubmitting ? (
          <div className="text-center" style={{ padding: '3rem 1rem' }}>
            <div style={{
              display: 'inline-block',
              width: '50px',
              height: '50px',
              border: '4px solid rgba(255,255,255,0.1)',
              borderTop: '4px solid #3b82f6',
              borderRadius: '50%',
              animation: 'spin 1s linear infinite',
              marginBottom: '1.5rem'
            }}></div>
            <h3 style={{ marginBottom: '0.5rem' }}>AI Complaint Router Processing...</h3>
            <p className="text-muted">We are analyzing keywords, determining priority severity, selecting the ideal department, and drafting recommended responses.</p>
            
            <style>{`
              @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
              }
            `}</style>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label" htmlFor="title">Title *</label>
              <input
                type="text"
                id="title"
                className="form-control"
                placeholder="e.g. Wi-Fi connection down in Room 102"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="description">Detailed Description *</label>
              <textarea
                id="description"
                rows="6"
                className="form-control"
                placeholder="Describe your issue in detail. Add specific keywords (e.g. water, leak, wire, router, socket) to help our auto-routing system classify and assign it instantly."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                required
              ></textarea>
            </div>

            <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
              <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                Submit Request
              </button>
              <button 
                type="button" 
                className="btn btn-secondary" 
                onClick={() => navigate('/user')}
              >
                Cancel
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default RaiseComplaint;
