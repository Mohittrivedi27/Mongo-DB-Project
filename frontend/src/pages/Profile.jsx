import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';

export default function Profile() {
  const [profile, setProfile] = useState({
    name: '',
    email: '',
    phone: '',
    location: '',
    portfolioUrl: '',
    githubUrl: '',
    linkedinUrl: '',
    education: [],
    experience: []
  });
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const res = await api.get('/profile');
      setProfile(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleChange = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMessage('');
    try {
      await api.put('/profile', profile);
      setMessage('Profile updated successfully!');
    } catch (err) {
      setMessage('Failed to update profile.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ maxWidth: '800px' }}>
      <div className="card">
        <h3 className="card-title" style={{ marginBottom: '1.5rem' }}>Personal Information</h3>

        {message && (
          <div style={{
            padding: '0.75rem 1rem',
            backgroundColor: message.includes('success') ? 'var(--success-light)' : 'var(--danger-light)',
            color: message.includes('success') ? 'var(--success-color)' : 'var(--danger-color)',
            borderRadius: 'var(--border-radius)',
            marginBottom: '1rem',
            fontSize: '0.875rem'
          }}>
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input
                type="text"
                name="name"
                className="form-control"
                value={profile.name || ''}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
                <label className="form-label" style={{ marginBottom: 0 }}>Email Address</label>
                {profile.emailVerified ? (
                  <span className="badge badge-success" style={{ fontSize: '0.75rem' }}>Verified ✓</span>
                ) : (
                  <Link to="/verify-email" className="badge badge-warning" style={{ fontSize: '0.75rem', textDecoration: 'none' }}>
                    Unverified (Verify Now) ⚠️
                  </Link>
                )}
              </div>
              <input
                type="email"
                name="email"
                className="form-control"
                value={profile.email || ''}
                disabled
              />
            </div>
            <div className="form-group">
              <label className="form-label">Phone Number</label>
              <input
                type="text"
                name="phone"
                className="form-control"
                value={profile.phone || ''}
                onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Location</label>
              <input
                type="text"
                name="location"
                className="form-control"
                value={profile.location || ''}
                onChange={handleChange}
              />
            </div>
          </div>

          <h4 style={{ margin: '1.5rem 0 1rem 0', fontSize: '1rem' }}>Social & Portfolio Links</h4>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label className="form-label">Portfolio URL</label>
              <input
                type="url"
                name="portfolioUrl"
                className="form-control"
                value={profile.portfolioUrl || ''}
                onChange={handleChange}
                placeholder="https://myportfolio.dev"
              />
            </div>
            <div className="form-group">
              <label className="form-label">GitHub URL</label>
              <input
                type="url"
                name="githubUrl"
                className="form-control"
                value={profile.githubUrl || ''}
                onChange={handleChange}
                placeholder="https://github.com/username"
              />
            </div>
            <div className="form-group">
              <label className="form-label">LinkedIn URL</label>
              <input
                type="url"
                name="linkedinUrl"
                className="form-control"
                value={profile.linkedinUrl || ''}
                onChange={handleChange}
                placeholder="https://linkedin.com/in/username"
              />
            </div>
          </div>

          <button type="submit" className="btn btn-primary" style={{ marginTop: '1rem' }} disabled={saving}>
            {saving ? 'Saving...' : 'Save Profile Changes'}
          </button>
        </form>
      </div>
    </div>
  );
}
