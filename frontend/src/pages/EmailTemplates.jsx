import React, { useState, useEffect } from 'react';
import api from '../services/api';

export default function EmailTemplates() {
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedTemplate, setSelectedTemplate] = useState({
    name: 'Default Personalized Application',
    subject: 'Application for {{jobTitle}} at {{companyName}}',
    body: `Hi {{hrName}},\n\nI am {{candidateName}} and I am interested in the {{jobTitle}} opportunity at {{companyName}}.\n\nMy core technical skills include {{skills}}.\n\nPlease find my resume attached for your consideration.\n\nPortfolio: {{portfolio}}\nGitHub: {{github}}\nLinkedIn: {{linkedin}}\n\nRegards,\n{{candidateName}}`
  });
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchTemplates();
  }, []);

  const fetchTemplates = async () => {
    try {
      const res = await api.get('/email-templates');
      if (res.data && res.data.length > 0) {
        setTemplates(res.data);
        setSelectedTemplate(res.data[0]);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await api.post('/email-templates', selectedTemplate);
      alert('Email template saved successfully!');
      fetchTemplates();
    } catch (err) {
      alert('Failed to save email template');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ maxWidth: '900px' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Email Template Manager</h3>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
          Customize dynamic placeholders for automated personalized candidate emails
        </p>
      </div>

      <div className="card">
        <div style={{ marginBottom: '1rem', backgroundColor: '#f8fafc', padding: '0.75rem', borderRadius: '4px' }}>
          <strong>Available Variables:</strong>{' '}
          <code style={{ color: 'var(--primary-color)' }}>
            {"{{candidateName}}"}, {"{{hrName}}"}, {"{{companyName}}"}, {"{{jobTitle}}"}, {"{{skills}}"}, {"{{portfolio}}"}, {"{{github}}"}, {"{{linkedin}}"}
          </code>
        </div>

        <form onSubmit={handleSave}>
          <div className="form-group">
            <label className="form-label">Template Name</label>
            <input
              type="text"
              className="form-control"
              value={selectedTemplate.name}
              onChange={(e) => setSelectedTemplate({ ...selectedTemplate, name: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Subject Pattern</label>
            <input
              type="text"
              className="form-control"
              value={selectedTemplate.subject}
              onChange={(e) => setSelectedTemplate({ ...selectedTemplate, subject: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Email Body Pattern</label>
            <textarea
              className="form-control"
              rows="10"
              style={{ fontFamily: 'monospace', fontSize: '0.9rem' }}
              value={selectedTemplate.body}
              onChange={(e) => setSelectedTemplate({ ...selectedTemplate, body: e.target.value })}
              required
            ></textarea>
          </div>

          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving...' : 'Save Template'}
          </button>
        </form>
      </div>
    </div>
  );
}
