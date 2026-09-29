import React, { useState, useEffect, useCallback } from 'react';
import { assetsApi } from '../api/assetsApi';
import { basesApi } from '../api/basesApi';
import { equipmentTypesApi } from '../api/equipmentTypesApi';
import { useAuth } from '../context/AuthContext';
import { Modal } from '../components/common/Modal';
import { FilterBar } from '../components/common/FilterBar';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { AlertMessage } from '../components/common/AlertMessage';
import {
  Boxes,
  Plus,
  Building,
  Layers,
  Box,
} from 'lucide-react';

export function AssetsPage() {
  const { user, isAdmin } = useAuth();

  // Data
  const [assets, setAssets] = useState([]);
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Filters
  const [filters, setFilters] = useState({
    baseId: user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '',
    equipmentTypeId: '',
  });

  // Modals state for ADMIN creation
  const [showCreateAssetModal, setShowCreateAssetModal] = useState(false);
  const [showCreateBaseModal, setShowCreateBaseModal] = useState(false);
  const [showCreateEqTypeModal, setShowCreateEqTypeModal] = useState(false);
  const [modalSubmitting, setModalSubmitting] = useState(false);
  const [modalError, setModalError] = useState(null);

  // Form states for modals
  const [assetForm, setAssetForm] = useState({
    name: '',
    equipmentTypeId: '',
    baseId: '',
    quantity: 0,
  });

  const [baseForm, setBaseForm] = useState({
    name: '',
    location: '',
  });

  const [eqTypeForm, setEqTypeForm] = useState({
    name: '',
    description: '',
  });

  // Load Lookups
  const loadLookups = useCallback(async () => {
    try {
      const [basesData, eqTypesData] = await Promise.all([
        basesApi.getAll(),
        equipmentTypesApi.getAll(),
      ]);
      setBases(basesData || []);
      setEquipmentTypes(eqTypesData || []);
    } catch (err) {
      console.error('Failed to load lookup lists', err);
    }
  }, []);

  useEffect(() => {
    loadLookups();
  }, [loadLookups]);

  // Fetch Assets
  const fetchAssets = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await assetsApi.getAll(filters);
      setAssets(data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch asset inventory');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchAssets();
  }, [fetchAssets]);

  const handleFilterReset = () => {
    setFilters({
      baseId: user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '',
      equipmentTypeId: '',
    });
  };

  // Submit New Asset (ADMIN)
  const handleCreateAsset = async (e) => {
    e.preventDefault();
    setModalError(null);

    if (!assetForm.name.trim() || !assetForm.equipmentTypeId || !assetForm.baseId) {
      setModalError('Please fill in all required fields.');
      return;
    }

    setModalSubmitting(true);
    try {
      const payload = {
        name: assetForm.name.trim(),
        equipmentTypeId: Number(assetForm.equipmentTypeId),
        baseId: Number(assetForm.baseId),
        quantity: parseInt(assetForm.quantity, 10) || 0,
      };
      await assetsApi.create(payload);
      setSuccess(`Asset item "${payload.name}" successfully registered in inventory.`);
      setShowCreateAssetModal(false);
      setAssetForm({ name: '', equipmentTypeId: '', baseId: '', quantity: 0 });
      fetchAssets();
    } catch (err) {
      setModalError(err.message || 'Failed to create asset item.');
    } finally {
      setModalSubmitting(false);
    }
  };

  // Submit New Base (ADMIN)
  const handleCreateBase = async (e) => {
    e.preventDefault();
    setModalError(null);

    if (!baseForm.name.trim()) {
      setModalError('Base name is required.');
      return;
    }

    setModalSubmitting(true);
    try {
      const payload = {
        name: baseForm.name.trim(),
        location: baseForm.location.trim() || undefined,
      };
      await basesApi.create(payload);
      setSuccess(`Military base "${payload.name}" successfully created.`);
      setShowCreateBaseModal(false);
      setBaseForm({ name: '', location: '' });
      loadLookups();
    } catch (err) {
      setModalError(err.message || 'Failed to create base.');
    } finally {
      setModalSubmitting(false);
    }
  };

  // Submit New Equipment Type (ADMIN)
  const handleCreateEqType = async (e) => {
    e.preventDefault();
    setModalError(null);

    if (!eqTypeForm.name.trim()) {
      setModalError('Equipment type name is required.');
      return;
    }

    setModalSubmitting(true);
    try {
      const payload = {
        name: eqTypeForm.name.trim(),
        description: eqTypeForm.description.trim() || undefined,
      };
      await equipmentTypesApi.create(payload);
      setSuccess(`Equipment category "${payload.name}" successfully created.`);
      setShowCreateEqTypeModal(false);
      setEqTypeForm({ name: '', description: '' });
      loadLookups();
    } catch (err) {
      setModalError(err.message || 'Failed to create equipment category.');
    } finally {
      setModalSubmitting(false);
    }
  };

  const getStatusBadge = (quantity) => {
    if (quantity <= 0) {
      return <span className="badge badge-danger">Depleted</span>;
    }
    if (quantity < 10) {
      return <span className="badge badge-warning">Low Reserve</span>;
    }
    return <span className="badge badge-success">Operational</span>;
  };

  return (
    <div>
      {/* Header & Actions */}
      <div style={{ marginBottom: '1.25rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.75rem' }}>
        <div>
          <h1 style={{ fontSize: '1.35rem', fontWeight: 700 }}>Asset Inventory</h1>
          <p style={{ marginTop: '0.2rem' }}>
            Master inventory ledger, base distribution, and ready stock levels
          </p>
        </div>

        {/* Master Data Creation Actions (Admin Only) */}
        {isAdmin && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={() => {
                setModalError(null);
                setShowCreateBaseModal(true);
              }}
            >
              <Building size={14} />
              <span>+ New Base</span>
            </button>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={() => {
                setModalError(null);
                setShowCreateEqTypeModal(true);
              }}
            >
              <Layers size={14} />
              <span>+ New Category</span>
            </button>
            <button
              type="button"
              className="btn btn-primary btn-sm"
              onClick={() => {
                setModalError(null);
                setAssetForm({
                  name: '',
                  equipmentTypeId: equipmentTypes[0]?.id ? String(equipmentTypes[0].id) : '',
                  baseId: bases[0]?.id ? String(bases[0].id) : '',
                  quantity: 0,
                });
                setShowCreateAssetModal(true);
              }}
            >
              <Plus size={14} />
              <span>+ New Asset Item</span>
            </button>
          </div>
        )}
      </div>

      {error && <AlertMessage type="error" message={error} onClose={() => setError(null)} />}
      {success && <AlertMessage type="success" message={success} onClose={() => setSuccess(null)} />}

      {/* Filter Component */}
      <FilterBar
        filters={filters}
        onFilterChange={setFilters}
        onReset={handleFilterReset}
        bases={bases}
        equipmentTypes={equipmentTypes}
        userRole={user?.role}
        userBaseId={user?.baseId}
        showDateRange={false}
      />

      {/* Master Inventory Grid Table */}
      <div className="card" style={{ padding: '0' }}>
        <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <h3 className="card-title">
            <span>Inventory Records</span>
          </h3>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
            {assets.length} {assets.length === 1 ? 'item' : 'items'}
          </span>
        </div>

        {loading ? (
          <LoadingSpinner message="Loading asset inventory records..." />
        ) : assets.length === 0 ? (
          <div className="empty-state">
            <Boxes size={32} style={{ color: 'var(--text-muted)', opacity: 0.5 }} />
            <p>No asset inventory items found matching the selected filters.</p>
          </div>
        ) : (
          <div className="table-responsive" style={{ border: 'none', borderRadius: '0' }}>
            <table className="table table-assets">
              <thead>
                <tr>
                  <th style={{ width: '70px' }}>ID #</th>
                  <th>Asset Item</th>
                  <th>Equipment Category</th>
                  <th>Assigned Base</th>
                  <th style={{ textAlign: 'right', width: '100px' }}>Total Stock</th>
                  <th style={{ textAlign: 'right', width: '110px' }}>Field Assigned</th>
                  <th style={{ textAlign: 'right', width: '120px' }}>Available / Ready</th>
                  <th style={{ textAlign: 'center', width: '120px' }}>Stock Status</th>
                  <th style={{ width: '130px' }}>Last Updated</th>
                </tr>
              </thead>
              <tbody>
                {assets.map((asset) => {
                  const assignedCount = asset.assignedQuantity || 0;
                  const availableCount = Math.max(0, asset.quantity - assignedCount);

                  return (
                    <tr key={asset.id}>
                      <td className="mono" style={{ color: 'var(--text-muted)' }}>
                        #{asset.id}
                      </td>
                      <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                        {asset.name}
                      </td>
                      <td style={{ color: 'var(--text-secondary)' }}>
                        {asset.equipmentTypeName || 'General Equipment'}
                      </td>
                      <td style={{ color: 'var(--text-secondary)' }}>
                        {asset.baseName ? `${asset.baseName} (#${asset.baseId})` : `Base #${asset.baseId}`}
                      </td>
                      {/* Total Stock */}
                      <td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--text-primary)' }} className="mono">
                        {asset.quantity.toLocaleString()}
                      </td>
                      {/* Field Assigned */}
                      <td style={{ textAlign: 'right', fontWeight: 600, color: assignedCount > 0 ? 'var(--warning)' : 'var(--text-muted)' }} className="mono">
                        {assignedCount.toLocaleString()}
                      </td>
                      {/* Available / Ready */}
                      <td style={{ textAlign: 'right', fontWeight: 700, color: availableCount > 0 ? 'var(--success)' : 'var(--danger)' }} className="mono">
                        {availableCount.toLocaleString()}
                      </td>
                      {/* Status Badge */}
                      <td style={{ textAlign: 'center' }}>
                        {getStatusBadge(asset.quantity)}
                      </td>
                      <td style={{ color: 'var(--text-muted)', fontSize: '0.775rem' }}>
                        {asset.updatedAt ? new Date(asset.updatedAt).toLocaleDateString() : '—'}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* CREATE ASSET MODAL */}
      <Modal
        isOpen={showCreateAssetModal}
        title="Register New Master Asset Item"
        onClose={() => setShowCreateAssetModal(false)}
      >
        {modalError && <AlertMessage type="error" message={modalError} onClose={() => setModalError(null)} />}
        <form onSubmit={handleCreateAsset}>
          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label className="form-label" htmlFor="new-asset-name">
              Asset Item Name <span className="form-label-required">*</span>
            </label>
            <input
              id="new-asset-name"
              type="text"
              className="form-input"
              placeholder="e.g. M4A1 Tactical Carbine"
              value={assetForm.name}
              onChange={(e) => setAssetForm((prev) => ({ ...prev, name: e.target.value }))}
              required
              autoFocus
            />
          </div>

          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label className="form-label" htmlFor="new-asset-type">
              Equipment Category <span className="form-label-required">*</span>
            </label>
            <select
              id="new-asset-type"
              className="form-select"
              value={assetForm.equipmentTypeId}
              onChange={(e) => setAssetForm((prev) => ({ ...prev, equipmentTypeId: e.target.value }))}
              required
            >
              <option value="">-- Select Category --</option>
              {equipmentTypes.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.name}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label className="form-label" htmlFor="new-asset-base">
              Initial Station Base <span className="form-label-required">*</span>
            </label>
            <select
              id="new-asset-base"
              className="form-select"
              value={assetForm.baseId}
              onChange={(e) => setAssetForm((prev) => ({ ...prev, baseId: e.target.value }))}
              required
            >
              <option value="">-- Select Base --</option>
              {bases.map((b) => (
                <option key={b.id} value={b.id}>
                  {b.name} ({b.location})
                </option>
              ))}
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: '1.25rem' }}>
            <label className="form-label" htmlFor="new-asset-qty">
              Initial Quantity Stock
            </label>
            <input
              id="new-asset-qty"
              type="number"
              className="form-input"
              min="0"
              value={assetForm.quantity}
              onChange={(e) => setAssetForm((prev) => ({ ...prev, quantity: e.target.value }))}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setShowCreateAssetModal(false)}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={modalSubmitting}
            >
              {modalSubmitting ? 'Registering...' : 'Register Asset'}
            </button>
          </div>
        </form>
      </Modal>

      {/* CREATE BASE MODAL */}
      <Modal
        isOpen={showCreateBaseModal}
        title="Establish New Military Base"
        onClose={() => setShowCreateBaseModal(false)}
      >
        {modalError && <AlertMessage type="error" message={modalError} onClose={() => setModalError(null)} />}
        <form onSubmit={handleCreateBase}>
          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label className="form-label" htmlFor="new-base-name">
              Base Station Name <span className="form-label-required">*</span>
            </label>
            <input
              id="new-base-name"
              type="text"
              className="form-input"
              placeholder="e.g. Forward Operating Base Charlie"
              value={baseForm.name}
              onChange={(e) => setBaseForm((prev) => ({ ...prev, name: e.target.value }))}
              required
              autoFocus
            />
          </div>

          <div className="form-group" style={{ marginBottom: '1.25rem' }}>
            <label className="form-label" htmlFor="new-base-loc">
              Geographic Location / Coordinates
            </label>
            <input
              id="new-base-loc"
              type="text"
              className="form-input"
              placeholder="e.g. Sector 7, Eastern Command"
              value={baseForm.location}
              onChange={(e) => setBaseForm((prev) => ({ ...prev, location: e.target.value }))}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setShowCreateBaseModal(false)}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={modalSubmitting}
            >
              {modalSubmitting ? 'Creating...' : 'Create Base'}
            </button>
          </div>
        </form>
      </Modal>

      {/* CREATE EQUIPMENT TYPE MODAL */}
      <Modal
        isOpen={showCreateEqTypeModal}
        title="Define Equipment Category"
        onClose={() => setShowCreateEqTypeModal(false)}
      >
        {modalError && <AlertMessage type="error" message={modalError} onClose={() => setModalError(null)} />}
        <form onSubmit={handleCreateEqType}>
          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label className="form-label" htmlFor="new-eq-name">
              Category Name <span className="form-label-required">*</span>
            </label>
            <input
              id="new-eq-name"
              type="text"
              className="form-input"
              placeholder="e.g. Optical & Surveillance Gear"
              value={eqTypeForm.name}
              onChange={(e) => setEqTypeForm((prev) => ({ ...prev, name: e.target.value }))}
              required
              autoFocus
            />
          </div>

          <div className="form-group" style={{ marginBottom: '1.25rem' }}>
            <label className="form-label" htmlFor="new-eq-desc">
              Category Description
            </label>
            <textarea
              id="new-eq-desc"
              className="form-textarea"
              placeholder="Operational classification and inventory handling guidelines..."
              value={eqTypeForm.description}
              onChange={(e) => setEqTypeForm((prev) => ({ ...prev, description: e.target.value }))}
              rows={2}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setShowCreateEqTypeModal(false)}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={modalSubmitting}
            >
              {modalSubmitting ? 'Creating...' : 'Create Category'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
