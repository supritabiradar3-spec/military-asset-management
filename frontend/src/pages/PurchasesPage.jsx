import React, { useState, useEffect, useCallback } from 'react';
import { purchasesApi } from '../api/purchasesApi';
import { assetsApi } from '../api/assetsApi';
import { basesApi } from '../api/basesApi';
import { equipmentTypesApi } from '../api/equipmentTypesApi';
import { useAuth } from '../context/AuthContext';
import { FilterBar } from '../components/common/FilterBar';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { AlertMessage } from '../components/common/AlertMessage';
import {
  ShoppingCart,
  PlusCircle,
  Package,
} from 'lucide-react';

export function PurchasesPage() {
  const { user, isAdmin } = useAuth();

  // Lookups
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);
  const [availableAssets, setAvailableAssets] = useState([]);

  // Data
  const [purchases, setPurchases] = useState([]);
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

  // Form Mode: 'existing' vs 'new'
  const [assetMode, setAssetMode] = useState('existing');

  // Form State
  const defaultBaseId = user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '';
  const [form, setForm] = useState({
    baseId: defaultBaseId,
    assetId: '',
    assetName: '',
    equipmentTypeId: '',
    quantity: '',
    purchaseDate: new Date().toISOString().split('T')[0],
    supplier: '',
    notes: '',
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

        if (isAdmin && basesData?.length > 0 && !form.baseId) {
          setForm((prev) => ({ ...prev, baseId: String(basesData[0].id) }));
        }
      } catch (err) {
        console.error('Failed to load lookups', err);
      }
    }
    loadLookups();
  }, [isAdmin]);

  // Load assets for selected form base
  useEffect(() => {
    async function loadAssetsForBase() {
      const activeBaseId = form.baseId || user?.baseId;
      if (!activeBaseId) {
        setAvailableAssets([]);
        return;
      }
      try {
        const assetsData = await assetsApi.getAll({ baseId: activeBaseId });
        setAvailableAssets(assetsData || []);
      } catch (err) {
        console.error('Failed to load assets for base', err);
      }
    }
    loadAssetsForBase();
  }, [form.baseId, user?.baseId]);

  // Fetch Purchases History
  const fetchPurchases = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await purchasesApi.getAll(filters);
      setPurchases(data || []);
    } catch (err) {
      setError(err.message || 'Failed to load purchase records');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchPurchases();
  }, [fetchPurchases]);

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

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    const targetBaseId = isAdmin ? form.baseId : user?.baseId;
    if (!targetBaseId) {
      setError('Please select a receiving base.');
      return;
    }

    if (assetMode === 'existing' && !form.assetId) {
      setError('Please select an existing asset to restock.');
      return;
    }

    if (assetMode === 'new') {
      if (!form.assetName.trim()) {
        setError('Please enter the asset name.');
        return;
      }
      if (!form.equipmentTypeId) {
        setError('Please select an equipment type.');
        return;
      }
    }

    const qty = parseInt(form.quantity, 10);
    if (isNaN(qty) || qty <= 0) {
      setError('Quantity must be a positive integer (at least 1).');
      return;
    }

    const payload = {
      baseId: Number(targetBaseId),
      quantity: qty,
      purchaseDate: form.purchaseDate || undefined,
      supplier: form.supplier.trim() || undefined,
      notes: form.notes.trim() || undefined,
    };

    if (assetMode === 'existing') {
      payload.assetId = Number(form.assetId);
    } else {
      payload.assetName = form.assetName.trim();
      payload.equipmentTypeId = Number(form.equipmentTypeId);
    }

    setSubmitting(true);
    try {
      const created = await purchasesApi.create(payload);
      setSuccess(`Purchase #${created.id} recorded: +${created.quantity} units of "${created.assetName}" added to inventory.`);
      
      setForm((prev) => ({
        ...prev,
        assetId: '',
        assetName: '',
        equipmentTypeId: '',
        quantity: '',
        purchaseDate: new Date().toISOString().split('T')[0],
        supplier: '',
        notes: '',
      }));

      fetchPurchases();
    } catch (err) {
      setError(err.message || 'Failed to record purchase');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      {/* Header */}
      <div style={{ marginBottom: '1.25rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.75rem' }}>
        <div>
          <h1 style={{ fontSize: '1.35rem', fontWeight: 700 }}>Purchases</h1>
          <p style={{ marginTop: '0.2rem' }}>
            Record inbound asset procurement and inspect purchase transaction history
          </p>
        </div>
      </div>

      {error && <AlertMessage type="error" message={error} onClose={() => setError(null)} />}
      {success && <AlertMessage type="success" message={success} onClose={() => setSuccess(null)} />}

      {/* Procurement Form Card */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div className="card-header">
          <h3 className="card-title">
            <PlusCircle size={15} color="var(--primary)" />
            <span>New Purchase Order</span>
          </h3>
          {/* Segmented Control */}
          <div className="segmented-control">
            <button
              type="button"
              className={`segmented-button ${assetMode === 'existing' ? 'active' : ''}`}
              onClick={() => setAssetMode('existing')}
            >
              Restock Existing Asset
            </button>
            <button
              type="button"
              className={`segmented-button ${assetMode === 'new' ? 'active' : ''}`}
              onClick={() => setAssetMode('new')}
            >
              Procure New Asset
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            {/* Target Base */}
            <div className="form-group">
              <label className="form-label" htmlFor="purchase-base">
                Receiving Base <span className="form-label-required">*</span>
              </label>
              {isAdmin ? (
                <select
                  id="purchase-base"
                  name="baseId"
                  className="form-select"
                  value={form.baseId}
                  onChange={handleFormChange}
                  required
                >
                  <option value="">-- Select Base --</option>
                  {bases.map((b) => (
                    <option key={b.id} value={b.id}>
                      {b.name} ({b.location})
                    </option>
                  ))}
                </select>
              ) : (
                <input
                  id="purchase-base"
                  type="text"
                  className="form-input"
                  value={`Base #${user?.baseId || 'Assigned'}`}
                  disabled
                  readOnly
                />
              )}
            </div>

            {/* Asset Selection */}
            {assetMode === 'existing' ? (
              <div className="form-group">
                <label className="form-label" htmlFor="purchase-asset">
                  Asset to Restock <span className="form-label-required">*</span>
                </label>
                <select
                  id="purchase-asset"
                  name="assetId"
                  className="form-select"
                  value={form.assetId}
                  onChange={handleFormChange}
                  required
                >
                  <option value="">-- Select Existing Asset --</option>
                  {availableAssets.map((a) => (
                    <option key={a.id} value={a.id}>
                      {a.name} ({a.equipmentTypeName || 'Item'} — Current: {a.quantity})
                    </option>
                  ))}
                </select>
              </div>
            ) : (
              <>
                <div className="form-group">
                  <label className="form-label" htmlFor="purchase-asset-name">
                    New Asset Name <span className="form-label-required">*</span>
                  </label>
                  <input
                    id="purchase-asset-name"
                    type="text"
                    name="assetName"
                    className="form-input"
                    placeholder="e.g. Tactical Body Armor V2"
                    value={form.assetName}
                    onChange={handleFormChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label" htmlFor="purchase-eq-type">
                    Equipment Type <span className="form-label-required">*</span>
                  </label>
                  <select
                    id="purchase-eq-type"
                    name="equipmentTypeId"
                    className="form-select"
                    value={form.equipmentTypeId}
                    onChange={handleFormChange}
                    required
                  >
                    <option value="">-- Select Equipment Type --</option>
                    {equipmentTypes.map((t) => (
                      <option key={t.id} value={t.id}>
                        {t.name}
                      </option>
                    ))}
                  </select>
                </div>
              </>
            )}

            {/* Quantity */}
            <div className="form-group">
              <label className="form-label" htmlFor="purchase-quantity">
                Quantity (Units) <span className="form-label-required">*</span>
              </label>
              <input
                id="purchase-quantity"
                type="number"
                name="quantity"
                className="form-input"
                placeholder="e.g. 50"
                min="1"
                value={form.quantity}
                onChange={handleFormChange}
                required
              />
            </div>

            {/* Purchase Date */}
            <div className="form-group">
              <label className="form-label" htmlFor="purchase-date">
                Purchase Date
              </label>
              <input
                id="purchase-date"
                type="date"
                name="purchaseDate"
                className="form-input"
                value={form.purchaseDate}
                onChange={handleFormChange}
              />
            </div>

            {/* Supplier */}
            <div className="form-group">
              <label className="form-label" htmlFor="purchase-supplier">
                Supplier / Vendor
              </label>
              <input
                id="purchase-supplier"
                type="text"
                name="supplier"
                className="form-input"
                placeholder="e.g. Defense Dynamics Ltd."
                value={form.supplier}
                onChange={handleFormChange}
              />
            </div>
          </div>

          {/* Notes */}
          <div className="form-group" style={{ marginTop: '1rem' }}>
            <label className="form-label" htmlFor="purchase-notes">
              Order Notes / Reference
            </label>
            <textarea
              id="purchase-notes"
              name="notes"
              className="form-textarea"
              placeholder="Contract ID, delivery invoice number, or batch details..."
              value={form.notes}
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
              <ShoppingCart size={15} />
              <span>{submitting ? 'Recording Purchase...' : 'Record Purchase'}</span>
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

      {/* Purchase History Ledger */}
      <div className="card" style={{ padding: '0' }}>
        <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <h3 className="card-title">
            <span>Purchase History</span>
          </h3>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
            {purchases.length} {purchases.length === 1 ? 'record' : 'records'}
          </span>
        </div>

        {loading ? (
          <LoadingSpinner message="Loading purchase records..." />
        ) : purchases.length === 0 ? (
          <div className="empty-state">
            <Package size={32} style={{ color: 'var(--text-muted)', opacity: 0.5 }} />
            <p>No purchase records found for the selected filters.</p>
          </div>
        ) : (
          <div className="table-responsive" style={{ border: 'none', borderRadius: '0' }}>
            <table className="table table-purchases">
              <thead>
                <tr>
                  <th style={{ width: '80px' }}>Order #</th>
                  <th style={{ width: '110px' }}>Date</th>
                  <th>Asset Item</th>
                  <th>Equipment Type</th>
                  <th>Receiving Base</th>
                  <th style={{ textAlign: 'right', width: '100px' }}>Quantity</th>
                  <th>Supplier</th>
                  <th>Notes</th>
                  <th>Logged By</th>
                </tr>
              </thead>
              <tbody>
                {purchases.map((p) => (
                  <tr key={p.id}>
                    <td className="mono" style={{ color: 'var(--text-muted)' }}>
                      #{p.id}
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>
                      {p.purchaseDate ? p.purchaseDate.substring(0, 10) : '—'}
                    </td>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                      {p.assetName}
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>
                      {p.equipmentTypeName || '—'}
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>
                      {p.baseName ? `${p.baseName} (#${p.baseId})` : `Base #${p.baseId}`}
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--success)' }} className="mono">
                      +{p.quantity.toLocaleString()}
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>
                      {p.supplier || '—'}
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem', maxWidth: '200px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={p.notes || ''}>
                      {p.notes || '—'}
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>
                      {p.createdByUser || '—'}
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
