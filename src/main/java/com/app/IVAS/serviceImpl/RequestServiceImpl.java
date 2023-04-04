package com.app.IVAS.serviceImpl;
import com.app.IVAS.Enum.AssignmentStatusConstant;
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
import org.xhtmlrenderer.css.style.derived.StringValue;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final PlateNumberRepository plateNumberRepository;

    @Override
    public List<PlateNumberRequestPojo> getPlateNumberRequest(List<PlateNumberRequest> requests) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");

        return requests.stream().map(request -> {
            PlateNumberRequestPojo pojo = new PlateNumberRequestPojo();
            pojo.setId(request.getId());
            pojo.setMlaId(request.getCreatedBy().getId());
            pojo.setTrackingId(request.getTrackingId());
            pojo.setCreatedAt(request.getCreatedAt().format(df));
            pojo.setCreatedBy(request.getCreatedBy().getDisplayName());
            pojo.setMlaZone(request.getCreatedBy().getOffice().getName());
            pojo.setPlateNumberType(request.getPlateNumberType().getName());
            pojo.setTypeId(request.getPlateNumberType().getId());
            pojo.setPlateNumberSubType(request.getSubType() != null ? request.getSubType().getName() : null);
            pojo.setSubTypeId(request.getSubType() != null ? request.getSubType().getId() : null);
            pojo.setNumberOfPlates(request.getTotalNumberRequested().toString());
            pojo.setAssignedPlates(plateNumberRepository.findByRequest(request).size());
            pojo.setStatus(request.getWorkFlowApprovalStatus());
            pojo.setAssignmentStatus(request.getAssignmentStatus());
            pojo.setCurrentApprovingOfficer(request.getWorkFlow().getStage().getApprovingOfficer().getDisplayName());

            if (request.getWorkFlowApprovalStatus() == WorkFlowApprovalStatus.APPROVED || request.getWorkFlowApprovalStatus() == WorkFlowApprovalStatus.DENIED){
                pojo.setFinalApprovingOfficer(workFlowLogRepository.findByRequest(request).stream().sorted(Comparator.comparing(WorkFLowLog::getCreatedAt).reversed()).collect(Collectors.toList()).get(0).getCreatedBy().getDisplayName());
            }

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
            pojo.setCategoryName(serviceType.getCategory() != null ? serviceType.getCategory().getName(): "");
            pojo.setCreatedAt(serviceType.getCreatedAt() != null ? serviceType.getCreatedAt().format(df) : "");
            pojo.setCreatedBy(serviceType.getCreatedBy()!= null ?  serviceType.getCreatedBy().getDisplayName() : "");
            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<WorkFLowStagePojo> getWorkFlowStage(List<WorkFlowStage> workFlowStages) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");
        return  workFlowStages.stream().map(workFlowStage -> {
            WorkFLowStagePojo pojo = new WorkFLowStagePojo();
            pojo.setStep(workFlowStage.getStep());
            pojo.setApprovingOfficer(workFlowStage.getApprovingOfficer().getDisplayName());
            pojo.setType(workFlowStage.getType());
            pojo.setIsFinalStage(workFlowStage.getIsFinalStage());
            pojo.setIsSuperApprover(workFlowStage.getIsSuperApprover());
            pojo.setCreatedAt(workFlowStage.getCreatedAt().format(df));
            pojo.setCreatedBy(workFlowStage.getCreatedBy().getDisplayName());

            return pojo;
        }).collect(Collectors.toList());
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

        if(request.getPlateNumberType().getName().toUpperCase().contains("CUSTOM")){
            request.setFancyPlate(dto.getFancyPlate());
        }

        request.setWorkFlow(workFlowRepository.save(workFlow));
        request.setWorkFlowApprovalStatus(WorkFlowApprovalStatus.PENDING);
        request.setStatus(GenericStatusConstant.ACTIVE);
        request.setAssignmentStatus(AssignmentStatusConstant.NOT_ASSIGNED);
        request.setCreatedBy(jwtService.user);
        PlateNumberRequest plateNumberRequest = plateNumberRequestRepository.save(request);

        if (!dto.getPreferredPlates().isEmpty()){
            dto.getPreferredPlates().forEach(preferredPlateDto -> {
                PreferredPlate preferredPlate = new PreferredPlate();
                preferredPlate.setCode(preferredPlate.getCode());
                preferredPlate.setNumberOfPlates(preferredPlateDto.getNumberOfPlates());
                preferredPlate.setRequest(plateNumberRequest);
                preferredPlateRepository.save(preferredPlate);
            });
        }

    }

    @Override
    public void CreateWorkFlowStage(WorkFlowStageDto dto) {
        List<WorkFlowStage> stages = workFlowStageRepository.findByType(dto.getType());

        WorkFlowStage newStage = new WorkFlowStage();
        if (stages.isEmpty()){
            newStage.setStep(1L);
        } else {
            newStage.setStep(stages.size() + 1L);
        }
        newStage.setApprovingOfficer(portalUserRepository.findById(dto.getApprovingOfficer()).get());
        newStage.setType(dto.getType());
        newStage.setIsFinalStage(dto.getIsFinalStage());
        newStage.setIsSuperApprover(dto.getIsSuperApprover());
        newStage.setStatus(GenericStatusConstant.ACTIVE);
        newStage.setCreatedBy(jwtService.user);
        workFlowStageRepository.save(newStage);
    }

    @Override
    public void CreateServiceType(ServiceTypeDto dto) {
        ServiceType serviceType = serviceTypeRepository.findByName(dto.getName()).orElseThrow(RuntimeException::new);
        if (serviceType == null) {
            ServiceType type = new ServiceType();
            type.setName(dto.getName());
            type.setPrice(dto.getPrice());
            type.setDurationInMonth(dto.getDurationInMonth());

            if (dto.getCategory() != null){
                type.setCategory(vehicleCategoryRepository.findById(dto.getCategory()).get());
                type.setType(dto.getType());
                type.setPlateNumberType(plateNumberTypeRepository.findById(dto.getPlateNumberType()).get());
            }

            type.setStatus(GenericStatusConstant.ACTIVE);
            type.setCreatedBy(jwtService.user);
            serviceTypeRepository.save(type);
        } else {
            if(dto.getName() != null){
                serviceType.setName(dto.getName());
            }
            if(dto.getPrice() != null){
                serviceType.setPrice(dto.getPrice());
            }
            if(dto.getDurationInMonth() != null){
                serviceType.setDurationInMonth(dto.getDurationInMonth());
            }

            if (dto.getCategory() != null){
                serviceType.setCategory(vehicleCategoryRepository.findById(dto.getCategory()).get());
            }

            if(dto.getType() != null){
                serviceType.setType(dto.getType());
            }

            if(dto.getPlateNumberType() != null){
                serviceType.setPlateNumberType(plateNumberTypeRepository.findById(dto.getPlateNumberType()).get());
            }
        }
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
        if ((!workFlow.getStage().getIsFinalStage() && !workFlow.getStage().getIsSuperApprover()) && action.equalsIgnoreCase("APPROVED")){
            workFlow.setStage(workFlowStageRepository.findByTypeAndStep(workFlow.getType(), workFlow.getStage().getStep() + 1).get());
        } else if (action.equalsIgnoreCase("DISAPPROVED")) {
            workFlow.setWorkFlowApprovalStatus(WorkFlowApprovalStatus.DENIED);
            workFlow.setFinalApprover(jwtService.user);

            request.setWorkFlowApprovalStatus(WorkFlowApprovalStatus.DENIED);
            plateNumberRequestRepository.save(request);

        } else if ((workFlow.getStage().getIsFinalStage() || canApproveRequest()) && action.equalsIgnoreCase("APPROVED")){
            workFlow.setWorkFlowApprovalStatus(WorkFlowApprovalStatus.APPROVED);
            workFlow.setFinalApprover(jwtService.user);

            request.setWorkFlowApprovalStatus(WorkFlowApprovalStatus.APPROVED);
            plateNumberRequestRepository.save(request);

        }

        workFlow.setLastUpdatedBy(jwtService.user);
        workFlowRepository.save(workFlow);
    }

    @Override
    public Boolean canApproveRequest() {
        List<WorkFlowStage> stages = workFlowStageRepository.findByType(WorkFlowType.PLATE_NUMBER_REQUEST);

        return stages.stream().anyMatch(stage -> {
            if (stage.getIsSuperApprover()){
               return stage.getApprovingOfficer().getDisplayName().equalsIgnoreCase(jwtService.user.getDisplayName());
            }
            return false;
        });
    }

//    private PlateNumber createCustomPlate(){
//
//    }
}
