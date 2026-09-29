import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  ShoppingCart,
  ArrowLeftRight,
  ClipboardList,
  Boxes,
  History,
  LogOut,
  Shield,
  MapPin,
  X,
} from 'lucide-react';

export function Sidebar({ isOpen, onClose }) {
  const { user, logout, isAdmin, isBaseCommander, isLogisticsOfficer } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getRoleBadgeClass = () => {
    if (isAdmin) return 'badge-admin';
    if (isBaseCommander) return 'badge-commander';
    if (isLogisticsOfficer) return 'badge-logistics';
    return 'badge-neutral';
  };

  const navItems = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/purchases', label: 'Purchases', icon: ShoppingCart },
    { to: '/transfers', label: 'Transfers', icon: ArrowLeftRight },
    { to: '/assignments-expenditures', label: 'Assignments & Expenditures', icon: ClipboardList },
    { to: '/assets', label: 'Assets', icon: Boxes },
    ...(isAdmin ? [{ to: '/audit-logs', label: 'Audit Logs', icon: History }] : []),
  ];

  return (
    <>
      {/* Mobile backdrop */}
      {isOpen && (
        <div
          onClick={onClose}
          style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(5, 8, 15, 0.75)',
            backdropFilter: 'blur(3px)',
            WebkitBackdropFilter: 'blur(3px)',
            zIndex: 35,
          }}
        />
      )}

      <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
        {/* Brand / Logo */}
        <div className="sidebar-brand">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
            <div
              style={{
                backgroundColor: 'rgba(13, 148, 136, 0.12)',
                color: 'var(--primary)',
                padding: '0.35rem',
                borderRadius: 'var(--radius-md)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                border: '1px solid rgba(13, 148, 136, 0.3)',
              }}
            >
              <Shield size={18} />
            </div>
            <div>
              <div className="sidebar-brand-title">
                Military Asset Management
              </div>
              <div className="sidebar-brand-subtitle">
                Asset Operations
              </div>
            </div>
          </div>
          <button
            onClick={onClose}
            className="mobile-toggle"
            style={{ background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', padding: '0.25rem' }}
            aria-label="Close menu"
          >
            <X size={18} />
          </button>
        </div>

        {/* Navigation Links */}
        <nav className="sidebar-nav">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={onClose}
                className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
              >
                {({ isActive }) => (
                  <>
                    <Icon size={16} style={{ color: isActive ? 'var(--primary)' : 'var(--text-muted)', flexShrink: 0 }} />
                    <span style={{ flex: 1 }}>{item.label}</span>
                  </>
                )}
              </NavLink>
            );
          })}
        </nav>

        {/* Footer: User Information & Sign Out */}
        <div className="sidebar-footer">
          <div className="sidebar-user-info">
            <div>
              <div style={{ fontSize: '0.825rem', fontWeight: 600, color: 'var(--text-primary)', lineHeight: 1.2 }}>
                {user?.username}
              </div>
              {user?.baseId ? (
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', color: 'var(--text-muted)', fontSize: '0.7rem', marginTop: '0.15rem' }}>
                  <MapPin size={10} color="var(--info)" />
                  <span>Base #{user.baseId}</span>
                </div>
              ) : (
                <div style={{ color: 'var(--text-muted)', fontSize: '0.7rem', marginTop: '0.15rem' }}>
                  All Bases
                </div>
              )}
            </div>
            <span className={`badge ${getRoleBadgeClass()}`}>
              {user?.role}
            </span>
          </div>

          <button
            type="button"
            onClick={handleLogout}
            style={{
              width: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '0.5rem',
              padding: '0.45rem 0.75rem',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border-color)',
              background: 'transparent',
              color: 'var(--text-secondary)',
              fontSize: '0.8rem',
              fontWeight: 500,
              cursor: 'pointer',
              transition: 'all 0.12s ease',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = 'rgba(239, 68, 68, 0.1)';
              e.currentTarget.style.borderColor = 'rgba(239, 68, 68, 0.3)';
              e.currentTarget.style.color = '#f87171';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = 'transparent';
              e.currentTarget.style.borderColor = 'var(--border-color)';
              e.currentTarget.style.color = 'var(--text-secondary)';
            }}
          >
            <LogOut size={14} />
            <span>Sign Out</span>
          </button>
        </div>
      </aside>
    </>
  );
}
