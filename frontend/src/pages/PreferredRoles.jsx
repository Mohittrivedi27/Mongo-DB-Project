import React, { useState, useEffect } from 'react';
import api from '../services/api';

export default function PreferredRoles() {
  const [preferredRoles, setPreferredRoles] = useState([]);
  const [newRole, setNewRole] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const res = await api.get('/profile');
      setPreferredRoles(res.data.preferredRoles || []);
    } catch (err) {
      console.error(err);
    }
  };

  const handleAddRole = (e) => {
    e.preventDefault();
    if (!newRole.trim() || preferredRoles.includes(newRole.trim())) return;
    const updated = [...preferredRoles, newRole.trim()];
    setPreferredRoles(updated);
    setNewRole('');
    saveRoles(updated);
  };

  const handleRemoveRole = (roleToRemove) => {
    const updated = preferredRoles.filter((r) => r !== roleToRemove);
    setPreferredRoles(updated);
    saveRoles(updated);
  };

  const saveRoles = async (updatedRoles) => {
    setSaving(true);
    try {
      await api.put('/profile/roles', { preferredRoles: updatedRoles });
    } catch (err) {
      console.error('Failed to save roles', err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ maxWidth: '700px' }}>
      <div className="card">
        <h3 className="card-title" style={{ marginBottom: '0.5rem' }}>Preferred Job Roles</h3>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
          Define target job titles (e.g. Java Backend Developer, Full Stack Developer, Android Developer) to automatically map appropriate domain resumes.
        </p>

        <form onSubmit={handleAddRole} style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem' }}>
          <input
            type="text"
            className="form-control"
            placeholder="e.g. Java Backend Developer"
            value={newRole}
            onChange={(e) => setNewRole(e.target.value)}
          />
          <button type="submit" className="btn btn-primary" disabled={saving}>
            Add Role
          </button>
        </form>

        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
          {preferredRoles.map((role) => (
            <div
              key={role}
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '0.5rem',
                backgroundColor: 'var(--info-light)',
                color: 'var(--info-color)',
                padding: '0.4rem 0.8rem',
                borderRadius: '9999px',
                fontWeight: 600,
                fontSize: '0.875rem'
              }}
            >
              <span>{role}</span>
              <button
                type="button"
                onClick={() => handleRemoveRole(role)}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'var(--danger-color)',
                  cursor: 'pointer',
                  fontSize: '1rem',
                  lineHeight: 1
                }}
              >
                &times;
              </button>
            </div>
          ))}

          {preferredRoles.length === 0 && (
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>No preferred roles added yet.</p>
          )}
        </div>
      </div>
    </div>
  );
}
