import React, { useState, useEffect, useCallback } from 'react';
import { transfersApi } from '../api/transfersApi';
import { assetsApi } from '../api/assetsApi';
import { basesApi } from '../api/basesApi';
import { equipmentTypesApi } from '../api/equipmentTypesApi';
import { useAuth } from '../context/AuthContext';
import { FilterBar } from '../components/common/FilterBar';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { AlertMessage } from '../components/common/AlertMessage';
import {
  ArrowLeftRight,
  Send,
  ArrowRight,
  Boxes,
} from 'lucide-react';

export function TransfersPage() {
  const { user, isAdmin } = useAuth();

  // Lookups
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);
  const [sourceAssets, setSourceAssets] = useState([]);

  // Data
  const [transfers, setTransfers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Filters
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    baseId: user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '',
    equipmentTypeId: '',
  });

  // Form State
  const defaultFromBase = user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '';
  const [form, setForm] = useState({
    fromBaseId: defaultFromBase,
    toBaseId: '',
    assetId: '',
    quantity: '',
    transferDate: new Date().toISOString().split('T')[0],
    reason: '',
  });

  // Load Lookups
  useEffect(() => {
    async function loadLookups() {
      try {
        const [basesData, eqTypesData] = await Promise.all([
          basesApi.getAll(),
          equipmentTypesApi.getAll(),
        ]);
        setBases(basesData || []);
        setEquipmentTypes(eqTypesData || []);

        if (isAdmin && basesData?.length > 1 && !form.fromBaseId) {
          setForm((prev) => ({
            ...prev,
            fromBaseId: String(basesData[0].id),
            toBaseId: String(basesData[1].id),
          }));
        }
      } catch (err) {
        console.error('Failed to load lookups', err);
      }
    }
    loadLookups();
  }, [isAdmin]);

  // Load assets available at source base
  useEffect(() => {
    async function loadSourceAssets() {
      const activeSourceBase = form.fromBaseId || user?.baseId;
      if (!activeSourceBase) {
        setSourceAssets([]);
        return;
      }
      try {
        const data = await assetsApi.getAll({ baseId: activeSourceBase });
        setSourceAssets(data || []);
      } catch (err) {
        console.error('Failed to load source assets', err);
      }
    }
    loadSourceAssets();
  }, [form.fromBaseId, user?.baseId]);

  // Fetch Transfers History
  const fetchTransfers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await transfersApi.getAll(filters);
      setTransfers(data || []);
    } catch (err) {
      setError(err.message || 'Failed to load transfer history');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchTransfers();
  }, [fetchTransfers]);

  const handleFilterReset = () => {
    setFilters({
      startDate: '',
      endDate: '',
      baseId: user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '',
      equipmentTypeId: '',
    });
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const selectedAsset = sourceAssets.find((a) => String(a.id) === String(form.assetId));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    const sourceBase = isAdmin ? form.fromBaseId : user?.baseId;
    if (!sourceBase) {
      setError('Please select an origin base.');
      return;
    }

    if (!form.toBaseId) {
      setError('Please select a destination base.');
      return;
    }

    if (String(sourceBase) === String(form.toBaseId)) {
      setError('Origin and destination base cannot be the same.');
      return;
    }

    if (!form.assetId) {
      setError('Please select an asset to transfer.');
      return;
    }

    const qty = parseInt(form.quantity, 10);
    if (isNaN(qty) || qty <= 0) {
      setError('Quantity must be a positive integer (at least 1).');
      return;
    }

    if (selectedAsset && qty > selectedAsset.quantity) {
      setError(`Requested quantity (${qty}) exceeds available stock at origin (${selectedAsset.quantity}).`);
      return;
    }

    const payload = {
      fromBaseId: Number(sourceBase),
      toBaseId: Number(form.toBaseId),
      assetId: Number(form.assetId),
      quantity: qty,
      transferDate: form.transferDate || undefined,
      reason: form.reason.trim() || undefined,
    };

    setSubmitting(true);
    try {
      const created = await transfersApi.create(payload);
      setSuccess(`Transfer #${created.id} executed: ${created.quantity} units dispatched from ${created.fromBaseName} to ${created.toBaseName}.`);

      setForm((prev) => ({
        ...prev,
        assetId: '',
        quantity: '',
        transferDate: new Date().toISOString().split('T')[0],
        reason: '',
      }));

      // Refresh source assets and history
      if (sourceBase) {
        const refreshedAssets = await assetsApi.getAll({ baseId: sourceBase });
        setSourceAssets(refreshedAssets || []);
      }
      fetchTransfers();
    } catch (err) {
      setError(err.message || 'Failed to execute transfer');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      {/* Header */}
      <div style={{ marginBottom: '1.25rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.75rem' }}>
        <div>
          <h1 style={{ fontSize: '1.35rem', fontWeight: 700 }}>Transfers</h1>
          <p style={{ marginTop: '0.2rem' }}>
            Coordinate inventory transfers between bases and inspect movement history
          </p>
        </div>
      </div>

      {error && <AlertMessage type="error" message={error} onClose={() => setError(null)} />}
      {success && <AlertMessage type="success" message={success} onClose={() => setSuccess(null)} />}

      {/* Transfer Form Card */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div className="card-header">
          <h3 className="card-title">
            <Send size={15} color="var(--primary)" />
            <span>New Asset Transfer</span>
          </h3>
        </div>

        <form onSubmit={handleSubmit}>
          {/* Base Movement Flow Row */}
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: '1fr auto 1fr',
              gap: '1rem',
              alignItems: 'center',
              backgroundColor: 'var(--bg-input)',
              padding: '1.15rem',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border-color)',
              marginBottom: '1.15rem',
            }}
          >
            {/* Origin Base */}
            <div className="form-group">
              <label className="form-label" htmlFor="transfer-from-base">
                Origin Base (Source) <span className="form-label-required">*</span>
              </label>
              {isAdmin ? (
                <select
                  id="transfer-from-base"
                  name="fromBaseId"
                  className="form-select"
                  value={form.fromBaseId}
                  onChange={handleFormChange}
                  required
                >
                  <option value="">-- Select Origin Base --</option>
                  {bases.map((b) => (
                    <option key={b.id} value={b.id}>
                      {b.name} ({b.location})
                    </option>
                  ))}
                </select>
              ) : (
                <input
                  id="transfer-from-base"
                  type="text"
                  className="form-input"
                  value={`Base #${user?.baseId || 'Assigned'}`}
                  disabled
                  readOnly
                />
              )}
            </div>

            {/* Direction Arrow */}
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', paddingTop: '1.25rem', color: 'var(--primary)' }}>
              <ArrowRight size={20} />
            </div>

            {/* Destination Base */}
            <div className="form-group">
              <label className="form-label" htmlFor="transfer-to-base">
                Destination Base (Target) <span className="form-label-required">*</span>
              </label>
              <select
                id="transfer-to-base"
                name="toBaseId"
                className="form-select"
                value={form.toBaseId}
                onChange={handleFormChange}
                required
              >
                <option value="">-- Select Destination Base --</option>
                {bases
                  .filter((b) => String(b.id) !== String(form.fromBaseId || user?.baseId))
                  .map((b) => (
                    <option key={b.id} value={b.id}>
                      {b.name} ({b.location})
                    </option>
                  ))}
              </select>
            </div>
          </div>

          {/* Details Row */}
          <div className="form-grid">
            {/* Asset Selection */}
            <div className="form-group">
              <label className="form-label" htmlFor="transfer-asset">
                Asset Item <span className="form-label-required">*</span>
              </label>
              <select
                id="transfer-asset"
                name="assetId"
                className="form-select"
                value={form.assetId}
                onChange={handleFormChange}
                required
              >
                <option value="">-- Select Asset to Transfer --</option>
                {sourceAssets.map((a) => (
                  <option key={a.id} value={a.id} disabled={a.quantity <= 0}>
                    {a.name} ({a.equipmentTypeName || 'Item'}) — In Stock: {a.quantity}
                  </option>
                ))}
              </select>
              {selectedAsset && (
                <span style={{ fontSize: '0.725rem', color: 'var(--text-secondary)' }}>
                  Current stock at origin: <strong style={{ color: selectedAsset.quantity > 0 ? 'var(--text-primary)' : 'var(--danger)' }}>{selectedAsset.quantity} units</strong>
                </span>
              )}
            </div>

            {/* Quantity */}
            <div className="form-group">
              <label className="form-label" htmlFor="transfer-quantity">
                Transfer Quantity <span className="form-label-required">*</span>
              </label>
              <input
                id="transfer-quantity"
                type="number"
                name="quantity"
                className="form-input"
                placeholder="e.g. 25"
                min="1"
                max={selectedAsset ? selectedAsset.quantity : undefined}
                value={form.quantity}
                onChange={handleFormChange}
                required
              />
            </div>

            {/* Transfer Date */}
            <div className="form-group">
              <label className="form-label" htmlFor="transfer-date">
                Transfer Date
              </label>
              <input
                id="transfer-date"
                type="date"
                name="transferDate"
                className="form-input"
                value={form.transferDate}
                onChange={handleFormChange}
              />
            </div>
          </div>

          {/* Transfer Reason */}
          <div className="form-group" style={{ marginTop: '1rem' }}>
            <label className="form-label" htmlFor="transfer-reason">
              Reason / Transfer Justification
            </label>
            <textarea
              id="transfer-reason"
              name="reason"
              className="form-textarea"
              placeholder="Operational deployment, inventory rebalancing, or base redeployment order..."
              value={form.reason}
              onChange={handleFormChange}
              rows={2}
            />
          </div>

          <div style={{ marginTop: '1.25rem', display: 'flex', justifyContent: 'flex-end' }}>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={submitting}
            >
              <ArrowLeftRight size={15} />
              <span>{submitting ? 'Executing Transfer...' : 'Execute Transfer'}</span>
            </button>
          </div>
        </form>
      </div>

      {/* Filter Component */}
      <FilterBar
        filters={filters}
        onFilterChange={setFilters}
        onReset={handleFilterReset}
        bases={bases}
        equipmentTypes={equipmentTypes}
        userRole={user?.role}
        userBaseId={user?.baseId}
      />

      {/* Transfer History Table */}
      <div className="card" style={{ padding: '0' }}>
        <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <h3 className="card-title">
            <span>Transfer History</span>
          </h3>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
            {transfers.length} {transfers.length === 1 ? 'record' : 'records'}
          </span>
        </div>

        {loading ? (
          <LoadingSpinner message="Loading transfer records..." />
        ) : transfers.length === 0 ? (
          <div className="empty-state">
            <Boxes size={32} style={{ color: 'var(--text-muted)', opacity: 0.5 }} />
            <p>No transfers found for the selected filters.</p>
          </div>
        ) : (
          <div className="table-responsive" style={{ border: 'none', borderRadius: '0' }}>
            <table className="table table-transfers">
              <thead>
                <tr>
                  <th style={{ width: '80px' }}>ID #</th>
                  <th style={{ width: '110px' }}>Date</th>
                  <th>Asset Item</th>
                  <th>Origin Base</th>
                  <th style={{ width: '30px', textAlign: 'center' }}></th>
                  <th>Destination Base</th>
                  <th style={{ textAlign: 'right', width: '90px' }}>Quantity</th>
                  <th>Reason</th>
                  <th>Authorized By</th>
                </tr>
              </thead>
              <tbody>
                {transfers.map((t) => (
                  <tr key={t.id}>
                    <td className="mono" style={{ color: 'var(--text-muted)' }}>
                      #{t.id}
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>
                      {t.transferDate ? t.transferDate.substring(0, 10) : '—'}
                    </td>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                      {t.assetName}
                    </td>
                    <td>
                      <span style={{ color: 'var(--text-secondary)' }}>
                        {t.fromBaseName}
                      </span>
                    </td>
                    <td style={{ textAlign: 'center', color: 'var(--primary)' }}>
                      &rarr;
                    </td>
                    <td>
                      <span style={{ color: 'var(--text-secondary)' }}>
                        {t.toBaseName}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--text-primary)' }} className="mono">
                      {t.quantity.toLocaleString()}
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem', maxWidth: '220px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={t.reason || ''}>
                      {t.reason || '—'}
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>
                      {t.authorizedByUser || '—'}
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
