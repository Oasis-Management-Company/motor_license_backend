package com.app.IVAS.service;

import com.app.IVAS.dto.*;
import com.app.IVAS.entity.PlateNumberRequest;
import com.app.IVAS.entity.ServiceType;
import com.app.IVAS.entity.WorkFlowStage;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RequestService {

    List<PlateNumberRequestPojo> getPlateNumberRequest(List<PlateNumberRequest> requests);

    List<ServiceTypePojo> getServiceTYpe(List<ServiceType> serviceTypes);

    List<WorkFLowStagePojo> getWorkFlowStage(List<WorkFlowStage> workFlowStages);

    void CreatePlateNumberRequest(PlateNumberRequestDto dto);

    void CreateWorkFlowStage(WorkFlowStageDto dto);

    void CreateServiceType(ServiceTypeDto dto);

    void UpdatePlateNumberRequest(Long requestId, String action);

    Boolean canApproveRequest();
}
