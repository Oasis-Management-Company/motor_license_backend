package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.VehicleMakeAndModelDto;
import com.app.IVAS.entity.VehicleMake;
import com.app.IVAS.entity.VehicleModel;
import com.app.IVAS.repository.VehicleMakeRepository;
import com.app.IVAS.repository.VehicleModelRepository;
import com.app.IVAS.service.VehicleMakeAndModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleMakeAndModelServiceImpl implements VehicleMakeAndModelService {
    private final VehicleMakeRepository vehicleMakeRepository;
    private final VehicleModelRepository vehicleModelRepository;

    @Override
    public VehicleMakeAndModelDto createVehicleMake(VehicleMakeAndModelDto vehicleMakeDto) {

        VehicleMake vehicleMake = new VehicleMake();
        VehicleModel vehicleModel = new VehicleModel();

        Optional<VehicleMake> vehicleMakeOptional = vehicleMakeRepository.findByNameIgnoreCase(vehicleMakeDto.getMakeName());

        if (!vehicleMakeOptional.isPresent()) {

            vehicleMake.setName(vehicleMakeDto.getMakeName());
            vehicleMakeRepository.save(vehicleMake);

            vehicleModel.setName(vehicleMakeDto.getModelName());
            vehicleModel.setYear(vehicleMakeDto.getModelYear());

            Optional<VehicleMake> vehicleMakeOptional2 = vehicleMakeRepository.findByNameIgnoreCase(vehicleMakeDto.getMakeName());

            vehicleModel.setVehicleMake(vehicleMakeOptional2.get());

            return vehicleMakeDto;

        } else {

            Optional<VehicleModel> vehicleModelOptional = vehicleModelRepository
                    .findByNameIgnoreCaseAndYearAndVehicleMake(vehicleMakeDto.getModelName(),
                            vehicleMakeDto.getModelYear(), vehicleMakeOptional.get());

            if (!vehicleModelOptional.isPresent()) {
                vehicleModel.setName(vehicleMakeDto.getModelName());
                vehicleModel.setYear(vehicleMakeDto.getModelYear());
                vehicleModel.setVehicleMake(vehicleMakeOptional.get());

                return vehicleMakeDto;

            }

        }

        return null;
    }
}
