package com.app.IVAS.service;

import com.app.IVAS.dto.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;

import java.util.List;
import java.util.Optional;

public interface SalesCtrlService {
    Invoice SaveSales(SalesDto sales);

    Invoice SaveSalesEdit(SalesDto sales);

    List<SalesDto> GetSales(List<Sales> results);

    PortalUser viewPortalUser(Long id);

    PortalUser editPortalUser(PortalUserPojo pojo);

    void approveTaxPayerEdit(Long id, String approval);

    AsinDto ValidateAsin(String asin);

    List<VehicleMake> getVehicleMake();

    List<VehicleModel> getVehicleModel(Long id);

    List<PlateNumber> getUserPlateNumbers(Long id);

    List<PlateNumberType> getUserPlateNumberTypes();

    List<VehicleCategory> getVehicleCategory();

    List<ServiceType> getServiceType(Long id);

    String getApprovalStatus(Long id, String action, Optional<String> reason);

    List<SalesDto> searchAllForVIO(List<Sales> results);

    List<VehicleDto> searchAllVehicles(List<Vehicle> results);

    SalesDto AddVehicle(SalesDto sales);

    List<InvoiceServiceType> getServiceTypeByCategory(Long salesId);

    InvoiceDto VehicleInvoice(Long id);

    PortalUser createUser(UserDto dto);

    List<InvoiceServiceType> getServiceTypeByInvoiceId(Long invoiceId);

    List<InsuranceCompany> getInsurance();

    VehicleDto viewVehicle(String chassisNo);

    InvoiceDto getVehicleOwnerDetails(Long invoiceId);

    List<ServiceType> getServiceByCatandPlate(Long cat, Long plate);

    SalesDto viewSale(Long id);

//    Void editSalesInvoice(SalesDto dto);
}
