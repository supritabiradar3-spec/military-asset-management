import React, { useState, useEffect, useCallback } from 'react';
import { auditLogsApi } from '../api/auditLogsApi';
import { basesApi } from '../api/basesApi';
import { useAuth } from '../context/AuthContext';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { AlertMessage } from '../components/common/AlertMessage';
import {
  History,
  Shield,
  RotateCcw,
  Filter,
} from 'lucide-react';

export function AuditLogsPage() {
  const { user, isAdmin } = useAuth();

  const [logs, setLogs] = useState([]);
  const [bases, setBases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters
  const [filters, setFilters] = useState({
    baseId: '',
    action: '',
    entityType: '',
    startDateTime: '',
    endDateTime: '',
  });

  // Load Bases for filter
  useEffect(() => {
    basesApi.getAll().then(setBases).catch(console.error);
  }, []);

  // Fetch Audit Logs
  const fetchLogs = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await auditLogsApi.getAll(filters);
      setLogs(data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch audit log records');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchLogs();
  }, [fetchLogs]);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleReset = () => {
    setFilters({
      baseId: '',
      action: '',
      entityType: '',
      startDateTime: '',
      endDateTime: '',
    });
  };

  const getActionBadge = (action) => {
    if (!action) return <span className="badge badge-neutral">UNKNOWN</span>;
    const act = action.toUpperCase();
    if (act.includes('PURCHASE') || act.includes('CREATE')) {
      return <span className="badge badge-success">{action}</span>;
    }
    if (act.includes('TRANSFER')) {
      return <span className="badge badge-info">{action}</span>;
    }
    if (act.includes('EXPENDITURE') || act.includes('DELETE')) {
      return <span className="badge badge-danger">{action}</span>;
    }
    if (act.includes('ASSIGN')) {
      return <span className="badge badge-warning">{action}</span>;
    }
    if (act.includes('LOGIN')) {
      return <span className="badge badge-admin">{action}</span>;
    }
    return <span className="badge badge-neutral">{action}</span>;
  };

  return (
    <div>
      {/* Header */}
      <div style={{ marginBottom: '1.25rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.75rem' }}>
        <div>
          <h1 style={{ fontSize: '1.35rem', fontWeight: 700 }}>Audit History</h1>
          <p style={{ marginTop: '0.2rem' }}>
            System event log, user transactions, and security activity records
          </p>
        </div>
        <div>
          <span className="badge badge-admin" style={{ padding: '0.3rem 0.65rem' }}>
            <Shield size={12} style={{ marginRight: '0.2rem' }} />
            ADMIN ACCESS
          </span>
        </div>
      </div>

      {error && <AlertMessage type="error" message={error} onClose={() => setError(null)} />}

      {/* Styled Filter Card */}
      <div className="filter-card">
        <div className="filter-header">
          <div className="filter-title">
            <Filter size={13} color="var(--primary)" />
            <span>Filter Audit Records</span>
          </div>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={handleReset}
            title="Reset filters"
          >
            <RotateCcw size={12} />
            <span>Reset</span>
          </button>
        </div>

        <div className="filter-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))' }}>
          {/* Base Filter */}
          <div className="filter-item">
            <label className="filter-label" htmlFor="audit-base">
              Base Scope
            </label>
            <select
              id="audit-base"
              name="baseId"
              className="filter-select"
              value={filters.baseId}
              onChange={handleFilterChange}
            >
              <option value="">All Bases</option>
              {bases.map((b) => (
                <option key={b.id} value={b.id}>
                  {b.name}
                </option>
              ))}
            </select>
          </div>

          {/* Action Type Filter */}
          <div className="filter-item">
            <label className="filter-label" htmlFor="audit-action">
              Action Type
            </label>
            <select
              id="audit-action"
              name="action"
              className="filter-select"
              value={filters.action}
              onChange={handleFilterChange}
            >
              <option value="">All Actions</option>
              <option value="LOGIN">LOGIN</option>
              <option value="PURCHASE_CREATED">PURCHASE_CREATED</option>
              <option value="TRANSFER_CREATED">TRANSFER_CREATED</option>
              <option value="ASSIGNMENT_CREATED">ASSIGNMENT_CREATED</option>
              <option value="EXPENDITURE_CREATED">EXPENDITURE_CREATED</option>
              <option value="ASSET_CREATED">ASSET_CREATED</option>
              <option value="BASE_CREATED">BASE_CREATED</option>
              <option value="EQUIPMENT_TYPE_CREATED">EQUIPMENT_TYPE_CREATED</option>
            </select>
          </div>

          {/* Entity Type Filter */}
          <div className="filter-item">
            <label className="filter-label" htmlFor="audit-entity">
              Entity Type
            </label>
            <select
              id="audit-entity"
              name="entityType"
              className="filter-select"
              value={filters.entityType}
              onChange={handleFilterChange}
            >
              <option value="">All Entities</option>
              <option value="USER">USER</option>
              <option value="PURCHASE">PURCHASE</option>
              <option value="TRANSFER">TRANSFER</option>
              <option value="ASSIGNMENT">ASSIGNMENT</option>
              <option value="EXPENDITURE">EXPENDITURE</option>
              <option value="ASSET">ASSET</option>
              <option value="BASE">BASE</option>
              <option value="EQUIPMENT_TYPE">EQUIPMENT_TYPE</option>
            </select>
          </div>

          {/* Start Date */}
          <div className="filter-item">
            <label className="filter-label" htmlFor="audit-from-date">
              From Date
            </label>
            <input
              id="audit-from-date"
              type="date"
              name="startDateTime"
              className="filter-input"
              value={filters.startDateTime}
              onChange={handleFilterChange}
            />
          </div>

          {/* End Date */}
          <div className="filter-item">
            <label className="filter-label" htmlFor="audit-to-date">
              To Date
            </label>
            <input
              id="audit-to-date"
              type="date"
              name="endDateTime"
              className="filter-input"
              value={filters.endDateTime}
              onChange={handleFilterChange}
            />
          </div>
        </div>
      </div>

      {/* Audit Logs Table */}
      <div className="card" style={{ padding: '0' }}>
        <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <h3 className="card-title">
            <span>Activity Log</span>
          </h3>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
            {logs.length} {logs.length === 1 ? 'event' : 'events'}
          </span>
        </div>

        {loading ? (
          <LoadingSpinner message="Loading audit history logs..." />
        ) : logs.length === 0 ? (
          <div className="empty-state">
            <History size={32} style={{ color: 'var(--text-muted)', opacity: 0.5 }} />
            <p>No audit events match the selected filters.</p>
          </div>
        ) : (
          <div className="table-responsive" style={{ border: 'none', borderRadius: '0' }}>
            <table className="table table-audit-logs">
              <thead>
                <tr>
                  <th style={{ width: '70px' }}>Log ID</th>
                  <th style={{ width: '150px' }}>Timestamp</th>
                  <th style={{ width: '110px' }}>Operator</th>
                  <th style={{ width: '180px' }}>Action</th>
                  <th style={{ width: '120px' }}>Entity Type</th>
                  <th style={{ width: '90px' }}>Entity ID</th>
                  <th style={{ width: '130px' }}>Base Scope</th>
                  <th>Details & Context</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.id}>
                    <td className="mono" style={{ color: 'var(--text-muted)' }}>
                      #{log.id}
                    </td>
                    <td style={{ color: 'var(--text-secondary)', fontSize: '0.775rem' }} className="mono">
                      {log.timestamp ? new Date(log.timestamp).toLocaleString() : '—'}
                    </td>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                      {log.username || 'System'}
                    </td>
                    <td>{getActionBadge(log.action)}</td>
                    <td style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>
                      {log.entityType || '—'}
                    </td>
                    <td className="mono" style={{ color: 'var(--text-muted)' }}>
                      {log.entityId ? `#${log.entityId}` : '—'}
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>
                      {log.baseId ? `Base #${log.baseId}` : 'Global Scope'}
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem', maxWidth: '300px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={log.details || ''}>
                      {log.details || '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
