package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ExpenditureRequest;
import com.military.assetmanagement.dto.ExpenditureResponse;
import com.military.assetmanagement.service.ExpenditureService;
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
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    public ExpenditureController(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ExpenditureResponse> createExpenditure(@Valid @RequestBody ExpenditureRequest request) {
        ExpenditureResponse response = expenditureService.createExpenditure(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ExpenditureResponse> getExpenditureById(@PathVariable Long id) {
        ExpenditureResponse response = expenditureService.getExpenditureById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<List<ExpenditureResponse>> getExpenditures(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ExpenditureResponse> expenditures = expenditureService.getExpenditures(baseId, equipmentTypeId, startDate, endDate);
        return ResponseEntity.ok(expenditures);
    }
}
