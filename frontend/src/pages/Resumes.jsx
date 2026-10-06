import React, { useState, useEffect } from 'react';
import api from '../services/api';

export default function Resumes() {
  const [resumes, setResumes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [newResume, setNewResume] = useState({
    name: '',
    type: 'Java Backend',
    associatedRoles: 'Java Backend Developer, Spring Boot Developer',
  });
  const [selectedFile, setSelectedFile] = useState(null);

  useEffect(() => {
    fetchResumes();
  }, []);

  const fetchResumes = async () => {
    try {
      const res = await api.get('/resumes');
      setResumes(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!newResume.name || !selectedFile) return;

    const formData = new FormData();
    formData.append('file', selectedFile);
    formData.append('name', newResume.name);
    formData.append('type', newResume.type);
    formData.append('associatedRoles', newResume.associatedRoles);

    try {
      await api.post('/resumes', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setShowModal(false);
      setNewResume({ name: '', type: 'Java Backend', associatedRoles: '' });
      setSelectedFile(null);
      fetchResumes();
    } catch (err) {
      alert('Failed to upload resume: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this resume?')) return;
    try {
      await api.delete(`/resumes/${id}`);
      fetchResumes();
    } catch (err) {
      alert('Failed to delete resume');
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Resume Management</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Upload targeted resumes for different job domains (e.g. Java Backend, Full Stack, Android)
          </p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          + Upload Domain Resume
        </button>
      </div>

      {loading ? (
        <p style={{ color: 'var(--text-muted)' }}>Loading resumes...</p>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.5rem' }}>
          {resumes.map((resume) => (
            <div key={resume.id} className="card" style={{ marginBottom: 0 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                <div>
                  <h4 style={{ fontSize: '1rem', fontWeight: 600 }}>{resume.name}</h4>
                  <span className="badge badge-primary" style={{ marginTop: '0.25rem' }}>{resume.type}</span>
                </div>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={() => handleDelete(resume.id)}
                >
                  Delete
                </button>
              </div>

              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.5rem' }}>
                <strong>Target Roles:</strong> {Array.isArray(resume.associatedRoles) ? resume.associatedRoles.join(', ') : resume.associatedRoles}
              </div>

              <div style={{ fontSize: '0.8rem', color: 'var(--text-light)' }}>
                File: {resume.fileName || 'resume.pdf'} ({(resume.fileSize ? (resume.fileSize / 1024).toFixed(1) : '150')} KB)
              </div>
            </div>
          ))}

          {resumes.length === 0 && (
            <div className="card" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '3rem' }}>
              <div style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>📄</div>
              <h4>No Resumes Uploaded Yet</h4>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginTop: '0.5rem' }}>
                Upload at least one domain resume to start auto-selecting appropriate resumes for job applications.
              </p>
            </div>
          )}
        </div>
      )}

      {showModal && (
        <div style={{
          position: 'fixed',
          top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          zIndex: 1000
        }}>
          <div className="card" style={{ width: '100%', maxWidth: '500px' }}>
            <h3 className="card-title" style={{ marginBottom: '1rem' }}>Upload Domain Resume</h3>
            <form onSubmit={handleUpload}>
              <div className="form-group">
                <label className="form-label">Resume Label / Title</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Java Backend Developer Resume 2026"
                  value={newResume.name}
                  onChange={(e) => setNewResume({ ...newResume, name: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Domain Type</label>
                <select
                  className="form-control"
                  value={newResume.type}
                  onChange={(e) => setNewResume({ ...newResume, type: e.target.value })}
                >
                  <option value="Java Backend">Java Backend</option>
                  <option value="Java Full Stack">Java Full Stack</option>
                  <option value="Android Developer">Android Developer</option>
                  <option value="Data Science">Data Science</option>
                  <option value="DevOps & Cloud">DevOps & Cloud</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Associated Job Titles (Comma-separated)</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Java Backend Developer, Spring Boot Engineer"
                  value={newResume.associatedRoles}
                  onChange={(e) => setNewResume({ ...newResume, associatedRoles: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label className="form-label">PDF / DOCX File</label>
                <input
                  type="file"
                  className="form-control"
                  accept=".pdf,.doc,.docx"
                  onChange={(e) => setSelectedFile(e.target.files[0])}
                  required
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Upload Resume
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
