import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import api from '../services/api';

export default function ApplicationPreview() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);

  const [application, setApplication] = useState(null);
  const [resumes, setResumes] = useState([]);
  const [emailSubject, setEmailSubject] = useState('');
  const [emailBody, setEmailBody] = useState('');
  const [selectedResumeId, setSelectedResumeId] = useState('');
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    fetchApplicationDetails();
  }, [id]);

  const fetchApplicationDetails = async () => {
    try {
      const res = await api.get(`/applications/${id}`);
      setApplication(res.data);
      setEmailSubject(res.data.emailSubject || '');
      setEmailBody(res.data.emailBody || '');
      setSelectedResumeId(res.data.resumeId || '');

      const resumesRes = await api.get('/resumes');
      setResumes(resumesRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSaveDraft = async () => {
    setActionLoading(true);
    try {
      await api.put(`/applications/${id}`, {
        emailSubject,
        emailBody,
        resumeId: selectedResumeId
      });
      alert('Draft saved successfully!');
    } catch (err) {
      alert('Failed to save draft');
    } finally {
      setActionLoading(false);
    }
  };

  const handleApprove = async () => {
    setActionLoading(true);
    try {
      await api.post(`/applications/${id}/approve`, {
        emailSubject,
        emailBody,
        resumeId: selectedResumeId
      });
      alert('Application APPROVED! Ready to queue.');
      fetchApplicationDetails();
    } catch (err) {
      alert('Failed to approve application: ' + (err.response?.data?.message || err.message));
    } finally {
      setActionLoading(false);
    }
  };

  const handleSendNow = async () => {
    if (!user?.emailVerified) {
      alert('Your email address is not verified yet. Please verify your email before sending job applications.');
      navigate('/verify-email');
      return;
    }

    if (!window.confirm('Send personalized email now to recruiter via SMTP Queue?')) return;
    setActionLoading(true);
    try {
      await api.post(`/applications/${id}/send`);
      alert('Application submitted to sending queue!');
      navigate('/applications');
    } catch (err) {
      alert('Failed to send application: ' + (err.response?.data?.message || err.message));
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <p style={{ color: 'var(--text-muted)' }}>Loading application details...</p>;
  if (!application) return <p style={{ color: 'var(--text-muted)' }}>Application not found.</p>;

  return (
    <div style={{ maxWidth: '900px' }}>
      {!user?.emailVerified && (
        <div style={{
          backgroundColor: 'var(--warning-light)',
          border: '1px solid var(--warning-color)',
          borderRadius: 'var(--border-radius)',
          padding: '0.85rem 1.25rem',
          marginBottom: '1.25rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: '1rem'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
            <span style={{ fontSize: '1.3rem' }}>⚠️</span>
            <span style={{ fontSize: '0.875rem', color: '#92400e' }}>
              <strong>Email Verification Required:</strong> Your email address is unverified. Verify your email to enable automatic dispatch to recruiters.
            </span>
          </div>
          <button
            className="btn btn-sm btn-primary"
            style={{ whiteSpace: 'nowrap' }}
            onClick={() => navigate('/verify-email')}
          >
            Verify Now
          </button>
        </div>
      )}

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Review & Approve Application</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Verify candidate details, selected resume, and personalized email copy before sending
          </p>
        </div>
        <div>
          <span className={`badge badge-${application.status === 'APPROVED' ? 'success' : application.status === 'SENT' ? 'primary' : 'warning'}`}>
            Status: {application.status}
          </span>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '1.5rem' }}>
        {/* Left Column: Job & Recruiter Details */}
        <div>
          <div className="card">
            <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.75rem' }}>{application.jobTitle}</h4>
            <div style={{ fontWeight: 600, color: 'var(--primary-color)', marginBottom: '0.75rem' }}>{application.companyName}</div>

            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.5rem' }}>
              <strong>Match Score:</strong> <span className="badge badge-success">{application.matchScore}%</span>
            </div>

            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
              <strong>Recruiter:</strong> {application.hrName || 'Hiring Manager'} ({application.hrEmail})
            </div>

            <div className="form-group">
              <label className="form-label">Attached Domain Resume</label>
              <select
                className="form-control"
                value={selectedResumeId}
                onChange={(e) => setSelectedResumeId(e.target.value)}
              >
                {resumes.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.name} ({r.type})
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* Right Column: Personalized Email Editor */}
        <div>
          <div className="card">
            <h4 className="card-title" style={{ marginBottom: '1rem' }}>Personalized Email Copy</h4>

            <div className="form-group">
              <label className="form-label">Subject Line</label>
              <input
                type="text"
                className="form-control"
                value={emailSubject}
                onChange={(e) => setEmailSubject(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Email Body Content</label>
              <textarea
                className="form-control"
                rows="12"
                style={{ fontFamily: 'monospace', fontSize: '0.9rem', lineHeight: '1.4' }}
                value={emailBody}
                onChange={(e) => setEmailBody(e.target.value)}
              ></textarea>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '1.5rem' }}>
              <button className="btn btn-secondary" onClick={handleSaveDraft} disabled={actionLoading}>
                Save Draft
              </button>
              <div style={{ display: 'flex', gap: '0.75rem' }}>
                {application.status !== 'APPROVED' && application.status !== 'SENT' && (
                  <button className="btn btn-success" onClick={handleApprove} disabled={actionLoading}>
                    ✓ Approve Application
                  </button>
                )}
                <button className="btn btn-primary" onClick={handleSendNow} disabled={actionLoading || application.status === 'SENT'}>
                  ✉️ Queue & Send Email
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
