package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.TransferRequest;
import com.military.assetmanagement.dto.TransferResponse;
import com.military.assetmanagement.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<TransferResponse> createTransfer(@Valid @RequestBody TransferRequest request) {
        TransferResponse response = transferService.createTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<TransferResponse> getTransferById(@PathVariable Long id) {
        TransferResponse response = transferService.getTransferById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<List<TransferResponse>> getTransfers(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long fromBaseId,
            @RequestParam(required = false) Long toBaseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<TransferResponse> transfers = transferService.getTransfers(baseId, fromBaseId, toBaseId, equipmentTypeId, startDate, endDate);
        return ResponseEntity.ok(transfers);
    }
}
