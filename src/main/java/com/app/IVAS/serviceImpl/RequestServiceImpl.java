package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.WorkFlowApprovalStatus;
import com.app.IVAS.Enum.WorkFlowType;
import com.app.IVAS.dto.PlateNumberRequestDto;
import com.app.IVAS.dto.PlateNumberRequestPojo;
import com.app.IVAS.entity.PlateNumberRequest;
import com.app.IVAS.entity.PlateNumberType;
import com.app.IVAS.entity.PreferredPlate;
import com.app.IVAS.entity.WorkFlow;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final JwtService jwtService;
    private final PlateNumberTypeRepository plateNumberTypeRepository;
    private final PlateNumberSubTypeRepository plateNumberSubTypeRepository;
    private final WorkFlowRepository workFlowRepository;
    private final WorkFlowStageRepository workFlowStageRepository;
    private final PlateNumberRequestRepository plateNumberRequestRepository;
    private final PreferredPlateRepository preferredPlateRepository;

    @Override
    public List<PlateNumberRequestPojo> getPlateNumberRequest(List<PlateNumberRequest> requests) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");

        return requests.stream().map(request -> {
            PlateNumberRequestPojo pojo = new PlateNumberRequestPojo();
            pojo.setTrackingId(request.getTrackingId());
            pojo.setCreatedAt(request.getCreatedAt().format(df));
            pojo.setPlateNumberType(request.getPlateNumberType().getName());
            pojo.setPlateNumberSubType(request.getSubType() != null ? request.getSubType().getName() : null);
            pojo.setNumberOfPlates(request.getTotalNumberRequested().toString());
            pojo.setStatus(request.getWorkFlowApprovalStatus());
            pojo.setCurrentApprovingOfficer(request.getWorkFlow().getStage().getApprovingOfficer().getDisplayName());

            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void CreatePlateNumberRequest(PlateNumberRequestDto dto) {
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("ddMMyyHHmmss");
        String trackingId = "PNR-" + LocalDateTime.now().format(formatter2);

        WorkFlow workFlow = new WorkFlow();
        workFlow.setStage(workFlowStageRepository.findByTypeAndStep(WorkFlowType.PLATE_NUMBER_REQUEST, 1L));
        workFlow.setType(WorkFlowType.PLATE_NUMBER_REQUEST);
        workFlow.setWorkFlowApprovalStatus(WorkFlowApprovalStatus.PENDING);
        workFlow.setStatus(GenericStatusConstant.ACTIVE);

        PlateNumberRequest request = new PlateNumberRequest();
        request.setTrackingId(trackingId);
        request.setTotalNumberRequested(dto.getNumberOfPlates());
        request.setPlateNumberType(plateNumberTypeRepository.findByName(dto.getPlateType()));

        if (dto.getPlateSubType() != null){
            request.setSubType(plateNumberSubTypeRepository.findByName(dto.getPlateSubType()));
        }

        request.setWorkFlow(workFlowRepository.save(workFlow));
        request.setWorkFlowApprovalStatus(WorkFlowApprovalStatus.PENDING);
        request.setStatus(GenericStatusConstant.ACTIVE);
        request.setCreatedBy(jwtService.user);
        plateNumberRequestRepository.save(request);

        if (dto.getPlateSubType().equalsIgnoreCase("PREFERRED")){
            dto.getPreferredPlates().forEach(preferredPlateDto -> {
                PreferredPlate preferredPlate = new PreferredPlate();
                preferredPlate.setCode(preferredPlate.getCode());
                preferredPlate.setNumberOfPlates(preferredPlateDto.getNumberOfPlates());
                preferredPlate.setRequest(plateNumberRequestRepository.findByTrackingId(request.getTrackingId()));
                preferredPlateRepository.save(preferredPlate);
            });
        }

    }
}
