package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.ActivityStatusConstant;
import com.app.IVAS.dto.VehicleMakeAndModelDto;
import com.app.IVAS.entity.VehicleMake;
import com.app.IVAS.entity.VehicleModel;
import com.app.IVAS.repository.VehicleMakeRepository;
import com.app.IVAS.repository.VehicleModelRepository;
import com.app.IVAS.service.ActivityLogService;
import com.app.IVAS.service.VehicleMakeAndModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleMakeAndModelServiceImpl implements VehicleMakeAndModelService {
    private final VehicleMakeRepository vehicleMakeRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final ActivityLogService activityLogService;

    @Override
    public VehicleMakeAndModelDto createVehicleMake(VehicleMakeAndModelDto vehicleMakeDto) {

        VehicleMake vehicleMake = new VehicleMake();
        VehicleModel vehicleModel = new VehicleModel();

        Optional<VehicleMake> vehicleMakeOptional = vehicleMakeRepository.findByNameIgnoreCase(vehicleMakeDto.getMakeName());

        if (!vehicleMakeOptional.isPresent()) {

            vehicleMake.setName(vehicleMakeDto.getMakeName());
            vehicleMakeRepository.save(vehicleMake);
            activityLogService.createActivityLog((vehicleMake.getName() + " vehicle make was created"), ActivityStatusConstant.CREATE);

            vehicleModel.setName(vehicleMakeDto.getModelName());
            vehicleModel.setYear(vehicleMakeDto.getModelYear());

            Optional<VehicleMake> vehicleMakeOptional2 = vehicleMakeRepository.findByNameIgnoreCase(vehicleMakeDto.getMakeName());

            vehicleModel.setVehicleMake(vehicleMakeOptional2.get());

            vehicleModelRepository.save(vehicleModel);
            activityLogService.createActivityLog((vehicleModel.getName() + " of " + vehicleMake.getName() + " vehicle make was created"), ActivityStatusConstant.CREATE);


            return vehicleMakeDto;

        } else {

            Optional<VehicleModel> vehicleModelOptional = vehicleModelRepository
                    .findByNameIgnoreCaseAndYearAndVehicleMake(vehicleMakeDto.getModelName(),
                            vehicleMakeDto.getModelYear(), vehicleMakeOptional.get());

            if (!vehicleModelOptional.isPresent()) {
                vehicleModel.setName(vehicleMakeDto.getModelName());
                vehicleModel.setYear(vehicleMakeDto.getModelYear());
                vehicleModel.setVehicleMake(vehicleMakeOptional.get());

                vehicleModelRepository.save(vehicleModel);

                activityLogService.createActivityLog((vehicleModel.getName() + " of " + vehicleMakeOptional.get().getName() + " vehicle make was created"), ActivityStatusConstant.CREATE);

                return vehicleMakeDto;

            }

        }

        return null;
    }

    @Override
    public List<VehicleMake> get(List<VehicleMake> vehicleMakes) {

        return vehicleMakes;
    }

    @Override
    public List<VehicleModel> getVehicleModels(List<VehicleModel> vehicleModels) {
        return vehicleModels;
    }

    @Override
    public List<VehicleMake> fetchVehicleMake() {

        return vehicleMakeRepository.findAll();
    }

    @Override
    public List<VehicleModel> fetchVehicleModel(Long id) {

        Optional<VehicleMake> vehicleMake = vehicleMakeRepository.findById(id);

        return vehicleModelRepository.findAllByVehicleMake(vehicleMake.get());
    }


}
