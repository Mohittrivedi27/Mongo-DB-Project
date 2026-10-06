import React, { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

export default function TopBar({ title }) {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="top-bar">
      <h1 className="page-title">{title || 'Job Application System'}</h1>
      <div className="user-profile-menu">
        {user ? (
          <>
            <div style={{ textAlign: 'right' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: '0.4rem' }}>
                <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>{user.name || user.email}</span>
                {user.emailVerified ? (
                  <span className="badge badge-success" style={{ fontSize: '0.7rem', padding: '0.15rem 0.45rem' }}>
                    Verified ✓
                  </span>
                ) : (
                  <Link
                    to="/verify-email"
                    className="badge badge-warning"
                    style={{ fontSize: '0.7rem', padding: '0.15rem 0.45rem', textDecoration: 'none' }}
                    title="Click to verify email"
                  >
                    Unverified ⚠️
                  </Link>
                )}
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{user.email}</div>
            </div>
            <button className="btn btn-secondary btn-sm" onClick={handleLogout}>
              Logout
            </button>
          </>
        ) : (
          <button className="btn btn-primary btn-sm" onClick={() => navigate('/login')}>
            Login
          </button>
        )}
      </div>
    </header>
  );
}
