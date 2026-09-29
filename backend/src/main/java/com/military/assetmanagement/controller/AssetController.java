package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.AssetRequest;
import com.military.assetmanagement.dto.AssetResponse;
import com.military.assetmanagement.service.AssetService;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public ResponseEntity<List<AssetResponse>> getAssets(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId
    ) {
        List<AssetResponse> assets = assetService.getAssets(baseId, equipmentTypeId);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getAssetById(@PathVariable Long id) {
        AssetResponse asset = assetService.getAssetById(id);
        return ResponseEntity.ok(asset);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssetResponse> createAsset(@Valid @RequestBody AssetRequest request) {
        AssetResponse created = assetService.createAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
