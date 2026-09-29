package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.TransferRequest;
import com.military.assetmanagement.dto.TransferResponse;

import java.time.LocalDate;
import java.util.List;

public interface TransferService {
    TransferResponse createTransfer(TransferRequest request);
    TransferResponse getTransferById(Long id);
    List<TransferResponse> getTransfers(Long baseId, Long fromBaseId, Long toBaseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate);
}
