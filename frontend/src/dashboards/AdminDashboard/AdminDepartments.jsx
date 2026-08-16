import React, { useEffect, useState } from 'react';
import api from '../../api/axiosConfig';

const AdminDepartments = () => {
  const [departments, setDepartments] = useState([]);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchDepartments = async () => {
    try {
      const res = await api.get('/api/departments');
      setDepartments(res.data);
    } catch (err) {
      console.error(err);
      setError('Failed to fetch departments.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDepartments();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name.trim()) {
      setError('Department name is required.');
      return;
    }

    setError('');
    setSuccess('');

    try {
      if (editingId) {
        // Update
        const res = await api.put(`/api/departments/${editingId}`, { name, description });
        setSuccess('Department updated successfully.');
        setDepartments(prev => prev.map(d => d.id === editingId ? res.data : d));
        setEditingId(null);
      } else {
        // Create
        const res = await api.post('/api/departments', { name, description });
        setSuccess('Department created successfully.');
        setDepartments(prev => [...prev, res.data]);
      }
      setName('');
      setDescription('');
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Action failed.');
    }
  };

  const handleEdit = (dept) => {
    setEditingId(dept.id);
    setName(dept.name);
    setDescription(dept.description || '');
    setError('');
    setSuccess('');
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setName('');
    setDescription('');
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this department?')) return;
    
    setError('');
    setSuccess('');

    try {
      await api.delete(`/api/departments/${id}`);
      setSuccess('Department deleted successfully.');
      setDepartments(prev => prev.filter(d => d.id !== id));
    } catch (err) {
      console.error(err);
      setError('Failed to delete department. It may be referenced elsewhere.');
    }
  };

  return (
    <div className="dashboard-grid">
      {/* List */}
      <div className="glass-panel">
        <h3 className="mb-1">Active Departments</h3>
        {loading ? (
          <p className="text-muted">Loading departments...</p>
        ) : departments.length === 0 ? (
          <p className="text-muted">No departments created yet.</p>
        ) : (
          <div className="custom-table-container">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Description</th>
                  <th className="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                {departments.map((dept) => (
                  <tr key={dept.id}>
                    <td>{dept.id}</td>
                    <td><strong>{dept.name}</strong></td>
                    <td className="text-muted" style={{ fontSize: '0.9rem' }}>{dept.description || 'N/A'}</td>
                    <td className="text-center">
                      <div style={{ display: 'inline-flex', gap: '0.5rem' }}>
                        <button 
                          onClick={() => handleEdit(dept)} 
                          className="btn btn-secondary"
                          style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                        >
                          Edit
                        </button>
                        <button 
                          onClick={() => handleDelete(dept.id)} 
                          className="btn btn-danger"
                          style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Editor Form */}
      <div className="glass-panel">
        <h3>{editingId ? 'Edit Department' : 'Create Department'}</h3>
        <p className="text-muted mb-1" style={{ fontSize: '0.85rem' }}>
          {editingId ? 'Modify department settings' : 'Add a new department category to the platform'}
        </p>

        {error && <div className="alert alert-danger" style={{ padding: '0.5rem 0.75rem', fontSize: '0.85rem' }}>{error}</div>}
        {success && <div className="alert alert-success" style={{ padding: '0.5rem 0.75rem', fontSize: '0.85rem' }}>{success}</div>}

        <form onSubmit={handleSubmit} style={{ marginTop: '1rem' }}>
          <div className="form-group">
            <label className="form-label" htmlFor="deptName">Department Name *</label>
            <input
              type="text"
              id="deptName"
              className="form-control"
              placeholder="e.g. Electrical Department"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="deptDesc">Description</label>
            <textarea
              id="deptDesc"
              rows="4"
              className="form-control"
              placeholder="Provide a brief explanation of what this department manages."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            ></textarea>
          </div>

          <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem' }}>
            <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
              {editingId ? 'Save Changes' : 'Create'}
            </button>
            {editingId && (
              <button type="button" className="btn btn-secondary" onClick={handleCancelEdit}>
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
};

export default AdminDepartments;
