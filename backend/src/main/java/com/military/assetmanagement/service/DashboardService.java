package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.DashboardMetricsResponse;

import java.time.LocalDate;

public interface DashboardService {
    DashboardMetricsResponse getDashboardMetrics(Long baseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate);
}
