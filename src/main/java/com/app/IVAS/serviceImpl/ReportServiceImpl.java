package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.dto.AssignedReportPojo;
import com.app.IVAS.dto.SalesReportDto;
import com.app.IVAS.dto.SalesReportPojo;
import com.app.IVAS.dto.StockReportPojo;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.entity.QPlateNumber;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.PlateNumberRepository;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.ReportService;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

            InvoiceServiceType invoiceService = appRepository.startJPAQuery(com.app.IVAS.entity.QInvoiceServiceType.invoiceServiceType)
                    .where(QInvoiceServiceType.invoiceServiceType.invoice.eq(salesList.getInvoice()).and(QInvoiceServiceType.invoiceServiceType.serviceType.name.equalsIgnoreCase("PLATE NUMBER REGISTRATION")))
                    .fetchFirst();

           if (invoiceService != null){
               SalesReportDto dto = new SalesReportDto();
               dto.setMla(salesList.getCreatedBy().getDisplayName());
               dto.setPlateNumber(salesList.getVehicle().getPlateNumber().getPlateNumber());
               dto.setMlaStation(salesList.getCreatedBy().getOffice().getName());
               dto.setDateSold(salesList.getInvoice().getPaymentDate().format(df));
               dto.setAmount(invoiceService.getServiceType().getPrice());
               dtos.add(dto);
           }
        }
        return dtos;
    }

    @Override
    public List<StockReportPojo> getStockReport(List<PortalUser> users) {
        return users.stream().map(portalUser -> {

            JPAQuery<PlateNumber> plateNumberJPAQuery = appRepository.startJPAQuery(com.app.IVAS.entity.QPlateNumber.plateNumber1)
                    .where(QPlateNumber.plateNumber1.request.isNotNull())
                    .where(QPlateNumber.plateNumber1.agent.eq(portalUser));

            StockReportPojo pojo =  new StockReportPojo();
            pojo.setMla(portalUser.getDisplayName());
            pojo.setStation(portalUser.getOffice().getName());
            pojo.setAssigned(plateNumberJPAQuery.fetch().size());
            pojo.setSold(plateNumberJPAQuery.where(QPlateNumber.plateNumber1.plateNumberStatus.eq(PlateNumberStatus.SOLD)).fetch().size());
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
            pojo.setType(plateNumber.getType().getName());
            pojo.setSubType(plateNumber.getSubType() != null ? plateNumber.getSubType().getName() : null);
            pojo.setDateAssigned(plateNumber.getRequest() != null ? plateNumber.getRequest().getLastUpdatedAt().format(df) : "");
            pojo.setStatus(plateNumber.getPlateNumberStatus());
            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SalesReportPojo> getServiceSales(List<InvoiceServiceType> invoiceServiceTypes) {
        List<SalesReportPojo> pojos = new ArrayList<>();
        for(InvoiceServiceType invoiceService:invoiceServiceTypes){
            SalesReportPojo pojo =  new SalesReportPojo();
            pojo.setMla(invoiceService.getInvoice().getCreatedBy().getDisplayName());
            pojo.setTaxPayer(invoiceService.getInvoice().getPayer().getDisplayName());
            pojo.setServiceType(invoiceService.getServiceType().getName());
            pojo.setInvoiceID(invoiceService.getReference());
            pojo.setMlaStation(invoiceService.getInvoice().getCreatedBy().getOffice().getName());
            pojo.setDateSold(invoiceService.getPaymentDate().format(df));
            pojo.setAmount(invoiceService.getAmount());
            pojos.add(pojo);
        }
        return pojos;
    }
}
