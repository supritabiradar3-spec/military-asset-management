package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.ExpenditureRequest;
import com.military.assetmanagement.dto.ExpenditureResponse;

import java.time.LocalDate;
import java.util.List;

public interface ExpenditureService {
    ExpenditureResponse createExpenditure(ExpenditureRequest request);
    ExpenditureResponse getExpenditureById(Long id);
    List<ExpenditureResponse> getExpenditures(Long baseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate);
}
