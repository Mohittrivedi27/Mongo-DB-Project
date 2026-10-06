import React, { useState, useEffect } from 'react';
import api from '../services/api';

export default function Skills() {
  const [skills, setSkills] = useState([]);
  const [newSkill, setNewSkill] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const res = await api.get('/profile');
      setSkills(res.data.skills || []);
    } catch (err) {
      console.error(err);
    }
  };

  const handleAddSkill = (e) => {
    e.preventDefault();
    if (!newSkill.trim() || skills.includes(newSkill.trim())) return;
    const updated = [...skills, newSkill.trim()];
    setSkills(updated);
    setNewSkill('');
    saveSkills(updated);
  };

  const handleRemoveSkill = (skillToRemove) => {
    const updated = skills.filter((s) => s !== skillToRemove);
    setSkills(updated);
    saveSkills(updated);
  };

  const saveSkills = async (updatedSkills) => {
    setSaving(true);
    try {
      await api.put('/profile/skills', { skills: updatedSkills });
    } catch (err) {
      console.error('Failed to save skills', err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ maxWidth: '700px' }}>
      <div className="card">
        <h3 className="card-title" style={{ marginBottom: '0.5rem' }}>Technical & Professional Skills</h3>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
          Add your key technical skills (e.g., Java, Spring Boot, MongoDB, React, REST API) to calculate matching scores against job descriptions.
        </p>

        <form onSubmit={handleAddSkill} style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem' }}>
          <input
            type="text"
            className="form-control"
            placeholder="e.g. Java, Spring Boot, React.js"
            value={newSkill}
            onChange={(e) => setNewSkill(e.target.value)}
          />
          <button type="submit" className="btn btn-primary" disabled={saving}>
            Add Skill
          </button>
        </form>

        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
          {skills.map((skill) => (
            <div
              key={skill}
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '0.5rem',
                backgroundColor: 'var(--primary-light)',
                color: 'var(--primary-color)',
                padding: '0.4rem 0.8rem',
                borderRadius: '9999px',
                fontWeight: 600,
                fontSize: '0.875rem'
              }}
            >
              <span>{skill}</span>
              <button
                type="button"
                onClick={() => handleRemoveSkill(skill)}
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

          {skills.length === 0 && (
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>No skills added yet.</p>
          )}
        </div>
      </div>
    </div>
  );
}
