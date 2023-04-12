package com.app.IVAS.service;

import com.app.IVAS.dto.AssignedReportPojo;
import com.app.IVAS.dto.SalesReportDto;
import com.app.IVAS.dto.StockReportPojo;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.Sales;
import com.app.IVAS.entity.userManagement.PortalUser;

import java.util.List;

public interface ReportService {

    List<SalesReportDto> getSales(List<Sales> sales);

    List<StockReportPojo> getStockReport(List<PortalUser> users);

    List<AssignedReportPojo> getAssignedPlateNumbers(List<PlateNumber> plateNumbers);
}
