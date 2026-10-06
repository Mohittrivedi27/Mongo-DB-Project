import React, { useState, useEffect } from 'react';
import api from '../services/api';

export default function EmailQueue() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    try {
      const res = await api.get('/email-logs');
      setLogs(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Kafka Asynchronous Email Queue & Logs</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Monitors Kafka message processing, Redis rate limits, and SMTP delivery logs
          </p>
        </div>
        <button className="btn btn-secondary" onClick={fetchLogs}>
          🔄 Refresh Logs
        </button>
      </div>

      {loading ? (
        <p style={{ color: 'var(--text-muted)' }}>Loading queue logs...</p>
      ) : (
        <div className="table-responsive card">
          <table className="table">
            <thead>
              <tr>
                <th>Log ID</th>
                <th>Application ID</th>
                <th>Recipient Email</th>
                <th>Subject</th>
                <th>Status</th>
                <th>Sent Timestamp</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id}>
                  <td style={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>{log.id}</td>
                  <td style={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>{log.applicationId}</td>
                  <td style={{ fontWeight: 500 }}>{log.recipientEmail}</td>
                  <td>{log.subject}</td>
                  <td>
                    <span className={`badge badge-${
                      log.status === 'SENT' || log.status === 'DELIVERED' ? 'success' :
                      log.status === 'QUEUED' ? 'info' :
                      log.status === 'RATE_LIMITED' ? 'warning' : 'danger'
                    }`}>
                      {log.status}
                    </span>
                  </td>
                  <td>{log.sentAt ? new Date(log.sentAt).toLocaleString() : 'Pending Queue'}</td>
                </tr>
              ))}

              {logs.length === 0 && (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                    No email queue logs recorded yet. Send an application to publish Kafka events!
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
