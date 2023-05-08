package com.app.IVAS.service;

import com.app.IVAS.dto.CardDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.dto.VehicleMakeAndModelDto;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.VehicleMake;
import com.app.IVAS.entity.VehicleModel;

import java.util.List;

public interface VehicleMakeAndModelService {

    VehicleMakeAndModelDto createVehicleMake(VehicleMakeAndModelDto vehicleMakeDto);

    List<VehicleMake> get(List<VehicleMake> vehicleMakes);

    List<VehicleModel> getVehicleModels(List<VehicleModel> vehicleModels);

    List<VehicleMake> fetchVehicleMake();

    List<VehicleModel> fetchVehicleModel(Long id);

}
