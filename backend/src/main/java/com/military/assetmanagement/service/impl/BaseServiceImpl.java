package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.BaseRequest;
import com.military.assetmanagement.dto.BaseResponse;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.exception.InvalidOperationException;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.BaseRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BaseServiceImpl implements BaseService {

    private final BaseRepository baseRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;

    public BaseServiceImpl(
            BaseRepository baseRepository,
            SecurityUtils securityUtils,
            AuditLogService auditLogService
    ) {
        this.baseRepository = baseRepository;
        this.securityUtils = securityUtils;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaseResponse> getAllBases() {
        return baseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse getBaseById(Long id) {
        Base base = getBaseEntity(id);
        return mapToResponse(base);
    }

    @Override
    @Transactional(readOnly = true)
    public Base getBaseEntity(Long id) {
        return baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + id));
    }

    @Override
    @Transactional
    public BaseResponse createBase(BaseRequest request) {
        if (!securityUtils.isAdmin()) {
            throw new AccessDeniedException("Only ADMIN users can create military bases");
        }

        if (baseRepository.existsByName(request.getName())) {
            throw new InvalidOperationException("Base with name '" + request.getName() + "' already exists");
        }

        Base base = Base.builder()
                .name(request.getName().trim())
                .location(request.getLocation().trim())
                .build();

        Base saved = baseRepository.save(base);

        auditLogService.logAction(
                securityUtils.getCurrentUsername(),
                "BASE_CREATED",
                "BASE",
                saved.getId(),
                saved.getId(),
                "Created base: " + saved.getName()
        );

        return mapToResponse(saved);
    }

    private BaseResponse mapToResponse(Base base) {
        return BaseResponse.builder()
                .id(base.getId())
                .name(base.getName())
                .location(base.getLocation())
                .build();
    }
}
