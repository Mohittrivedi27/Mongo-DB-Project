import React, { useState, useEffect } from 'react';
import api from '../services/api';

export default function HRContacts() {
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [newContact, setNewContact] = useState({
    companyName: '',
    name: '',
    email: '',
    designation: 'Technical Recruiter',
    source: 'MANUAL',
    verificationStatus: 'VERIFIED'
  });

  useEffect(() => {
    fetchContacts();
  }, []);

  const fetchContacts = async () => {
    try {
      const res = await api.get('/hr-contacts');
      setContacts(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddContact = async (e) => {
    e.preventDefault();
    try {
      await api.post('/hr-contacts', newContact);
      setShowModal(false);
      setNewContact({ companyName: '', name: '', email: '', designation: 'Technical Recruiter', source: 'MANUAL', verificationStatus: 'VERIFIED' });
      fetchContacts();
    } catch (err) {
      alert('Failed to add HR contact');
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>HR & Recruiter Contacts</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Legitimate business contact details for job applications
          </p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          + Add HR Contact
        </button>
      </div>

      {loading ? (
        <p style={{ color: 'var(--text-muted)' }}>Loading HR contacts...</p>
      ) : (
        <div className="table-responsive card">
          <table className="table">
            <thead>
              <tr>
                <th>Company</th>
                <th>HR Name</th>
                <th>Email Address</th>
                <th>Designation</th>
                <th>Source</th>
                <th>Verification</th>
              </tr>
            </thead>
            <tbody>
              {contacts.map((contact) => (
                <tr key={contact.id}>
                  <td style={{ fontWeight: 600 }}>{contact.companyName}</td>
                  <td>{contact.name}</td>
                  <td style={{ fontFamily: 'monospace' }}>{contact.email}</td>
                  <td>{contact.designation}</td>
                  <td>
                    <span className="badge badge-primary">{contact.source || 'MANUAL'}</span>
                  </td>
                  <td>
                    <span className={`badge badge-${contact.verificationStatus === 'VERIFIED' ? 'success' : 'warning'}`}>
                      {contact.verificationStatus || 'VERIFIED'}
                    </span>
                  </td>
                </tr>
              ))}

              {contacts.length === 0 && (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                    No HR contacts added yet. Add a contact or import via Google Sheets!
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {showModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000
        }}>
          <div className="card" style={{ width: '100%', maxWidth: '500px' }}>
            <h3 className="card-title" style={{ marginBottom: '1rem' }}>Add HR Contact</h3>
            <form onSubmit={handleAddContact}>
              <div className="form-group">
                <label className="form-label">Company Name</label>
                <input
                  type="text"
                  className="form-control"
                  value={newContact.companyName}
                  onChange={(e) => setNewContact({ ...newContact, companyName: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">HR / Recruiter Name</label>
                <input
                  type="text"
                  className="form-control"
                  value={newContact.name}
                  onChange={(e) => setNewContact({ ...newContact, name: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Email Address</label>
                <input
                  type="email"
                  className="form-control"
                  value={newContact.email}
                  onChange={(e) => setNewContact({ ...newContact, email: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Designation / Role</label>
                <input
                  type="text"
                  className="form-control"
                  value={newContact.designation}
                  onChange={(e) => setNewContact({ ...newContact, designation: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save Contact
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
