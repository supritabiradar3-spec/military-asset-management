import React, { useState, useEffect, useCallback } from 'react';
import { dashboardApi } from '../api/dashboardApi';
import { basesApi } from '../api/basesApi';
import { equipmentTypesApi } from '../api/equipmentTypesApi';
import { useAuth } from '../context/AuthContext';
import { FilterBar } from '../components/common/FilterBar';
import { Modal } from '../components/common/Modal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { AlertMessage } from '../components/common/AlertMessage';
import {
  TrendingUp,
  Package,
  ArrowDownLeft,
  ArrowUpRight,
  UserCheck,
  Flame,
  ArrowRight,
} from 'lucide-react';

export function DashboardPage() {
  const { user, isAdmin } = useAuth();

  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);

  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    baseId: user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '',
    equipmentTypeId: '',
  });

  const [netMovementModalOpen, setNetMovementModalOpen] = useState(false);

  // Load lookup options
  useEffect(() => {
    async function loadLookups() {
      try {
        const [basesData, eqTypesData] = await Promise.all([
          basesApi.getAll(),
          equipmentTypesApi.getAll(),
        ]);
        setBases(basesData || []);
        setEquipmentTypes(eqTypesData || []);
      } catch (err) {
        console.error('Failed to load lookup data', err);
      }
    }
    loadLookups();
  }, []);

  // Fetch Dashboard metrics
  const fetchMetrics = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await dashboardApi.getMetrics(filters);
      setMetrics(data);
    } catch (err) {
      setError(err.message || 'Failed to load dashboard metrics');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchMetrics();
  }, [fetchMetrics]);

  const handleResetFilters = () => {
    setFilters({
      startDate: '',
      endDate: '',
      baseId: user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '',
      equipmentTypeId: '',
    });
  };

  return (
    <div>
      {/* Header */}
      <div style={{ marginBottom: '1.25rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.75rem' }}>
        <div>
          <h1 style={{ fontSize: '1.35rem', fontWeight: 700 }}>Dashboard</h1>
          <p style={{ marginTop: '0.2rem' }}>
            Operational overview of asset movement, inventory balances, and field allocations
          </p>
        </div>
        <div>
          <span className="badge badge-neutral">
            Scope: {isAdmin ? 'All Bases' : `Base #${user?.baseId || 'Assigned'}`}
          </span>
        </div>
      </div>

      {/* Filter Component */}
      <FilterBar
        filters={filters}
        onFilterChange={setFilters}
        onReset={handleResetFilters}
        bases={bases}
        equipmentTypes={equipmentTypes}
        userRole={user?.role}
        userBaseId={user?.baseId}
      />

      {error && <AlertMessage type="error" message={error} onClose={() => setError(null)} />}

      {loading ? (
        <LoadingSpinner message="Calculating operational balances..." />
      ) : metrics ? (
        <>
          {/* Top Summary Area: Opening Balance -> Net Movement -> Closing Balance */}
          <div className="dashboard-summary-grid">
            {/* Opening Balance */}
            <div className="summary-tile">
              <div>
                <div className="summary-tile-label">Opening Balance</div>
                <div className="summary-tile-value">
                  {metrics.openingBalance.toLocaleString()}
                </div>
              </div>
              <div className="summary-tile-sub">
                {filters.startDate ? `Prior to ${filters.startDate}` : 'Initial baseline stock'}
              </div>
            </div>

            {/* Net Movement (Prominent Center Piece) */}
            <div
              className="summary-tile summary-tile-hero"
              onClick={() => setNetMovementModalOpen(true)}
              style={{ cursor: 'pointer', transition: 'border-color 0.15s ease' }}
              onMouseEnter={(e) => {
                e.currentTarget.style.borderColor = 'var(--border-focus)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.borderColor = '#28374d';
              }}
            >
              <div>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <div className="summary-tile-label" style={{ color: 'var(--primary)' }}>
                    Net Movement
                  </div>
                  <span style={{ fontSize: '0.7rem', color: 'var(--primary)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.2rem' }}>
                    Breakdown <ArrowRight size={12} />
                  </span>
                </div>
                <div
                  className="summary-tile-value"
                  style={{
                    color: metrics.netMovement >= 0 ? 'var(--success)' : 'var(--danger)',
                  }}
                >
                  {metrics.netMovement >= 0 ? `+${metrics.netMovement.toLocaleString()}` : metrics.netMovement.toLocaleString()}
                </div>
              </div>
              <div className="summary-tile-sub">
                Purchases + Transfers In − Transfers Out
              </div>
            </div>

            {/* Closing Balance */}
            <div className="summary-tile">
              <div>
                <div className="summary-tile-label">Closing Balance</div>
                <div className="summary-tile-value">
                  {metrics.closingBalance.toLocaleString()}
                </div>
              </div>
              <div className="summary-tile-sub">
                Accounted inventory on hand
              </div>
            </div>
          </div>

          {/* Operational Status Area: Assigned & Expended */}
          <div className="operational-status-bar">
            {/* Assigned in Field */}
            <div className="status-mini-tile">
              <div>
                <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
                  Assigned in Field
                </div>
                <div style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--warning)', marginTop: '0.2rem', fontFamily: 'var(--font-mono)' }}>
                  {metrics.assigned.toLocaleString()}
                </div>
                <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                  Issued to personnel (remains on-book)
                </div>
              </div>
              <UserCheck size={20} style={{ color: 'var(--warning)', opacity: 0.8 }} />
            </div>

            {/* Expended / Consumed */}
            <div className="status-mini-tile">
              <div>
                <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
                  Expended / Consumed
                </div>
                <div style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--danger)', marginTop: '0.2rem', fontFamily: 'var(--font-mono)' }}>
                  {metrics.expended.toLocaleString()}
                </div>
                <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                  Permanent write-off / ammunition consumed
                </div>
              </div>
              <Flame size={20} style={{ color: 'var(--danger)', opacity: 0.8 }} />
            </div>
          </div>

          {/* Movement Summary Section */}
          <div className="card" style={{ marginBottom: '1.5rem' }}>
            <div className="card-header">
              <h3 className="card-title">
                <span>Movement Summary</span>
              </h3>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                {filters.startDate && filters.endDate
                  ? `${filters.startDate} to ${filters.endDate}`
                  : 'Cumulative period'}
              </span>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '0.85rem' }}>
              {/* Purchases */}
              <div
                style={{
                  backgroundColor: 'var(--bg-input)',
                  padding: '1rem',
                  borderRadius: 'var(--radius-md)',
                  border: '1px solid var(--border-color)',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <span style={{ fontSize: '0.775rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                    Purchases (+)
                  </span>
                  <Package size={14} style={{ color: 'var(--text-muted)' }} />
                </div>
                <div style={{ fontSize: '1.35rem', fontWeight: 700, color: 'var(--text-primary)', marginTop: '0.25rem', fontFamily: 'var(--font-mono)' }}>
                  +{metrics.purchases.toLocaleString()}
                </div>
                <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                  Inbound acquisitions
                </div>
              </div>

              {/* Transfers In */}
              <div
                style={{
                  backgroundColor: 'var(--bg-input)',
                  padding: '1rem',
                  borderRadius: 'var(--radius-md)',
                  border: '1px solid var(--border-color)',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <span style={{ fontSize: '0.775rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                    Transfers In (+)
                  </span>
                  <ArrowDownLeft size={14} style={{ color: 'var(--success)' }} />
                </div>
                <div style={{ fontSize: '1.35rem', fontWeight: 700, color: 'var(--success)', marginTop: '0.25rem', fontFamily: 'var(--font-mono)' }}>
                  +{metrics.transferIn.toLocaleString()}
                </div>
                <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                  Received from other bases
                </div>
              </div>

              {/* Transfers Out */}
              <div
                style={{
                  backgroundColor: 'var(--bg-input)',
                  padding: '1rem',
                  borderRadius: 'var(--radius-md)',
                  border: '1px solid var(--border-color)',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <span style={{ fontSize: '0.775rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                    Transfers Out (-)
                  </span>
                  <ArrowUpRight size={14} style={{ color: 'var(--danger)' }} />
                </div>
                <div style={{ fontSize: '1.35rem', fontWeight: 700, color: 'var(--danger)', marginTop: '0.25rem', fontFamily: 'var(--font-mono)' }}>
                  -{metrics.transferOut.toLocaleString()}
                </div>
                <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                  Dispatched to other bases
                </div>
              </div>

              {/* Net Movement Result */}
              <div
                style={{
                  backgroundColor: 'var(--bg-input)',
                  padding: '1rem',
                  borderRadius: 'var(--radius-md)',
                  border: '1px solid var(--border-color)',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <span style={{ fontSize: '0.775rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                    Net Movement (=)
                  </span>
                  <TrendingUp size={14} style={{ color: 'var(--primary)' }} />
                </div>
                <div
                  style={{
                    fontSize: '1.35rem',
                    fontWeight: 700,
                    color: metrics.netMovement >= 0 ? 'var(--success)' : 'var(--danger)',
                    marginTop: '0.25rem',
                    fontFamily: 'var(--font-mono)',
                  }}
                >
                  {metrics.netMovement >= 0 ? `+${metrics.netMovement.toLocaleString()}` : metrics.netMovement.toLocaleString()}
                </div>
                <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                  Purchases + In − Out
                </div>
              </div>
            </div>
          </div>
        </>
      ) : null}

      {/* Net Movement Detail Modal */}
      <Modal
        isOpen={netMovementModalOpen}
        title="Net Movement Breakdown"
        onClose={() => setNetMovementModalOpen(false)}
      >
        {metrics && (
          <div>
            <p style={{ fontSize: '0.825rem', marginBottom: '1.15rem', color: 'var(--text-secondary)' }}>
              Net Movement calculates total inventory additions and subtractions for the specified parameters:
            </p>

            <div
              style={{
                backgroundColor: 'var(--bg-input)',
                padding: '1.15rem',
                borderRadius: 'var(--radius-md)',
                marginBottom: '1.25rem',
                fontFamily: 'var(--font-mono)',
                fontSize: '0.85rem',
                border: '1px solid var(--border-color)',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                <span style={{ color: 'var(--text-primary)' }}>+ Purchases:</span>
                <span style={{ fontWeight: 600 }}>{metrics.purchases.toLocaleString()} units</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                <span style={{ color: 'var(--success)' }}>+ Transfers In:</span>
                <span style={{ fontWeight: 600 }}>{metrics.transferIn.toLocaleString()} units</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                <span style={{ color: 'var(--danger)' }}>− Transfers Out:</span>
                <span style={{ fontWeight: 600 }}>{metrics.transferOut.toLocaleString()} units</span>
              </div>
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  paddingTop: '0.75rem',
                  borderTop: '1px solid var(--border-color)',
                  fontWeight: 700,
                  fontSize: '0.95rem',
                }}
              >
                <span>= Net Movement:</span>
                <span style={{ color: metrics.netMovement >= 0 ? 'var(--success)' : 'var(--danger)' }}>
                  {metrics.netMovement >= 0 ? `+${metrics.netMovement.toLocaleString()}` : metrics.netMovement.toLocaleString()} units
                </span>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <button
                type="button"
                className="btn btn-secondary"
                style={{ minWidth: '100px' }}
                onClick={() => setNetMovementModalOpen(false)}
              >
                Close
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}
