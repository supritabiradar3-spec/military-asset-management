package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.PurchaseRequest;
import com.military.assetmanagement.dto.PurchaseResponse;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest request);
    PurchaseResponse getPurchaseById(Long id);
    List<PurchaseResponse> getPurchases(Long baseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate);
}
