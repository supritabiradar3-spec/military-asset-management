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

        {/* Evaluation Demo Credentials Guide */}
        <div style={{ marginTop: '1.5rem', paddingTop: '1.15rem', borderTop: '1px solid var(--border-subtle)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: 'var(--text-muted)', fontSize: '0.725rem', marginBottom: '0.5rem' }}>
            <KeyRound size={12} />
            <span style={{ fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.04em' }}>Demo Accounts</span>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '0.35rem' }}>
            <button
              type="button"
              className="btn btn-outline btn-sm"
              style={{ fontSize: '0.725rem', padding: '0.2rem 0.35rem' }}
              onClick={() => handleQuickFill('admin', 'admin123')}
            >
              ADMIN
            </button>
            <button
              type="button"
              className="btn btn-outline btn-sm"
              style={{ fontSize: '0.725rem', padding: '0.2rem 0.35rem' }}
              onClick={() => handleQuickFill('commander1', 'commander123')}
            >
              COMMANDER
            </button>
            <button
              type="button"
              className="btn btn-outline btn-sm"
              style={{ fontSize: '0.725rem', padding: '0.2rem 0.35rem' }}
              onClick={() => handleQuickFill('logistics1', 'logistics123')}
            >
              LOGISTICS
            </button>
          </div>
        </div>

        <div style={{ marginTop: '0.85rem', textAlign: 'center', fontSize: '0.7rem', color: 'var(--text-muted)' }}>
          Role-Based Access Control Protected
        </div>
      </div>
    </div>
  );
}
