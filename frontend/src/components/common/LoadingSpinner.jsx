import React from 'react';

export function LoadingSpinner({ message = 'Loading...', inline = false }) {
  if (inline) {
    return (
      <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem' }}>
        <div className="spinner-circle" />
        <span style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>{message}</span>
        <style>{`
          .spinner-circle {
            width: 16px;
            height: 16px;
            border: 2px solid rgba(255, 255, 255, 0.2);
            border-top-color: var(--primary);
            border-radius: 50%;
            animation: spin 0.6s linear infinite;
          }
          @keyframes spin {
            to { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '3rem 1rem' }}>
      <div className="spinner-main" />
      {message && <p style={{ marginTop: '1rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{message}</p>}
      <style>{`
        .spinner-main {
          width: 38px;
          height: 38px;
          border: 3px solid rgba(99, 102, 241, 0.15);
          border-top-color: var(--primary);
          border-radius: 50%;
          animation: spin 0.8s linear infinite;
        }
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
}
