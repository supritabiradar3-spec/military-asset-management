import React, { useState, useEffect, useCallback } from 'react';
import { assignmentsApi } from '../api/assignmentsApi';
import { expendituresApi } from '../api/expendituresApi';
import { assetsApi } from '../api/assetsApi';
import { basesApi } from '../api/basesApi';
import { equipmentTypesApi } from '../api/equipmentTypesApi';
import { useAuth } from '../context/AuthContext';
import { FilterBar } from '../components/common/FilterBar';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { AlertMessage } from '../components/common/AlertMessage';
import {
  UserCheck,
  Flame,
  PlusCircle,
  ClipboardList,
  ShieldAlert,
} from 'lucide-react';

export function AssignmentsExpendituresPage() {
  const { user, isAdmin, isBaseCommander } = useAuth();

  const [activeTab, setActiveTab] = useState('assignments');

  // RBAC permission check for creation
  const canCreate = isAdmin || isBaseCommander;

  // Lookups
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);
  const [baseAssets, setBaseAssets] = useState([]);

  // Data
  const [assignments, setAssignments] = useState([]);
  const [expenditures, setExpenditures] = useState([]);
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

  // Assignment Form State
  const defaultBaseId = user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '';
  const [assignForm, setAssignForm] = useState({
    baseId: defaultBaseId,
    assetId: '',
    personnelName: '',
    personnelIdentifier: '',
    quantity: '',
    assignmentDate: new Date().toISOString().split('T')[0],
    notes: '',
  });

  // Expenditure Form State
  const [expendForm, setExpendForm] = useState({
    baseId: defaultBaseId,
    assetId: '',
    quantity: '',
    reason: '',
    expenditureDate: new Date().toISOString().split('T')[0],
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

        if (isAdmin && basesData?.length > 0 && !assignForm.baseId) {
          setAssignForm((prev) => ({ ...prev, baseId: String(basesData[0].id) }));
          setExpendForm((prev) => ({ ...prev, baseId: String(basesData[0].id) }));
        }
      } catch (err) {
        console.error('Failed to load lookups', err);
      }
    }
    loadLookups();
  }, [isAdmin]);

  // Load assets for selected base
  const selectedBaseId = activeTab === 'assignments' ? (assignForm.baseId || user?.baseId) : (expendForm.baseId || user?.baseId);
  useEffect(() => {
    async function loadBaseAssets() {
      if (!selectedBaseId) {
        setBaseAssets([]);
        return;
      }
      try {
        const data = await assetsApi.getAll({ baseId: selectedBaseId });
        setBaseAssets(data || []);
      } catch (err) {
        console.error('Failed to load base assets', err);
      }
    }
    loadBaseAssets();
  }, [selectedBaseId]);

  // Fetch History based on tab
  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      if (activeTab === 'assignments') {
        const data = await assignmentsApi.getAll(filters);
        setAssignments(data || []);
      } else {
        const data = await expendituresApi.getAll(filters);
        setExpenditures(data || []);
      }
    } catch (err) {
      setError(err.message || `Failed to load ${activeTab} records`);
    } finally {
      setLoading(false);
    }
  }, [activeTab, filters]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleFilterReset = () => {
    setFilters({
      startDate: '',
      endDate: '',
      baseId: user?.role !== 'ADMIN' ? (user?.baseId ? String(user.baseId) : '') : '',
      equipmentTypeId: '',
    });
  };

  // Submit Assignment
  const handleAssignSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    const targetBaseId = isAdmin ? assignForm.baseId : user?.baseId;
    if (!targetBaseId) {
      setError('Please select a base station.');
      return;
    }
    if (!assignForm.assetId) {
      setError('Please select an asset to assign.');
      return;
    }
    if (!assignForm.personnelName.trim()) {
      setError('Please enter the recipient personnel name.');
      return;
    }

    const qty = parseInt(assignForm.quantity, 10);
    if (isNaN(qty) || qty <= 0) {
      setError('Quantity must be a positive integer (at least 1).');
      return;
    }

    const selectedAsset = baseAssets.find((a) => String(a.id) === String(assignForm.assetId));
    if (selectedAsset && qty > selectedAsset.quantity) {
      setError(`Requested quantity (${qty}) exceeds available stock on base (${selectedAsset.quantity}).`);
      return;
    }

    const payload = {
      baseId: Number(targetBaseId),
      assetId: Number(assignForm.assetId),
      personnelName: assignForm.personnelName.trim(),
      personnelIdentifier: assignForm.personnelIdentifier.trim() || undefined,
      quantity: qty,
      assignmentDate: assignForm.assignmentDate || undefined,
      notes: assignForm.notes.trim() || undefined,
    };

    setSubmitting(true);
    try {
      const created = await assignmentsApi.create(payload);
      setSuccess(`Assignment #${created.id} recorded: ${created.quantity} units assigned to ${created.personnelName}.`);
      
      setAssignForm((prev) => ({
        ...prev,
        assetId: '',
        personnelName: '',
        personnelIdentifier: '',
        quantity: '',
        assignmentDate: new Date().toISOString().split('T')[0],
        notes: '',
      }));

      // Refresh base assets
      if (targetBaseId) {
        const refreshed = await assetsApi.getAll({ baseId: targetBaseId });
        setBaseAssets(refreshed || []);
      }
      fetchData();
    } catch (err) {
      setError(err.message || 'Failed to record assignment');
    } finally {
      setSubmitting(false);
    }
  };

  // Submit Expenditure
  const handleExpendSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    const targetBaseId = isAdmin ? expendForm.baseId : user?.baseId;
    if (!targetBaseId) {
      setError('Please select a base station.');
      return;
    }
    if (!expendForm.assetId) {
      setError('Please select an asset item.');
      return;
    }

    const qty = parseInt(expendForm.quantity, 10);
    if (isNaN(qty) || qty <= 0) {
      setError('Quantity must be a positive integer (at least 1).');
      return;
    }

    const selectedAsset = baseAssets.find((a) => String(a.id) === String(expendForm.assetId));
    if (selectedAsset && qty > selectedAsset.quantity) {
      setError(`Expenditure quantity (${qty}) exceeds available stock (${selectedAsset.quantity}).`);
      return;
    }

    const payload = {
      baseId: Number(targetBaseId),
      assetId: Number(expendForm.assetId),
      quantity: qty,
      reason: expendForm.reason.trim() || undefined,
      expenditureDate: expendForm.expenditureDate || undefined,
    };

    setSubmitting(true);
    try {
      const created = await expendituresApi.create(payload);
      setSuccess(`Expenditure #${created.id} recorded: -${created.quantity} units consumed/expended.`);
      
      setExpendForm((prev) => ({
        ...prev,
        assetId: '',
        quantity: '',
        reason: '',
        expenditureDate: new Date().toISOString().split('T')[0],
      }));

      // Refresh base assets
      if (targetBaseId) {
        const refreshed = await assetsApi.getAll({ baseId: targetBaseId });
        setBaseAssets(refreshed || []);
      }
      fetchData();
    } catch (err) {
      setError(err.message || 'Failed to record expenditure');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      {/* Header */}
      <div style={{ marginBottom: '1.25rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.75rem' }}>
        <div>
          <h1 style={{ fontSize: '1.35rem', fontWeight: 700 }}>Assignments & Expenditures</h1>
          <p style={{ marginTop: '0.2rem' }}>
            Track equipment issued to personnel and log consumed or expended inventory
          </p>
        </div>
      </div>

      {error && <AlertMessage type="error" message={error} onClose={() => setError(null)} />}
      {success && <AlertMessage type="success" message={success} onClose={() => setSuccess(null)} />}

      {/* Tabs */}
      <div className="tabs-container">
        <button
          type="button"
          className={`tab-button ${activeTab === 'assignments' ? 'active' : ''}`}
          onClick={() => {
            setActiveTab('assignments');
            setError(null);
            setSuccess(null);
          }}
        >
          <UserCheck size={15} style={{ color: activeTab === 'assignments' ? 'var(--warning)' : 'var(--text-muted)' }} />
          <span>Field Assignments</span>
        </button>
        <button
          type="button"
          className={`tab-button ${activeTab === 'expenditures' ? 'active' : ''}`}
          onClick={() => {
            setActiveTab('expenditures');
            setError(null);
            setSuccess(null);
          }}
        >
          <Flame size={15} style={{ color: activeTab === 'expenditures' ? 'var(--danger)' : 'var(--text-muted)' }} />
          <span>Expenditures & Consumption</span>
        </button>
      </div>

      {/* TAB 1: FIELD ASSIGNMENTS */}
      {activeTab === 'assignments' && (
        <>
          {/* Assignment Creation Form (Role Gated) */}
          {canCreate ? (
            <div className="card" style={{ marginBottom: '1.5rem' }}>
              <div className="card-header">
                <h3 className="card-title">
                  <PlusCircle size={15} color="var(--primary)" />
                  <span>Issue Equipment Assignment</span>
                </h3>
              </div>

              <form onSubmit={handleAssignSubmit}>
                <div className="form-grid">
                  {/* Base Station */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="assign-base">
                      Base <span className="form-label-required">*</span>
                    </label>
                    {isAdmin ? (
                      <select
                        id="assign-base"
                        className="form-select"
                        value={assignForm.baseId}
                        onChange={(e) => setAssignForm((prev) => ({ ...prev, baseId: e.target.value }))}
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
                        id="assign-base"
                        type="text"
                        className="form-input"
                        value={`Base #${user?.baseId || 'Assigned'}`}
                        disabled
                        readOnly
                      />
                    )}
                  </div>

                  {/* Asset Item */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="assign-asset">
                      Asset Item <span className="form-label-required">*</span>
                    </label>
                    <select
                      id="assign-asset"
                      className="form-select"
                      value={assignForm.assetId}
                      onChange={(e) => setAssignForm((prev) => ({ ...prev, assetId: e.target.value }))}
                      required
                    >
                      <option value="">-- Select Asset --</option>
                      {baseAssets.map((a) => (
                        <option key={a.id} value={a.id} disabled={a.quantity <= 0}>
                          {a.name} ({a.equipmentTypeName || 'Item'}) — Stock: {a.quantity}
                        </option>
                      ))}
                    </select>
                  </div>

                  {/* Personnel Name */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="assign-personnel-name">
                      Personnel Name <span className="form-label-required">*</span>
                    </label>
                    <input
                      id="assign-personnel-name"
                      type="text"
                      className="form-input"
                      placeholder="e.g. Sgt. Marcus Vance"
                      value={assignForm.personnelName}
                      onChange={(e) => setAssignForm((prev) => ({ ...prev, personnelName: e.target.value }))}
                      required
                    />
                  </div>

                  {/* Personnel Identifier */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="assign-personnel-id">
                      Service / Badge ID
                    </label>
                    <input
                      id="assign-personnel-id"
                      type="text"
                      className="form-input"
                      placeholder="e.g. MIL-77492-B"
                      value={assignForm.personnelIdentifier}
                      onChange={(e) => setAssignForm((prev) => ({ ...prev, personnelIdentifier: e.target.value }))}
                    />
                  </div>

                  {/* Quantity */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="assign-quantity">
                      Quantity <span className="form-label-required">*</span>
                    </label>
                    <input
                      id="assign-quantity"
                      type="number"
                      className="form-input"
                      placeholder="e.g. 1"
                      min="1"
                      value={assignForm.quantity}
                      onChange={(e) => setAssignForm((prev) => ({ ...prev, quantity: e.target.value }))}
                      required
                    />
                  </div>

                  {/* Assignment Date */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="assign-date">
                      Assignment Date
                    </label>
                    <input
                      id="assign-date"
                      type="date"
                      className="form-input"
                      value={assignForm.assignmentDate}
                      onChange={(e) => setAssignForm((prev) => ({ ...prev, assignmentDate: e.target.value }))}
                    />
                  </div>
                </div>

                {/* Notes */}
                <div className="form-group" style={{ marginTop: '1rem' }}>
                  <label className="form-label" htmlFor="assign-notes">
                    Deployment Notes / Mission Scope
                  </label>
                  <textarea
                    id="assign-notes"
                    className="form-textarea"
                    placeholder="Field operation assignment, mission details, or unit assignment notes..."
                    value={assignForm.notes}
                    onChange={(e) => setAssignForm((prev) => ({ ...prev, notes: e.target.value }))}
                    rows={2}
                  />
                </div>

                <div style={{ marginTop: '1.25rem', display: 'flex', justifyContent: 'flex-end' }}>
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={submitting}
                  >
                    <UserCheck size={15} />
                    <span>{submitting ? 'Assigning...' : 'Assign Equipment'}</span>
                  </button>
                </div>
              </form>
            </div>
          ) : (
            <div className="alert alert-info" style={{ marginBottom: '1.5rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <ShieldAlert size={16} />
                <span>Read-Only Access: Equipment assignment requires Base Commander or Administrator privileges.</span>
              </div>
            </div>
          )}

          {/* Filter Bar */}
          <FilterBar
            filters={filters}
            onFilterChange={setFilters}
            onReset={handleFilterReset}
            bases={bases}
            equipmentTypes={equipmentTypes}
            userRole={user?.role}
            userBaseId={user?.baseId}
          />

          {/* Assignments History Table */}
          <div className="card" style={{ padding: '0' }}>
            <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <h3 className="card-title">
                <span>Assignment History</span>
              </h3>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                {assignments.length} {assignments.length === 1 ? 'record' : 'records'}
              </span>
            </div>

            {loading ? (
              <LoadingSpinner message="Loading assignment records..." />
            ) : assignments.length === 0 ? (
              <div className="empty-state">
                <ClipboardList size={32} style={{ color: 'var(--text-muted)', opacity: 0.5 }} />
                <p>No assignment records found for the selected filters.</p>
              </div>
            ) : (
              <div className="table-responsive" style={{ border: 'none', borderRadius: '0' }}>
                <table className="table table-assignments">
                  <thead>
                    <tr>
                      <th style={{ width: '80px' }}>ID #</th>
                      <th style={{ width: '110px' }}>Date</th>
                      <th>Asset Item</th>
                      <th>Base Station</th>
                      <th>Personnel</th>
                      <th>Service / Badge #</th>
                      <th style={{ textAlign: 'right', width: '90px' }}>Quantity</th>
                      <th>Notes</th>
                      <th>Assigned By</th>
                    </tr>
                  </thead>
                  <tbody>
                    {assignments.map((a) => (
                      <tr key={a.id}>
                        <td className="mono" style={{ color: 'var(--text-muted)' }}>
                          #{a.id}
                        </td>
                        <td style={{ color: 'var(--text-secondary)' }}>
                          {a.assignmentDate ? a.assignmentDate.substring(0, 10) : '—'}
                        </td>
                        <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                          {a.assetName}
                        </td>
                        <td style={{ color: 'var(--text-secondary)' }}>
                          {a.baseName ? `${a.baseName} (#${a.baseId})` : `Base #${a.baseId}`}
                        </td>
                        <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                          {a.personnelName}
                        </td>
                        <td className="mono" style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>
                          {a.personnelIdentifier || '—'}
                        </td>
                        <td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--warning)' }} className="mono">
                          {a.quantity.toLocaleString()}
                        </td>
                        <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem', maxWidth: '200px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={a.notes || ''}>
                          {a.notes || '—'}
                        </td>
                        <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>
                          {a.createdByUser || '—'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}

      {/* TAB 2: EXPENDITURES & CONSUMPTION */}
      {activeTab === 'expenditures' && (
        <>
          {/* Expenditure Creation Form (Role Gated) */}
          {canCreate ? (
            <div className="card" style={{ marginBottom: '1.5rem' }}>
              <div className="card-header">
                <h3 className="card-title">
                  <PlusCircle size={15} color="var(--primary)" />
                  <span>Record Equipment Expenditure / Consumption</span>
                </h3>
              </div>

              <form onSubmit={handleExpendSubmit}>
                <div className="form-grid">
                  {/* Base Station */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="expend-base">
                      Base <span className="form-label-required">*</span>
                    </label>
                    {isAdmin ? (
                      <select
                        id="expend-base"
                        className="form-select"
                        value={expendForm.baseId}
                        onChange={(e) => setExpendForm((prev) => ({ ...prev, baseId: e.target.value }))}
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
                        id="expend-base"
                        type="text"
                        className="form-input"
                        value={`Base #${user?.baseId || 'Assigned'}`}
                        disabled
                        readOnly
                      />
                    )}
                  </div>

                  {/* Asset Item */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="expend-asset">
                      Asset Item <span className="form-label-required">*</span>
                    </label>
                    <select
                      id="expend-asset"
                      className="form-select"
                      value={expendForm.assetId}
                      onChange={(e) => setExpendForm((prev) => ({ ...prev, assetId: e.target.value }))}
                      required
                    >
                      <option value="">-- Select Asset --</option>
                      {baseAssets.map((a) => (
                        <option key={a.id} value={a.id} disabled={a.quantity <= 0}>
                          {a.name} ({a.equipmentTypeName || 'Item'}) — Stock: {a.quantity}
                        </option>
                      ))}
                    </select>
                  </div>

                  {/* Quantity */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="expend-quantity">
                      Quantity Expended <span className="form-label-required">*</span>
                    </label>
                    <input
                      id="expend-quantity"
                      type="number"
                      className="form-input"
                      placeholder="e.g. 100"
                      min="1"
                      value={expendForm.quantity}
                      onChange={(e) => setExpendForm((prev) => ({ ...prev, quantity: e.target.value }))}
                      required
                    />
                  </div>

                  {/* Expenditure Date */}
                  <div className="form-group">
                    <label className="form-label" htmlFor="expend-date">
                      Expenditure Date
                    </label>
                    <input
                      id="expend-date"
                      type="date"
                      className="form-input"
                      value={expendForm.expenditureDate}
                      onChange={(e) => setExpendForm((prev) => ({ ...prev, expenditureDate: e.target.value }))}
                    />
                  </div>
                </div>

                {/* Reason */}
                <div className="form-group" style={{ marginTop: '1rem' }}>
                  <label className="form-label" htmlFor="expend-reason">
                    Reason / Consumption Context
                  </label>
                  <textarea
                    id="expend-reason"
                    className="form-textarea"
                    placeholder="Live-fire exercise consumption, combat loss, wear-and-tear decommission, or medical use..."
                    value={expendForm.reason}
                    onChange={(e) => setExpendForm((prev) => ({ ...prev, reason: e.target.value }))}
                    rows={2}
                  />
                </div>

                <div style={{ marginTop: '1.25rem', display: 'flex', justifyContent: 'flex-end' }}>
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={submitting}
                  >
                    <Flame size={15} />
                    <span>{submitting ? 'Recording...' : 'Record Expenditure'}</span>
                  </button>
                </div>
              </form>
            </div>
          ) : (
            <div className="alert alert-info" style={{ marginBottom: '1.5rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <ShieldAlert size={16} />
                <span>Read-Only Access: Recording expenditure requires Base Commander or Administrator privileges.</span>
              </div>
            </div>
          )}

          {/* Filter Bar */}
          <FilterBar
            filters={filters}
            onFilterChange={setFilters}
            onReset={handleFilterReset}
            bases={bases}
            equipmentTypes={equipmentTypes}
            userRole={user?.role}
            userBaseId={user?.baseId}
          />

          {/* Expenditures History Table */}
          <div className="card" style={{ padding: '0' }}>
            <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <h3 className="card-title">
                <span>Expenditure History</span>
              </h3>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                {expenditures.length} {expenditures.length === 1 ? 'record' : 'records'}
              </span>
            </div>

            {loading ? (
              <LoadingSpinner message="Loading expenditure records..." />
            ) : expenditures.length === 0 ? (
              <div className="empty-state">
                <Flame size={32} style={{ color: 'var(--text-muted)', opacity: 0.5 }} />
                <p>No expenditure records found for the selected filters.</p>
              </div>
            ) : (
              <div className="table-responsive" style={{ border: 'none', borderRadius: '0' }}>
                <table className="table table-expenditures">
                  <thead>
                    <tr>
                      <th style={{ width: '80px' }}>ID #</th>
                      <th style={{ width: '110px' }}>Date</th>
                      <th>Asset Item</th>
                      <th>Base Station</th>
                      <th style={{ textAlign: 'right', width: '100px' }}>Quantity</th>
                      <th>Reason / Context</th>
                      <th>Logged By</th>
                    </tr>
                  </thead>
                  <tbody>
                    {expenditures.map((ex) => (
                      <tr key={ex.id}>
                        <td className="mono" style={{ color: 'var(--text-muted)' }}>
                          #{ex.id}
                        </td>
                        <td style={{ color: 'var(--text-secondary)' }}>
                          {ex.expenditureDate ? ex.expenditureDate.substring(0, 10) : '—'}
                        </td>
                        <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                          {ex.assetName}
                        </td>
                        <td style={{ color: 'var(--text-secondary)' }}>
                          {ex.baseName ? `${ex.baseName} (#${ex.baseId})` : `Base #${ex.baseId}`}
                        </td>
                        <td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--danger)' }} className="mono">
                          -{ex.quantity.toLocaleString()}
                        </td>
                        <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem', maxWidth: '280px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={ex.reason || ''}>
                          {ex.reason || '—'}
                        </td>
                        <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>
                          {ex.createdByUser || '—'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
