import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldAlert, ArrowLeft } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export function NotFoundPage() {
  const { isAuthenticated } = useAuth();

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '65vh',
        textAlign: 'center',
        padding: '2rem',
      }}
    >
      <div
        style={{
          backgroundColor: 'rgba(239, 68, 68, 0.1)',
          color: 'var(--danger)',
          padding: '1rem',
          borderRadius: 'var(--radius-lg)',
          marginBottom: '1.25rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <ShieldAlert size={40} />
      </div>
      <h1 style={{ fontSize: '1.75rem', marginBottom: '0.4rem' }}>404 - Page Not Found</h1>
      <p style={{ color: 'var(--text-secondary)', maxWidth: '420px', marginBottom: '1.35rem' }}>
        The requested page does not exist or you lack operational access permissions.
      </p>
      <Link
        to={isAuthenticated ? '/dashboard' : '/login'}
        className="btn btn-primary"
        style={{ display: 'inline-flex', alignItems: 'center', gap: '0.45rem' }}
      >
        <ArrowLeft size={15} />
        <span>Return to {isAuthenticated ? 'Dashboard' : 'Sign In'}</span>
      </Link>
    </div>
  );
}
