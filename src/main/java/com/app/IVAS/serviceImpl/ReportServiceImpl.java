package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.dto.AssignedReportPojo;
import com.app.IVAS.dto.SalesReportDto;
import com.app.IVAS.dto.StockReportPojo;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.QInvoice;
import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.entity.Sales;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.PlateNumberRepository;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");
    private final AppRepository appRepository;
    private final PlateNumberRepository plateNumberRepository;

    @Override
    public List<SalesReportDto> getSales(List<Sales> sales) {
        List<SalesReportDto> dtos = new ArrayList<>();
        for (Sales salesList: sales){
            SalesReportDto dto = new SalesReportDto();
            dto.setMla(salesList.getCreatedBy().getDisplayName());
            dto.setPlateNumber(salesList.getVehicle().getPlateNumber().getPlateNumber());
            dto.setMlaStation(salesList.getCreatedBy().getOffice().getName());
            dto.setDateSold(salesList.getInvoice().getPaymentDate().format(df));
            dto.setAmount(appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                    .where(QInvoiceServiceType.invoiceServiceType.invoice.eq(salesList.getInvoice())
                            .and(QInvoiceServiceType.invoiceServiceType.serviceType.name.equalsIgnoreCase("PLATE NUMBER REGISTRATION")))
                    .fetchFirst().getServiceType().getPrice());

            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<StockReportPojo> getStockReport(List<PortalUser> users) {
        return users.stream().map(portalUser -> {
            StockReportPojo pojo =  new StockReportPojo();
            pojo.setMla(portalUser.getDisplayName());
            pojo.setStation(portalUser.getOffice().getName());
            pojo.setAssigned(plateNumberRepository.findByAgentAndPlateNumberStatus(portalUser, PlateNumberStatus.ASSIGNED).size());
            pojo.setSold(plateNumberRepository.findByAgentAndPlateNumberStatus(portalUser, PlateNumberStatus.SOLD).size());
            pojo.setCurrentQuantity(pojo.getAssigned() - pojo.getSold());
            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AssignedReportPojo> getAssignedPlateNumbers(List<PlateNumber> plateNumbers) {
        return plateNumbers.stream().map(plateNumber -> {
            AssignedReportPojo pojo = new AssignedReportPojo();
            pojo.setId(plateNumber.getId());
            pojo.setPlateNumber(plateNumber.getPlateNumber());
            pojo.setMla(plateNumber.getAgent().getDisplayName());
            pojo.setDateAssigned(plateNumber.getRequest().getLastUpdatedAt().format(df));
            pojo.setStatus(plateNumber.getPlateNumberStatus());
            return pojo;
        }).collect(Collectors.toList());
    }
}
