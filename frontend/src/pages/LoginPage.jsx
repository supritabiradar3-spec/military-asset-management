import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { AlertMessage } from '../components/common/AlertMessage';
import { Shield, Lock, User, LogIn, KeyRound } from 'lucide-react';

export function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const isExpired = new URLSearchParams(location.search).get('expired') === 'true';

  React.useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username.trim() || !password) {
      setError('Please enter both username and password.');
      return;
    }

    setError(null);
    setSubmitting(true);

    try {
      await login(username.trim(), password);
      const origin = location.state?.from?.pathname || '/dashboard';
      navigate(origin, { replace: true });
    } catch (err) {
      setError(err.message || 'Invalid username or password. Please verify your credentials.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleQuickFill = (u, p) => {
    setUsername(u);
    setPassword(p);
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'var(--bg-base)',
        padding: '1.5rem',
      }}
    >
      <div
        className="card"
        style={{
          width: '100%',
          maxWidth: '420px',
          padding: '2rem',
          boxShadow: 'var(--shadow-lg)',
          borderColor: 'var(--border-color)',
        }}
      >
        {/* Header Branding */}
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <div
            style={{
              width: '46px',
              height: '46px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'rgba(13, 148, 136, 0.12)',
              border: '1px solid rgba(13, 148, 136, 0.3)',
              color: 'var(--primary)',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: '0.85rem',
            }}
          >
            <Shield size={24} />
          </div>
          <h1 style={{ fontSize: '1.25rem', marginBottom: '0.25rem' }}>
            Military Asset Management
          </h1>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
            Asset Operations & Logistics
          </p>
        </div>

        {isExpired && !error && (
          <AlertMessage
            type="info"
            message="Your session has expired. Please sign in again."
          />
        )}

        {error && <AlertMessage type="error" message={error} onClose={() => setError(null)} />}

        <form onSubmit={handleSubmit}>
          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label className="form-label" htmlFor="login-username">
              Username <span className="form-label-required">*</span>
            </label>
            <div style={{ position: 'relative' }}>
              <div
                style={{
                  position: 'absolute',
                  left: '0.75rem',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  color: 'var(--text-muted)',
                  display: 'flex',
                  alignItems: 'center',
                }}
              >
                <User size={15} />
              </div>
              <input
                id="login-username"
                type="text"
                className="form-input"
                style={{ paddingLeft: '2.25rem' }}
                placeholder="Enter username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                autoComplete="username"
                autoFocus
                disabled={submitting}
                required
              />
            </div>
          </div>

          <div className="form-group" style={{ marginBottom: '1.35rem' }}>
            <label className="form-label" htmlFor="login-password">
              Password <span className="form-label-required">*</span>
            </label>
            <div style={{ position: 'relative' }}>
              <div
                style={{
                  position: 'absolute',
                  left: '0.75rem',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  color: 'var(--text-muted)',
                  display: 'flex',
                  alignItems: 'center',
                }}
              >
                <Lock size={15} />
              </div>
              <input
                id="login-password"
                type="password"
                className="form-input"
                style={{ paddingLeft: '2.25rem' }}
                placeholder="••••••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
                disabled={submitting}
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-lg"
            style={{ width: '100%' }}
            disabled={submitting}
          >
            <LogIn size={16} />
            <span>{submitting ? 'Signing in...' : 'Sign In'}</span>
          </button>
        </form>

        {/* Demo Credentials Section */}
        <div style={{ marginTop: '1.5rem', paddingTop: '1.15rem', borderTop: '1px solid var(--border-color)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-secondary)', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.75rem' }}>
            <KeyRound size={14} style={{ color: 'var(--primary)' }} />
            <span>Demo Credentials</span>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            {/* Admin */}
            <div
              onClick={() => handleQuickFill('admin', 'admin123')}
              style={{
                padding: '0.55rem 0.75rem',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--bg-input)',
                border: '1px solid var(--border-color)',
                cursor: 'pointer',
                transition: 'border-color 0.15s ease',
              }}
              onMouseEnter={(e) => { e.currentTarget.style.borderColor = 'var(--primary)'; }}
              onMouseLeave={(e) => { e.currentTarget.style.borderColor = 'var(--border-color)'; }}
              title="Click to fill Admin credentials"
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.2rem' }}>
                <span style={{ fontSize: '0.775rem', fontWeight: 700, color: 'var(--text-primary)' }}>Admin</span>
                <span className="badge badge-admin" style={{ fontSize: '0.65rem', padding: '0.1rem 0.35rem' }}>Full Access</span>
              </div>
              <div style={{ fontSize: '0.725rem', color: 'var(--text-secondary)', display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
                <span>Username: <strong style={{ color: 'var(--text-primary)', fontFamily: 'var(--font-mono)' }}>admin</strong></span>
                <span>Password: <strong style={{ color: 'var(--text-primary)', fontFamily: 'var(--font-mono)' }}>admin123</strong></span>
              </div>
            </div>

            {/* Base Commander */}
            <div
              onClick={() => handleQuickFill('commander1', 'commander123')}
              style={{
                padding: '0.55rem 0.75rem',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--bg-input)',
                border: '1px solid var(--border-color)',
                cursor: 'pointer',
                transition: 'border-color 0.15s ease',
              }}
              onMouseEnter={(e) => { e.currentTarget.style.borderColor = 'var(--primary)'; }}
              onMouseLeave={(e) => { e.currentTarget.style.borderColor = 'var(--border-color)'; }}
              title="Click to fill Base Commander credentials"
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.2rem' }}>
                <span style={{ fontSize: '0.775rem', fontWeight: 700, color: 'var(--text-primary)' }}>Base Commander</span>
                <span className="badge badge-commander" style={{ fontSize: '0.65rem', padding: '0.1rem 0.35rem' }}>FOB Alpha</span>
              </div>
              <div style={{ fontSize: '0.725rem', color: 'var(--text-secondary)', display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
                <span>Username: <strong style={{ color: 'var(--text-primary)', fontFamily: 'var(--font-mono)' }}>commander1</strong></span>
                <span>Password: <strong style={{ color: 'var(--text-primary)', fontFamily: 'var(--font-mono)' }}>commander123</strong></span>
              </div>
            </div>

            {/* Logistics Officer */}
            <div
              onClick={() => handleQuickFill('logistics1', 'logistics123')}
              style={{
                padding: '0.55rem 0.75rem',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--bg-input)',
                border: '1px solid var(--border-color)',
                cursor: 'pointer',
                transition: 'border-color 0.15s ease',
              }}
              onMouseEnter={(e) => { e.currentTarget.style.borderColor = 'var(--primary)'; }}
              onMouseLeave={(e) => { e.currentTarget.style.borderColor = 'var(--border-color)'; }}
              title="Click to fill Logistics Officer credentials"
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.2rem' }}>
                <span style={{ fontSize: '0.775rem', fontWeight: 700, color: 'var(--text-primary)' }}>Logistics Officer</span>
                <span className="badge badge-logistics" style={{ fontSize: '0.65rem', padding: '0.1rem 0.35rem' }}>FOB Alpha</span>
              </div>
              <div style={{ fontSize: '0.725rem', color: 'var(--text-secondary)', display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
                <span>Username: <strong style={{ color: 'var(--text-primary)', fontFamily: 'var(--font-mono)' }}>logistics1</strong></span>
                <span>Password: <strong style={{ color: 'var(--text-primary)', fontFamily: 'var(--font-mono)' }}>logistics123</strong></span>
              </div>
            </div>
          </div>
        </div>

        <div style={{ marginTop: '0.85rem', textAlign: 'center', fontSize: '0.7rem', color: 'var(--text-muted)' }}>
          Role-Based Access Control Protected
        </div>
      </div>
    </div>
  );
}
