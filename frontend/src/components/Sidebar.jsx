import React from 'react';
import { NavLink } from 'react-router-dom';

export default function Sidebar() {
  const navItems = [
    { path: '/', label: 'Dashboard', icon: '📊' },
    { path: '/profile', label: 'Profile', icon: '👤' },
    { path: '/skills', label: 'Skills', icon: '⚡' },
    { path: '/preferred-roles', label: 'Preferred Roles', icon: '🎯' },
    { path: '/resumes', label: 'Resumes', icon: '📄' },
    { path: '/jobs', label: 'Jobs', icon: '💼' },
    { path: '/hr-contacts', label: 'HR Contacts', icon: '📇' },
    { path: '/matched-jobs', label: 'Matched Jobs', icon: '✨' },
    { path: '/applications', label: 'Applications', icon: '📮' },
    { path: '/templates', label: 'Email Templates', icon: '✉️' },
    { path: '/queue', label: 'Email Queue', icon: '⏱️' },
    { path: '/settings', label: 'Settings', icon: '⚙️' },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <span style={{ fontSize: '1.5rem' }}>🚀</span>
        <div className="sidebar-title">Job AutoPilot</div>
      </div>
      <nav className="sidebar-nav">
        {navItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
            end={item.path === '/'}
          >
            <span>{item.icon}</span>
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
