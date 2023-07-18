package com.app.IVAS.service;

import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.dto.VioSalesDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;

import java.util.List;

public interface VioService {
    List<Offense> getAllOffenses();

    Invoice saveOffenseTypeByPhonenumber(String phoneNumber, List<Long> ids);

    List<InvoiceOffenseType> getOffenseTypeByInvoice(Long salesId);

    PortalUser getOffensePayer(String phoneNumber);

    List<SalesDto> searchAllForVIO(List<Sales> results);

    List<VioSalesDto> get(List<Invoice> invoices);

    Invoice approveInvoice (Long id, String type);

    Vehicle getVehicle(Long id);
}
