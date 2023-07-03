package com.app.IVAS.service;

import com.app.IVAS.dto.*;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Sales;
import com.app.IVAS.entity.ServiceType;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface VehicleService {
    InvoiceDto getUserVehicleDetails(Long id);

    VehicleDto getVehicleDetails(String chasis);

    Vehicle saveEditedVehicle(VehicleDto vehicleDto);

    VehicleDto getVehicleDetailsByPlate(String plate);

    List<ServiceType> getServiceTypeByPlate(String plate);

    Invoice saveServiceTypeByPlate(String myplate, List<Long> ids);

    Invoice saveServiceTypeByPlateLegacy(String myplate, List<Long> ids, String type, String paymentDate);

    InvoiceDto getTypeByInvoiceIdEdit(Long invoiceId);

    List<ServiceType> getTypeByInvoiceTaxpayer();

    Invoice saveServiceTypeByPlateForTaxpayer(String phonenumber, List<Long> ids);

    AsinDto getTaxpayerByDetails(String phonenumber);

    List<ServiceType> getTaxpayerByDetailsServices();

    Invoice saveTaxPayerServiceType(Long id, List<Long> ids);

    Invoice saveRenewalEdit(Long oldInvoiceId, Long customerId, List<Long> ids);

    List<SalesDto> searchTaxpayerAssessments(List<Sales> results);

    List<PortalUserPojo> searchTaxpayerAssessment(List<Sales> results);

    List<InvoiceDto> searchAllInvoice(List<Invoice> results);

    List<VehicleDto> searchAllVehicleForApproval(List<Vehicle> results);

    VehicleEditDto getAllEditVehicle(Long id);

    HttpStatus approveEdittedVehicle(Long id, String type);

    List<ServiceType> getServiceTypeByPlateandType(String plate, String type);
}
