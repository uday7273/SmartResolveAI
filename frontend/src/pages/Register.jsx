import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Register = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState('USER');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name || !email || !password || !role) {
      setError('Please fill in all required fields.');
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }

    setIsSubmitting(true);
    setError('');
    setSuccess('');

    try {
      await register(name, email, password, phone, role);
      setSuccess('Registration successful! Redirecting to login page...');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message || 
        'Registration failed. Email might already be taken.'
      );
      setIsSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="glass-panel auth-card">
        <div className="auth-header">
          <h2>Create Account</h2>
          <p>Get started with the Service Request System</p>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="name">Full Name *</label>
            <input
              type="text"
              id="name"
              className="form-control"
              placeholder="John Doe"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="email">Email Address *</label>
            <input
              type="email"
              id="email"
              className="form-control"
              placeholder="john@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">Password * (Min 6 chars)</label>
            <input
              type="password"
              id="password"
              className="form-control"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="phone">Phone Number</label>
            <input
              type="tel"
              id="phone"
              className="form-control"
              placeholder="+1234567890"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="role">Register As *</label>
            <select
              id="role"
              className="form-control"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              required
            >
              <option value="USER">Resident / Customer (USER)</option>
              <option value="TECHNICIAN">Technician / Staff (TECHNICIAN)</option>
              <option value="ADMIN">System Administrator (ADMIN)</option>
            </select>
          </div>

          <button 
            type="submit" 
            className="btn btn-primary" 
            style={{ width: '100%', marginTop: '1rem' }}
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Registering...' : 'Register'}
          </button>
        </form>

        <div className="text-center" style={{ marginTop: '1.5rem', fontSize: '0.9rem' }}>
          <span className="text-muted">Already have an account? </span>
          <Link to="/login" style={{ color: '#3b82f6', textDecoration: 'none', fontWeight: 600 }}>
            Login here
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Register;
