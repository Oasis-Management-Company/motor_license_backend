package com.app.IVAS.serviceImpl;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.PlateNumberRequest;
import com.app.IVAS.entity.VehicleCategory;
import com.app.IVAS.entity.userManagement.PortalUser;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.WorkFlowApprovalStatus;
import com.app.IVAS.Enum.WorkFlowType;
import com.app.IVAS.entity.*;
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
    private final PortalUserRepository portalUserRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final WorkFlowLogRepository workFlowLogRepository;

    @Override
    public List<PlateNumberRequestPojo> getPlateNumberRequest(List<PlateNumberRequest> requests) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");

        return requests.stream().map(request -> {
            PlateNumberRequestPojo pojo = new PlateNumberRequestPojo();
            pojo.setId(request.getId());
            pojo.setTrackingId(request.getTrackingId());
            pojo.setCreatedAt(request.getCreatedAt().format(df));
            pojo.setCreatedBy(request.getCreatedBy().getDisplayName());
            pojo.setPlateNumberType(request.getPlateNumberType().getName());
            pojo.setPlateNumberSubType(request.getSubType() != null ? request.getSubType().getName() : null);
            pojo.setNumberOfPlates(request.getTotalNumberRequested().toString());
            pojo.setStatus(request.getWorkFlowApprovalStatus());
            pojo.setCurrentApprovingOfficer(request.getWorkFlow().getStage().getApprovingOfficer().getDisplayName());

            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ServiceTypePojo> getServiceTYpe(List<ServiceType> serviceTypes) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");
        return serviceTypes.stream().map(serviceType -> {
            ServiceTypePojo pojo = new ServiceTypePojo();
            pojo.setName(serviceType.getName());
            pojo.setPrice(serviceType.getPrice());
            pojo.setDurationInMonth(serviceType.getDurationInMonth());
            pojo.setCategoryName(serviceType.getCategory().getName());
            pojo.setCreatedAt(serviceType.getCreatedAt().format(df));
            pojo.setCreatedBy(serviceType.getCreatedBy().getDisplayName());
            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<WorkFLowStagePojo> getWorkFlowStage(List<WorkFlowStage> workFlowStages) {
        return null;
    }

    @Override
    @Transactional
    public void CreatePlateNumberRequest(PlateNumberRequestDto dto) {
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("ddMMyyHHmmss");
        String trackingId = "PNR-" + LocalDateTime.now().format(formatter2);

        WorkFlow workFlow = new WorkFlow();
        workFlow.setStage(workFlowStageRepository.findByTypeAndStep(WorkFlowType.PLATE_NUMBER_REQUEST, 1L).get());
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

    @Override
    public void CreateWorkFlowStage(WorkFlowStageDto dto) {
        WorkFlowStage stage = workFlowStageRepository.findByTypeAndStep(dto.getType(), dto.getStep()).orElseGet(() ->{
            WorkFlowStage newStage = new WorkFlowStage();
            newStage.setStep(dto.getStep());
            newStage.setApprovingOfficer(portalUserRepository.findById(dto.getApprovingOfficer()).get());
            newStage.setType(WorkFlowType.PLATE_NUMBER_REQUEST);
            newStage.setIsFinalStage(dto.getIsFinalStage());
            newStage.setIsSuperApprover(dto.getIsSuperApprover());
            newStage.setStatus(GenericStatusConstant.ACTIVE);
            newStage.setCreatedBy(jwtService.user);
            return workFlowStageRepository.save(newStage);
        });
    }

    @Override
    public void CreateServiceType(ServiceTypeDto dto) {
        ServiceType serviceType = serviceTypeRepository.findByName(dto.getName()).orElseGet(() -> {
            ServiceType type = new ServiceType();
            type.setName(dto.getName());
            type.setPrice(dto.getPrice());
            type.setDurationInMonth(dto.getDurationInMonth());
            type.setCategory(vehicleCategoryRepository.findById(dto.getCategory()).get());
            type.setStatus(GenericStatusConstant.ACTIVE);
            type.setCreatedBy(jwtService.user);
            return serviceTypeRepository.save(type);
        });
    }

    @Override
    public void UpdatePlateNumberRequest(Long requestId, String action) {
        PlateNumberRequest request = plateNumberRequestRepository.findById(requestId).get();

        WorkFLowLog log = new WorkFLowLog();
        log.setAction(action);
        log.setCreatedBy(jwtService.user);
        log.setRequest(request);
        workFlowLogRepository.save(log);

        WorkFlow workFlow = request.getWorkFlow();
        if ((!workFlow.getStage().getIsFinalStage() || !workFlow.getStage().getIsSuperApprover()) && action.equalsIgnoreCase("APPROVED")){
            workFlow.setStage(workFlowStageRepository.findByTypeAndStep(workFlow.getType(), workFlow.getStage().getStep() + 1).get());
        } else if (action.equalsIgnoreCase("DISAPPROVED")) {
            workFlow.setWorkFlowApprovalStatus(WorkFlowApprovalStatus.DENIED);
            workFlow.setFinalApprover(jwtService.user);

        } else if ((workFlow.getStage().getIsFinalStage() || workFlow.getStage().getIsSuperApprover()) && action.equalsIgnoreCase("APPROVED")){
            workFlow.setWorkFlowApprovalStatus(WorkFlowApprovalStatus.APPROVED);
            workFlow.setFinalApprover(jwtService.user);
        }

        workFlow.setLastUpdatedBy(jwtService.user);
        workFlowRepository.save(workFlow);
    }
}
