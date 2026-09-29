package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.AssignmentRequest;
import com.military.assetmanagement.dto.AssignmentResponse;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentRequest request);
    AssignmentResponse getAssignmentById(Long id);
    List<AssignmentResponse> getAssignments(Long baseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate);
}
