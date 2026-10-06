import React, { useState, useContext } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import api from '../services/api';

export default function VerifyEmail() {
  const { user, setUser } = useContext(AuthContext);
  const location = useLocation();
  const navigate = useNavigate();

  const userEmail = location.state?.email || user?.email || '';
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);
  const [message, setMessage] = useState(location.state?.message || '');
  const [error, setError] = useState('');

  const handleVerify = async (e) => {
    e.preventDefault();
    if (!code.trim()) {
      setError('Please enter the 6-digit verification code');
      return;
    }

    setLoading(true);
    setError('');
    setMessage('');

    try {
      const response = await api.post('/auth/verify-email', {
        email: userEmail,
        code: code.trim(),
      });

      if (response.data.user) {
        setUser(response.data.user);
        localStorage.setItem('user', JSON.stringify(response.data.user));
      }

      setMessage('Email verified successfully! Redirecting...');
      setTimeout(() => {
        navigate('/');
      }, 1500);
    } catch (err) {
      setError(err.response?.data?.message || 'Verification failed. Please check the code and try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (!userEmail) {
      setError('No email address found. Please log in again.');
      return;
    }

    setResending(true);
    setError('');
    setMessage('');

    try {
      const res = await api.post('/auth/resend-verification', { email: userEmail });
      setMessage(res.data?.message || 'A new verification code has been sent to your email.');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to resend verification code. Please try again.');
    } finally {
      setResending(false);
    }
  };

  return (
    <div style={{
      minHeight: '80vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '2rem 1rem'
    }}>
      <div className="card" style={{ width: '100%', maxWidth: '440px', padding: '2.5rem 2rem', textAlign: 'center' }}>
        <div style={{ fontSize: '3rem', marginBottom: '0.75rem' }}>✉️</div>
        <h2 style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '0.5rem' }}>
          Verify Your Email
        </h2>
        <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
          We sent a 6-digit verification code to:
          <br />
          <strong style={{ color: 'var(--text-main)' }}>{userEmail || 'your email'}</strong>
        </p>

        {message && (
          <div style={{
            padding: '0.75rem 1rem',
            backgroundColor: 'var(--success-light)',
            color: 'var(--success-color)',
            borderRadius: 'var(--border-radius)',
            fontSize: '0.875rem',
            marginBottom: '1.25rem',
            textAlign: 'left'
          }}>
            {message}
          </div>
        )}

        {error && (
          <div style={{
            padding: '0.75rem 1rem',
            backgroundColor: 'var(--danger-light)',
            color: 'var(--danger-color)',
            borderRadius: 'var(--border-radius)',
            fontSize: '0.875rem',
            marginBottom: '1.25rem',
            textAlign: 'left'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleVerify}>
          <div className="form-group" style={{ textAlign: 'left' }}>
            <label className="form-label" style={{ textAlign: 'center', display: 'block' }}>
              Enter 6-Digit Code
            </label>
            <input
              type="text"
              className="form-control"
              maxLength="6"
              style={{
                textAlign: 'center',
                letterSpacing: '0.5em',
                fontSize: '1.5rem',
                fontWeight: 700,
                fontFamily: 'monospace'
              }}
              value={code}
              onChange={(e) => setCode(e.target.value)}
              placeholder="123456"
              required
              autoFocus
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '0.5rem' }}
            disabled={loading}
          >
            {loading ? 'Verifying Code...' : 'Verify Email Address'}
          </button>
        </form>

        <div style={{ marginTop: '1.5rem', paddingTop: '1.25rem', borderTop: '1px solid var(--border-color)', fontSize: '0.875rem' }}>
          <span style={{ color: 'var(--text-muted)' }}>Didn't receive the email? </span>
          <button
            type="button"
            onClick={handleResend}
            disabled={resending}
            style={{
              background: 'none',
              border: 'none',
              color: 'var(--primary-color)',
              fontWeight: 600,
              cursor: 'pointer',
              padding: 0,
              fontSize: '0.875rem'
            }}
          >
            {resending ? 'Sending...' : 'Resend Code'}
          </button>
        </div>

        <div style={{ marginTop: '1rem' }}>
          <Link to="/" style={{ color: 'var(--text-muted)', fontSize: '0.8rem', textDecoration: 'none' }}>
            Back to Dashboard
          </Link>
        </div>
      </div>
    </div>
  );
}
