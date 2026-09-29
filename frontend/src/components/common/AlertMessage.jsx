import React from 'react';
import { AlertCircle, CheckCircle2, Info, AlertTriangle, X } from 'lucide-react';

export function AlertMessage({ type = 'error', message, onClose }) {
  if (!message) return null;

  const getAlertClass = () => {
    switch (type) {
      case 'success':
        return 'alert-success';
      case 'info':
        return 'alert-info';
      case 'warning':
        return 'alert-warning';
      default:
        return 'alert-error';
    }
  };

  const renderIcon = () => {
    switch (type) {
      case 'success':
        return <CheckCircle2 size={18} style={{ flexShrink: 0 }} />;
      case 'info':
        return <Info size={18} style={{ flexShrink: 0 }} />;
      case 'warning':
        return <AlertTriangle size={18} style={{ flexShrink: 0 }} />;
      default:
        return <AlertCircle size={18} style={{ flexShrink: 0 }} />;
    }
  };

  return (
    <div className={`alert ${getAlertClass()}`} role="alert">
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
        {renderIcon()}
        <span>{message}</span>
      </div>
      {onClose && (
        <button
          type="button"
          onClick={onClose}
          style={{
            background: 'transparent',
            border: 'none',
            color: 'inherit',
            cursor: 'pointer',
            padding: '0.2rem',
            display: 'flex',
            alignItems: 'center',
            opacity: 0.8,
            transition: 'opacity 0.15s ease',
          }}
          aria-label="Dismiss alert"
          onMouseEnter={(e) => { e.currentTarget.style.opacity = '1'; }}
          onMouseLeave={(e) => { e.currentTarget.style.opacity = '0.8'; }}
        >
          <X size={16} />
        </button>
      )}
    </div>
  );
}
