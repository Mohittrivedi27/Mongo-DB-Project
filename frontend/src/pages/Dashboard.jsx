import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import api from '../services/api';

export default function Dashboard() {
  const { user } = useContext(AuthContext);
  const [stats, setStats] = useState({
    totalJobs: 0,
    matchingJobs: 0,
    applicationsToday: 0,
    dailyLimit: 50,
    applicationsSent: 0,
    emailsDelivered: 0,
    replies: 0,
    interviews: 0,
    rejected: 0,
    offers: 0
  });

  const [recentApplications, setRecentApplications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const response = await api.get('/dashboard/stats');
      setStats(response.data);
      const appsResponse = await api.get('/applications');
      setRecentApplications(appsResponse.data.slice(0, 5));
    } catch (err) {
      console.error('Failed to load dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {user && !user.emailVerified && (
        <div style={{
          backgroundColor: 'var(--warning-light)',
          border: '1px solid var(--warning-color)',
          borderRadius: 'var(--border-radius)',
          padding: '0.85rem 1.25rem',
          marginBottom: '1.5rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: '1rem'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
            <span style={{ fontSize: '1.4rem' }}>✉️</span>
            <span style={{ fontSize: '0.875rem', color: '#92400e' }}>
              <strong>Verify your email:</strong> Please verify your email address to enable the automated application dispatcher and real-time recruiter messaging.
            </span>
          </div>
          <Link
            to="/verify-email"
            className="btn btn-sm btn-primary"
            style={{ textDecoration: 'none', whiteSpace: 'nowrap' }}
          >
            Verify Email
          </Link>
        </div>
      )}

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-label">Applications Today</div>
          <div className="stat-number">{stats.applicationsToday} / {stats.dailyLimit}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>Daily Quota</div>
        </div>

        <div className="stat-card">
          <div className="stat-label">Matching Jobs</div>
          <div className="stat-number" style={{ color: 'var(--primary-color)' }}>{stats.matchingJobs}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>High Match (&gt;75%)</div>
        </div>

        <div className="stat-card">
          <div className="stat-label">Emails Delivered</div>
          <div className="stat-number" style={{ color: 'var(--info-color)' }}>{stats.emailsDelivered}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>Sent & Confirmed</div>
        </div>

        <div className="stat-card">
          <div className="stat-label">Replies Received</div>
          <div className="stat-number" style={{ color: 'var(--success-color)' }}>{stats.replies}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>HR Responses</div>
        </div>

        <div className="stat-card">
          <div className="stat-label">Interviews Scheduled</div>
          <div className="stat-number" style={{ color: 'var(--warning-color)' }}>{stats.interviews}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>Active Processes</div>
        </div>

        <div className="stat-card">
          <div className="stat-label">Offers Received</div>
          <div className="stat-number" style={{ color: 'var(--success-color)' }}>{stats.offers}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>Job Offers</div>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Recent Job Applications</h3>
        </div>

        {loading ? (
          <p style={{ color: 'var(--text-muted)' }}>Loading recent applications...</p>
        ) : recentApplications.length === 0 ? (
          <p style={{ color: 'var(--text-muted)' }}>No job applications generated yet. Import jobs to start matching!</p>
        ) : (
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Company</th>
                  <th>Job Title</th>
                  <th>Match Score</th>
                  <th>HR Contact</th>
                  <th>Status</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {recentApplications.map((app) => (
                  <tr key={app.id}>
                    <td style={{ fontWeight: 600 }}>{app.companyName}</td>
                    <td>{app.jobTitle}</td>
                    <td>
                      <span className="badge badge-primary">{app.matchScore}%</span>
                    </td>
                    <td>{app.hrEmail || 'N/A'}</td>
                    <td>
                      <span className={`badge badge-${app.status === 'SENT' ? 'success' : app.status === 'APPROVED' ? 'info' : 'warning'}`}>
                        {app.status}
                      </span>
                    </td>
                    <td>{new Date(app.createdAt).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
