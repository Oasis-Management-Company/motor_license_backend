package com.app.IVAS.service;

import com.app.IVAS.dto.PlateNumberRequestDto;
import com.app.IVAS.dto.PlateNumberRequestPojo;
import com.app.IVAS.entity.PlateNumberRequest;

import java.util.List;

public interface RequestService {

    List<PlateNumberRequestPojo> getPlateNumberRequest(List<PlateNumberRequest> requests);

    void CreatePlateNumberRequest(PlateNumberRequestDto dto);
}
