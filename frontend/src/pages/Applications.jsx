import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';

export default function Applications() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [search, setSearch] = useState('');

  useEffect(() => {
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    try {
      const res = await api.get('/applications');
      setApplications(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const filteredApps = applications.filter((app) => {
    const matchesSearch =
      app.companyName?.toLowerCase().includes(search.toLowerCase()) ||
      app.jobTitle?.toLowerCase().includes(search.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || app.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Application Tracker</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Track full lifecycle status of personalized job applications
          </p>
        </div>
        <Link to="/matched-jobs" className="btn btn-primary">
          + New Application from Matched Jobs
        </Link>
      </div>

      <div className="card" style={{ padding: '1rem', marginBottom: '1.5rem', display: 'flex', gap: '1rem' }}>
        <input
          type="text"
          className="form-control"
          placeholder="Search by company or job role..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ flex: 2 }}
        />
        <select
          className="form-control"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          style={{ flex: 1 }}
        >
          <option value="ALL">All Statuses</option>
          <option value="DRAFT">DRAFT</option>
          <option value="APPROVED">APPROVED</option>
          <option value="QUEUED">QUEUED</option>
          <option value="SENT">SENT</option>
          <option value="DELIVERED">DELIVERED</option>
          <option value="REPLIED">REPLIED</option>
          <option value="INTERVIEW">INTERVIEW</option>
          <option value="RATE_LIMITED">RATE_LIMITED</option>
        </select>
      </div>

      {loading ? (
        <p style={{ color: 'var(--text-muted)' }}>Loading applications...</p>
      ) : (
        <div className="table-responsive card">
          <table className="table">
            <thead>
              <tr>
                <th>Company</th>
                <th>Job Role</th>
                <th>HR Recruiter</th>
                <th>Match Score</th>
                <th>Status</th>
                <th>Applied Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredApps.map((app) => (
                <tr key={app.id}>
                  <td style={{ fontWeight: 600 }}>{app.companyName}</td>
                  <td>{app.jobTitle}</td>
                  <td>{app.hrEmail || 'N/A'}</td>
                  <td>
                    <span className="badge badge-primary">{app.matchScore}%</span>
                  </td>
                  <td>
                    <span className={`badge badge-${
                      app.status === 'SENT' || app.status === 'DELIVERED' ? 'success' :
                      app.status === 'APPROVED' || app.status === 'QUEUED' ? 'info' :
                      app.status === 'REPLIED' || app.status === 'INTERVIEW' ? 'success' : 'warning'
                    }`}>
                      {app.status}
                    </span>
                  </td>
                  <td>{new Date(app.createdAt).toLocaleDateString()}</td>
                  <td>
                    <Link to={`/applications/preview/${app.id}`} className="btn btn-secondary btn-sm">
                      Review / Edit
                    </Link>
                  </td>
                </tr>
              ))}

              {filteredApps.length === 0 && (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                    No job applications matched current filters.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
