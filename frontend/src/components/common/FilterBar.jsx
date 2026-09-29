import React from 'react';
import { Filter, RotateCcw } from 'lucide-react';

export function FilterBar({
  filters,
  onFilterChange,
  onReset,
  bases = [],
  equipmentTypes = [],
  userRole,
  userBaseId,
  showDateRange = true,
  showBase = true,
  showEquipmentType = true,
}) {
  const isBaseRestricted = userRole !== 'ADMIN' && userBaseId != null;

  return (
    <div className="filter-card">
      <div className="filter-header">
        <div className="filter-title">
          <Filter size={13} color="var(--primary)" />
          <span>Filters</span>
        </div>
        {onReset && (
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={onReset}
            title="Reset filters"
          >
            <RotateCcw size={12} />
            <span>Reset</span>
          </button>
        )}
      </div>

      <div className="filter-grid">
        {showDateRange && (
          <>
            <div className="filter-item">
              <label className="filter-label" htmlFor="filter-start-date">Date From</label>
              <input
                id="filter-start-date"
                type="date"
                className="filter-input"
                value={filters.startDate || ''}
                onChange={(e) => onFilterChange({ ...filters, startDate: e.target.value })}
              />
            </div>
            <div className="filter-item">
              <label className="filter-label" htmlFor="filter-end-date">Date To</label>
              <input
                id="filter-end-date"
                type="date"
                className="filter-input"
                value={filters.endDate || ''}
                onChange={(e) => onFilterChange({ ...filters, endDate: e.target.value })}
              />
            </div>
          </>
        )}

        {showBase && (
          <div className="filter-item">
            <label className="filter-label" htmlFor="filter-base">
              Base {isBaseRestricted && '(Assigned)'}
            </label>
            <select
              id="filter-base"
              className="filter-select"
              value={filters.baseId || ''}
              disabled={isBaseRestricted}
              onChange={(e) => onFilterChange({ ...filters, baseId: e.target.value || null })}
            >
              {!isBaseRestricted && <option value="">All Bases</option>}
              {bases.map((base) => (
                <option key={base.id} value={base.id}>
                  {base.name} ({base.location})
                </option>
              ))}
            </select>
          </div>
        )}

        {showEquipmentType && (
          <div className="filter-item">
            <label className="filter-label" htmlFor="filter-equipment-type">Equipment Type</label>
            <select
              id="filter-equipment-type"
              className="filter-select"
              value={filters.equipmentTypeId || ''}
              onChange={(e) => onFilterChange({ ...filters, equipmentTypeId: e.target.value || null })}
            >
              <option value="">All Types</option>
              {equipmentTypes.map((type) => (
                <option key={type.id} value={type.id}>
                  {type.name}
                </option>
              ))}
            </select>
          </div>
        )}
      </div>
    </div>
  );
}
