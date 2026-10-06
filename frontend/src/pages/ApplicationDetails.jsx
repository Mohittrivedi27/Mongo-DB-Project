import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../services/api';

export default function ApplicationDetails() {
  const { id } = useParams();
  const [app, setApp] = useState(null);
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    try {
      const res = await api.get(`/applications/${id}`);
      setApp(res.data);
      const eventsRes = await api.get(`/applications/${id}/events`);
      setEvents(eventsRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (newStatus) => {
    try {
      await api.patch(`/applications/${id}/status`, { status: newStatus });
      fetchData();
    } catch (err) {
      alert('Failed to update status');
    }
  };

  if (loading) return <p style={{ color: 'var(--text-muted)' }}>Loading application timeline...</p>;
  if (!app) return <p style={{ color: 'var(--text-muted)' }}>Application not found.</p>;

  return (
    <div style={{ maxWidth: '800px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>{app.jobTitle} @ {app.companyName}</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Application ID: {app.id}</p>
        </div>
        <Link to={`/applications/preview/${app.id}`} className="btn btn-secondary">
          Edit Application Email
        </Link>
      </div>

      <div className="card">
        <h4 className="card-title" style={{ marginBottom: '1rem' }}>Lifecycle & Status Management</h4>
        <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '1.5rem' }}>
          {['DRAFT', 'APPROVED', 'QUEUED', 'SENT', 'DELIVERED', 'REPLIED', 'INTERVIEW', 'OFFER', 'REJECTED'].map((st) => (
            <button
              key={st}
              className={`btn btn-sm ${app.status === st ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => handleUpdateStatus(st)}
            >
              {st}
            </button>
          ))}
        </div>

        <h4 className="card-title" style={{ marginBottom: '1rem' }}>Audit Log & Event History</h4>
        <div style={{ borderLeft: '2px solid var(--primary-color)', paddingLeft: '1rem' }}>
          {events.map((ev, idx) => (
            <div key={idx} style={{ marginBottom: '1rem' }}>
              <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>{ev.eventType}</div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{ev.description}</div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-light)' }}>{new Date(ev.timestamp).toLocaleString()}</div>
            </div>
          ))}

          {events.length === 0 && (
            <div style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
              Application created at {new Date(app.createdAt || Date.now()).toLocaleString()}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
