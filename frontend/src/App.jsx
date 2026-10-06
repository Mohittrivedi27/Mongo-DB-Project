import React, { useContext } from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import Sidebar from './components/Sidebar';
import TopBar from './components/TopBar';

// Page imports
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Profile from './pages/Profile';
import Skills from './pages/Skills';
import PreferredRoles from './pages/PreferredRoles';
import Resumes from './pages/Resumes';
import Jobs from './pages/Jobs';
import HRContacts from './pages/HRContacts';
import MatchedJobs from './pages/MatchedJobs';
import ApplicationPreview from './pages/ApplicationPreview';
import Applications from './pages/Applications';
import ApplicationDetails from './pages/ApplicationDetails';
import EmailTemplates from './pages/EmailTemplates';
import EmailQueue from './pages/EmailQueue';
import Settings from './pages/Settings';
import VerifyEmail from './pages/VerifyEmail';

const ProtectedRoute = ({ children }) => {
  const { token, loading } = useContext(AuthContext);

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading application session...</div>;
  }

  // Allow bypass in local development preview if token not set, but enforce login cleanly
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

const MainLayout = ({ children }) => {
  const location = useLocation();

  const getPageTitle = (pathname) => {
    switch (pathname) {
      case '/': return 'Dashboard Analytics';
      case '/profile': return 'User Profile & Contact Details';
      case '/skills': return 'Skills Management';
      case '/preferred-roles': return 'Target Job Roles';
      case '/resumes': return 'Domain Resume Repository';
      case '/jobs': return 'Job Opportunities Directory';
      case '/hr-contacts': return 'Recruiter & HR Directory';
      case '/matched-jobs': return 'AI Job Matching Engine';
      case '/applications': return 'Application Tracker';
      case '/templates': return 'Personalized Email Templates';
      case '/queue': return 'Kafka Email Queue & Logs';
      case '/settings': return 'Automation Rate Limit Settings';
      case '/verify-email': return 'Email Verification';
      default:
        if (pathname.startsWith('/applications/preview')) return 'Review & Approve Application';
        if (pathname.startsWith('/applications/')) return 'Application Timeline';
        return 'Job Application System';
    }
  };

  return (
    <div className="app-container">
      <Sidebar />
      <div className="main-content">
        <TopBar title={getPageTitle(location.pathname)} />
        <main className="content-body">{children}</main>
      </div>
    </div>
  );
};

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route
          path="/*"
          element={
            <ProtectedRoute>
              <MainLayout>
                <Routes>
                  <Route path="/" element={<Dashboard />} />
                  <Route path="/verify-email" element={<VerifyEmail />} />
                  <Route path="/profile" element={<Profile />} />
                  <Route path="/skills" element={<Skills />} />
                  <Route path="/preferred-roles" element={<PreferredRoles />} />
                  <Route path="/resumes" element={<Resumes />} />
                  <Route path="/jobs" element={<Jobs />} />
                  <Route path="/hr-contacts" element={<HRContacts />} />
                  <Route path="/matched-jobs" element={<MatchedJobs />} />
                  <Route path="/applications/preview/:id" element={<ApplicationPreview />} />
                  <Route path="/applications/:id" element={<ApplicationDetails />} />
                  <Route path="/applications" element={<Applications />} />
                  <Route path="/templates" element={<EmailTemplates />} />
                  <Route path="/queue" element={<EmailQueue />} />
                  <Route path="/settings" element={<Settings />} />
                  <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
              </MainLayout>
            </ProtectedRoute>
          }
        />
      </Routes>
    </AuthProvider>
  );
}
