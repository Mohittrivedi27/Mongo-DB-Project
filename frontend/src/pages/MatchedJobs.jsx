import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function MatchedJobs() {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [calculating, setCalculating] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    fetchMatches();
  }, []);

  const fetchMatches = async () => {
    try {
      const res = await api.get('/jobs/matched');
      setMatches(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRecalculate = async () => {
    setCalculating(true);
    try {
      await api.post('/jobs/matched/calculate');
      await fetchMatches();
    } catch (err) {
      alert('Failed to recalculate job matches.');
    } finally {
      setCalculating(false);
    }
  };

  const handleCreateApplication = async (match) => {
    try {
      const res = await api.post('/applications', {
        jobId: match.jobId,
        hrContactId: match.hrContactId,
        resumeId: match.recommendedResumeId
      });
      navigate(`/applications/preview/${res.data.id}`);
    } catch (err) {
      alert('Failed to generate application: ' + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Matched Job Opportunities</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Job matching engine calculates skill overlap and selects your best domain resume
          </p>
        </div>
        <button className="btn btn-primary" onClick={handleRecalculate} disabled={calculating}>
          {calculating ? 'Matching...' : '⚡ Re-Run Matching Engine'}
        </button>
      </div>

      {loading ? (
        <p style={{ color: 'var(--text-muted)' }}>Calculating matches...</p>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '1.5rem' }}>
          {matches.map((item) => (
            <div key={item.id || item.jobId} className="card" style={{ marginBottom: 0, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                  <div>
                    <h4 style={{ fontSize: '1.1rem', fontWeight: 700 }}>{item.jobTitle}</h4>
                    <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)', fontWeight: 500 }}>{item.companyName}</div>
                  </div>
                  <div style={{
                    backgroundColor: item.matchScore >= 80 ? 'var(--success-light)' : item.matchScore >= 60 ? 'var(--warning-light)' : 'var(--danger-light)',
                    color: item.matchScore >= 80 ? 'var(--success-color)' : item.matchScore >= 60 ? 'var(--warning-color)' : 'var(--danger-color)',
                    padding: '0.4rem 0.75rem',
                    borderRadius: '9999px',
                    fontWeight: 700,
                    fontSize: '1rem'
                  }}>
                    {item.matchScore}%
                  </div>
                </div>

                <div style={{ fontSize: '0.85rem', marginBottom: '0.75rem' }}>
                  <strong>Matched Skills:</strong>{' '}
                  {item.matchedSkills?.map((s) => (
                    <span key={s} className="badge badge-success" style={{ marginRight: '0.25rem' }}>{s}</span>
                  ))}
                </div>

                {item.missingSkills?.length > 0 && (
                  <div style={{ fontSize: '0.85rem', marginBottom: '0.75rem' }}>
                    <strong>Missing Skills:</strong>{' '}
                    {item.missingSkills.map((s) => (
                      <span key={s} className="badge badge-danger" style={{ marginRight: '0.25rem' }}>{s}</span>
                    ))}
                  </div>
                )}

                <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1rem', backgroundColor: '#f8fafc', padding: '0.5rem 0.75rem', borderRadius: '4px' }}>
                  <strong>Recommended Resume:</strong> {item.recommendedResumeName || 'Java Backend Resume'}
                </div>
              </div>

              <button
                className="btn btn-primary"
                style={{ width: '100%', marginTop: '0.5rem' }}
                onClick={() => handleCreateApplication(item)}
              >
                Draft & Personalized Application
              </button>
            </div>
          ))}

          {matches.length === 0 && (
            <div className="card" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '3rem' }}>
              <div style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>✨</div>
              <h4>No Matched Jobs Found</h4>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginTop: '0.5rem' }}>
                Add your technical skills and job opportunities first, then click "Re-Run Matching Engine"!
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
