package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.BaseRequest;
import com.military.assetmanagement.dto.BaseResponse;
import com.military.assetmanagement.service.BaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bases")
public class BaseController {

    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @GetMapping
    public ResponseEntity<List<BaseResponse>> getAllBases() {
        List<BaseResponse> bases = baseService.getAllBases();
        return ResponseEntity.ok(bases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getBaseById(@PathVariable Long id) {
        BaseResponse base = baseService.getBaseById(id);
        return ResponseEntity.ok(base);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> createBase(@Valid @RequestBody BaseRequest request) {
        BaseResponse created = baseService.createBase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
