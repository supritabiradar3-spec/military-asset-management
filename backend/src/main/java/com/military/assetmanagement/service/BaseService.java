package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.BaseRequest;
import com.military.assetmanagement.dto.BaseResponse;
import com.military.assetmanagement.entity.Base;

import java.util.List;

public interface BaseService {
    List<BaseResponse> getAllBases();
    BaseResponse getBaseById(Long id);
    Base getBaseEntity(Long id);
    BaseResponse createBase(BaseRequest request);
}
