import React from 'react';

export function MetricCard({
  title,
  value,
  subtitle,
  icon: Icon,
  variant = 'default',
  onClick,
  isClickable = false,
}) {
  const getValueColor = () => {
    switch (variant) {
      case 'success':
        return 'var(--success)';
      case 'danger':
        return 'var(--danger)';
      case 'warning':
        return 'var(--warning)';
      case 'primary':
        return 'var(--text-primary)';
      case 'info':
        return 'var(--info)';
      default:
        return 'var(--text-primary)';
    }
  };

  return (
    <div
      className="card"
      onClick={onClick}
      style={{
        cursor: isClickable ? 'pointer' : 'default',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        padding: '1.25rem',
        minHeight: '120px',
        transition: 'border-color 0.15s ease',
      }}
      onMouseEnter={(e) => {
        if (isClickable) {
          e.currentTarget.style.borderColor = 'var(--border-focus)';
        }
      }}
      onMouseLeave={(e) => {
        if (isClickable) {
          e.currentTarget.style.borderColor = 'var(--border-color)';
        }
      }}
    >
      <div>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <span style={{ fontSize: '0.725rem', fontWeight: 600, color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
            {title}
          </span>
          {Icon && <Icon size={15} style={{ color: 'var(--text-muted)' }} />}
        </div>
        <div
          style={{
            fontSize: '1.85rem',
            fontWeight: 700,
            color: getValueColor(),
            marginTop: '0.35rem',
            fontFamily: 'var(--font-mono)',
            lineHeight: 1.15,
          }}
        >
          {typeof value === 'number' ? value.toLocaleString() : value}
        </div>
      </div>

      <div style={{ marginTop: '0.65rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        {subtitle && (
          <span style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>
            {subtitle}
          </span>
        )}
        {isClickable && (
          <span style={{ fontSize: '0.725rem', color: 'var(--primary)', fontWeight: 600, marginLeft: 'auto' }}>
            Breakdown &rarr;
          </span>
        )}
      </div>
    </div>
  );
}
