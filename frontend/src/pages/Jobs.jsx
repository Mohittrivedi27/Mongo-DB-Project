import React, { useState, useEffect } from 'react';
import api from '../services/api';

export default function Jobs() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [showSyncModal, setShowSyncModal] = useState(false);
  const [spreadsheetId, setSpreadsheetId] = useState('');
  const [syncing, setSyncing] = useState(false);

  const [newJob, setNewJob] = useState({
    companyName: '',
    jobTitle: '',
    description: '',
    requiredSkills: 'Java, Spring Boot, MongoDB',
    preferredSkills: 'Docker, Redis, Kafka',
    location: 'Remote',
    workMode: 'REMOTE',
    experienceRequired: '1-3 years',
    salary: '$90,000 - $120,000',
    jobUrl: 'https://company.com/careers/job-123'
  });

  useEffect(() => {
    fetchJobs();
  }, []);

  const fetchJobs = async () => {
    try {
      const res = await api.get('/jobs');
      setJobs(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateJob = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...newJob,
        requiredSkills: newJob.requiredSkills.split(',').map((s) => s.trim()),
        preferredSkills: newJob.preferredSkills.split(',').map((s) => s.trim())
      };
      await api.post('/jobs', payload);
      setShowModal(false);
      fetchJobs();
    } catch (err) {
      alert('Failed to add job opportunity');
    }
  };

  const handleGoogleSheetsSync = async (e) => {
    e.preventDefault();
    setSyncing(true);
    try {
      const res = await api.post('/google-sheets/import-jobs', { spreadsheetId });
      alert(`Import Successful! Imported ${res.data.recordsAdded} new jobs from Google Sheets.`);
      setShowSyncModal(false);
      fetchJobs();
    } catch (err) {
      alert('Failed to import jobs from Google Sheets: ' + (err.response?.data?.message || err.message));
    } finally {
      setSyncing(false);
    }
  };

  const filteredJobs = jobs.filter((j) =>
    j.jobTitle?.toLowerCase().includes(search.toLowerCase()) ||
    j.companyName?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Job Opportunities</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Manage target job postings or import from Google Sheets
          </p>
        </div>
        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button className="btn btn-secondary" onClick={() => setShowSyncModal(true)}>
            📊 Import Google Sheets
          </button>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            + Add Job Opportunity
          </button>
        </div>
      </div>

      <div className="card" style={{ padding: '1rem', marginBottom: '1.5rem' }}>
        <input
          type="text"
          className="form-control"
          placeholder="Search jobs by title or company..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {loading ? (
        <p style={{ color: 'var(--text-muted)' }}>Loading jobs...</p>
      ) : (
        <div className="table-responsive card">
          <table className="table">
            <thead>
              <tr>
                <th>Company</th>
                <th>Job Title</th>
                <th>Required Skills</th>
                <th>Work Mode</th>
                <th>Experience</th>
                <th>Source</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredJobs.map((job) => (
                <tr key={job.id}>
                  <td style={{ fontWeight: 600 }}>{job.companyName}</td>
                  <td>{job.jobTitle}</td>
                  <td>
                    {Array.isArray(job.requiredSkills)
                      ? job.requiredSkills.map((s) => (
                          <span key={s} className="badge badge-primary" style={{ marginRight: '0.25rem' }}>
                            {s}
                          </span>
                        ))
                      : job.requiredSkills}
                  </td>
                  <td>
                    <span className="badge badge-info">{job.workMode || 'REMOTE'}</span>
                  </td>
                  <td>{job.experienceRequired || '1-3 Yrs'}</td>
                  <td>
                    <span className="badge badge-warning">{job.source || 'MANUAL'}</span>
                  </td>
                  <td>
                    <a
                      href={job.jobUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="btn btn-secondary btn-sm"
                    >
                      View Link
                    </a>
                  </td>
                </tr>
              ))}

              {filteredJobs.length === 0 && (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                    No job opportunities found. Add a job or import via Google Sheets!
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Add Job Modal */}
      {showModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000
        }}>
          <div className="card" style={{ width: '100%', maxWidth: '600px', maxHeight: '90vh', overflowY: 'auto' }}>
            <h3 className="card-title" style={{ marginBottom: '1rem' }}>Add Job Opportunity</h3>
            <form onSubmit={handleCreateJob}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Company Name</label>
                  <input
                    type="text"
                    className="form-control"
                    value={newJob.companyName}
                    onChange={(e) => setNewJob({ ...newJob, companyName: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Job Title</label>
                  <input
                    type="text"
                    className="form-control"
                    value={newJob.jobTitle}
                    onChange={(e) => setNewJob({ ...newJob, jobTitle: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Job Description</label>
                <textarea
                  className="form-control"
                  rows="3"
                  value={newJob.description}
                  onChange={(e) => setNewJob({ ...newJob, description: e.target.value })}
                ></textarea>
              </div>

              <div className="form-group">
                <label className="form-label">Required Skills (Comma-separated)</label>
                <input
                  type="text"
                  className="form-control"
                  value={newJob.requiredSkills}
                  onChange={(e) => setNewJob({ ...newJob, requiredSkills: e.target.value })}
                  required
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Location</label>
                  <input
                    type="text"
                    className="form-control"
                    value={newJob.location}
                    onChange={(e) => setNewJob({ ...newJob, location: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Work Mode</label>
                  <select
                    className="form-control"
                    value={newJob.workMode}
                    onChange={(e) => setNewJob({ ...newJob, workMode: e.target.value })}
                  >
                    <option value="REMOTE">Remote</option>
                    <option value="HYBRID">Hybrid</option>
                    <option value="ON_SITE">On-Site</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Job URL / Link</label>
                <input
                  type="url"
                  className="form-control"
                  value={newJob.jobUrl}
                  onChange={(e) => setNewJob({ ...newJob, jobUrl: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save Job
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Google Sheets Modal */}
      {showSyncModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000
        }}>
          <div className="card" style={{ width: '100%', maxWidth: '500px' }}>
            <h3 className="card-title" style={{ marginBottom: '1rem' }}>Import Jobs from Google Sheets</h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
              Enter your Google Spreadsheet ID (or public sheet link). Columns expected: <code>Company | Job Role | HR Name | HR Email | Job URL | Status</code>
            </p>
            <form onSubmit={handleGoogleSheetsSync}>
              <div className="form-group">
                <label className="form-label">Spreadsheet ID or Link</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"
                  value={spreadsheetId}
                  onChange={(e) => setSpreadsheetId(e.target.value)}
                  required
                />
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowSyncModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={syncing}>
                  {syncing ? 'Syncing...' : 'Start Import'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
