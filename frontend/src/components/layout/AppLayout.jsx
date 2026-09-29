import React, { useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { useAuth } from '../../context/AuthContext';
import { Menu, MapPin, User as UserIcon } from 'lucide-react';

export function AppLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user, isAdmin, isBaseCommander, isLogisticsOfficer } = useAuth();
  const location = useLocation();

  const getPageTitle = (pathname) => {
    if (pathname.startsWith('/dashboard')) return 'Dashboard';
    if (pathname.startsWith('/purchases')) return 'Purchases';
    if (pathname.startsWith('/transfers')) return 'Transfers';
    if (pathname.startsWith('/assignments-expenditures')) return 'Assignments & Expenditures';
    if (pathname.startsWith('/assets')) return 'Asset Inventory';
    if (pathname.startsWith('/audit-logs')) return 'Audit History';
    return 'Asset Operations';
  };

  const getRoleBadge = () => {
    if (isAdmin) return <span className="badge badge-admin">ADMIN</span>;
    if (isBaseCommander) return <span className="badge badge-commander">COMMANDER</span>;
    if (isLogisticsOfficer) return <span className="badge badge-logistics">LOGISTICS</span>;
    return <span className="badge badge-neutral">{user?.role}</span>;
  };

  return (
    <div className="app-container">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div className="main-content">
        <header className="topbar">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <button
              type="button"
              className="mobile-toggle btn btn-outline btn-sm"
              onClick={() => setSidebarOpen(true)}
              aria-label="Open navigation menu"
              style={{ padding: '0.3rem 0.45rem' }}
            >
              <Menu size={16} />
            </button>
            <span style={{ fontWeight: 600, fontSize: '0.925rem', color: 'var(--text-primary)' }}>
              {getPageTitle(location.pathname)}
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            {user?.baseId && (
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.3rem',
                  fontSize: '0.725rem',
                  color: 'var(--text-secondary)',
                  backgroundColor: 'var(--bg-subtle)',
                  padding: '0.25rem 0.55rem',
                  borderRadius: 'var(--radius-sm)',
                  border: '1px solid var(--border-color)',
                }}
              >
                <MapPin size={11} color="var(--info)" />
                <span>Base #{user.baseId}</span>
              </div>
            )}
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <div
                style={{
                  width: '28px',
                  height: '28px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--bg-subtle)',
                  border: '1px solid var(--border-color)',
                  color: 'var(--primary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontWeight: 700,
                  fontSize: '0.8rem',
                }}
              >
                {user?.username ? user.username.charAt(0).toUpperCase() : <UserIcon size={14} />}
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                  {user?.username}
                </span>
                {getRoleBadge()}
              </div>
            </div>
          </div>
        </header>

        <main className="page-container">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
