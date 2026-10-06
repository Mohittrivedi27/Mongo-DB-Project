import React, { useState, useEffect } from 'react';
import api from '../services/api';

export default function Settings() {
  const [settings, setSettings] = useState({
    dailyLimit: 50,
    hourlyLimit: 10,
    scheduleStartTime: '09:00',
    scheduleEndTime: '17:00'
  });

  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      const res = await api.get('/settings');
      setSettings(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMessage('');
    try {
      await api.put('/settings', settings);
      setMessage('Settings updated successfully!');
    } catch (err) {
      setMessage('Failed to update settings');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ maxWidth: '650px' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Automation & Rate Limit Settings</h3>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
          Configure maximum daily and hourly sending thresholds and active sending hours
        </p>
      </div>

      <div className="card">
        {message && (
          <div style={{
            padding: '0.75rem 1rem',
            backgroundColor: 'var(--success-light)',
            color: 'var(--success-color)',
            borderRadius: 'var(--border-radius)',
            marginBottom: '1.25rem',
            fontSize: '0.875rem'
          }}>
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Maximum Applications Per Day (MAX_EMAILS_PER_DAY)</label>
            <input
              type="number"
              className="form-control"
              value={settings.dailyLimit}
              onChange={(e) => setSettings({ ...settings, dailyLimit: parseInt(e.target.value) || 1 })}
              min="1"
              max="200"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Maximum Applications Per Hour (MAX_EMAILS_PER_HOUR)</label>
            <input
              type="number"
              className="form-control"
              value={settings.hourlyLimit}
              onChange={(e) => setSettings({ ...settings, hourlyLimit: parseInt(e.target.value) || 1 })}
              min="1"
              max="50"
              required
            />
          </div>

          <h4 style={{ margin: '1.5rem 0 1rem 0', fontSize: '1rem' }}>Active Scheduler Window</h4>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label className="form-label">Start Time (HH:mm)</label>
              <input
                type="time"
                className="form-control"
                value={settings.scheduleStartTime || '09:00'}
                onChange={(e) => setSettings({ ...settings, scheduleStartTime: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">End Time (HH:mm)</label>
              <input
                type="time"
                className="form-control"
                value={settings.scheduleEndTime || '17:00'}
                onChange={(e) => setSettings({ ...settings, scheduleEndTime: e.target.value })}
                required
              />
            </div>
          </div>

          <button type="submit" className="btn btn-primary" style={{ marginTop: '1rem' }} disabled={saving}>
            {saving ? 'Saving...' : 'Save Rate Limits & Schedule'}
          </button>
        </form>
      </div>
    </div>
  );
}
