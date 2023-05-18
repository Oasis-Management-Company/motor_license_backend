package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.DashboardDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.entity.QPlateNumber;
import com.app.IVAS.entity.QPlateNumberRequest;
import com.app.IVAS.entity.QSales;
import com.app.IVAS.entity.QStock;
import com.app.IVAS.entity.userManagement.*;
import com.app.IVAS.entity.userManagement.QPortalUser;
import com.app.IVAS.entity.userManagement.QZonalOffice;
import com.app.IVAS.repository.*;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.DashboardCtrlService;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardCtrlService {

    private final StockRepository stockRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final SalesRepository salesRepository;
    private final PlateNumberRequestRepository plateNumberRequestRepository;
    private final PortalUserRepository portalUserRepository;
    private final ZonalOfficeRepository zonalOfficeRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;


    @Override
    public DashboardDto DashboardReport() {

        LocalDate now = LocalDate.now();
        DashboardDto dto = new DashboardDto();
        Double totalAmount = 0.0;
        Double totalPlateAmount = 0.0;
        List<Double> amounts = new ArrayList<>();
        Double mytoday = 0.0;
        Double mythisWeek = 0.0;
        Double mythisMonth = 0.0;
        Double mythisYear = 0.0;
        Double mypToday  = 0.0;
        Double mypThisWeek = 0.0;
        Double mypThisMonth = 0.0;
        Double mypThisYear = 0.0;

        JPAQuery<Stock> stockJPAQuery = appRepository.startJPAQuery(QStock.stock);
        JPAQuery<Sales> salesJPAQuery = appRepository.startJPAQuery(QSales.sales);
        JPAQuery<PlateNumberRequest> plateNumberRequestJPAQuery1 = appRepository.startJPAQuery(QPlateNumberRequest.plateNumberRequest);
        JPAQuery<PlateNumber> plateNumberJPAQuery = appRepository.startJPAQuery(QPlateNumber.plateNumber1);
        JPAQuery<PortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QPortalUser.portalUser).where(QPortalUser.portalUser.role.name.equalsIgnoreCase("MLA"));
        JPAQuery<ZonalOffice> zonalOfficeJPAQuery = appRepository.startJPAQuery(QZonalOffice.zonalOffice);

        JPAQuery<InvoiceServiceType> invoiceServiceTypeJPAQuery = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.contains("PLATE NUMBER VEHICLE"))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentStatus.eq(PaymentStatus.PAID));

        JPAQuery<InvoiceServiceType> pToday = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.contains("PLATE NUMBER VEHICLE"))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentDate.goe(LocalDate.now().atStartOfDay()))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentDate.loe(LocalDate.now().atStartOfDay().plusHours(24)))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentStatus.eq(PaymentStatus.PAID));


        LocalDate date = now.with(DayOfWeek.SUNDAY);
        JPAQuery<InvoiceServiceType> pThisWeek = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.contains("PLATE NUMBER VEHICLE"))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentDate.goe(date.minusDays(7).atStartOfDay()))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentDate.loe(date.atStartOfDay()))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentStatus.eq(PaymentStatus.PAID));

        LocalDate month = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.getMonth().length(now.isLeapYear()));
        JPAQuery<InvoiceServiceType> pThisMonth = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.contains("PLATE NUMBER VEHICLE"))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentDate.goe(month.atStartOfDay()))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentDate.loe(end.plusDays(1).atStartOfDay()))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentStatus.eq(PaymentStatus.PAID));

        Date d=new Date();
        int currentYear=d.getYear()+1900;
        Year startyear = Year.parse(String.valueOf(currentYear));
        LocalDate startdate = startyear.atDay(1);
        LocalDate endYear = startyear.atDay(365);
        JPAQuery<InvoiceServiceType> pThisYear = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.contains("PLATE NUMBER VEHICLE"))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentDate.goe(startdate.atStartOfDay()))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentDate.loe(endYear.atStartOfDay()))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentStatus.eq(PaymentStatus.PAID));


        JPAQuery<Invoice> invoices = appRepository.startJPAQuery(QInvoice.invoice)
                .where(QInvoice.invoice.paymentStatus.eq(PaymentStatus.PAID));

        JPAQuery<Invoice> Today = appRepository.startJPAQuery(QInvoice.invoice)
                .where(QInvoice.invoice.paymentStatus.eq(PaymentStatus.PAID))
                .where(QInvoice.invoice.paymentDate.goe(LocalDate.now().atStartOfDay()))
                .where(QInvoice.invoice.paymentDate.loe(LocalDate.now().atStartOfDay().plusHours(24)));

        JPAQuery<Invoice> ThisWeek = appRepository.startJPAQuery(QInvoice.invoice)
                .where(QInvoice.invoice.paymentDate.goe(date.minusDays(7).atStartOfDay()))
                .where(QInvoice.invoice.paymentDate.loe(date.atStartOfDay()))
                .where(QInvoice.invoice.paymentStatus.eq(PaymentStatus.PAID));

        JPAQuery<Invoice> ThisMonth = appRepository.startJPAQuery(QInvoice.invoice)
                .where(QInvoice.invoice.paymentDate.goe(month.atStartOfDay()))
                .where(QInvoice.invoice.paymentDate.loe(end.plusDays(1).atStartOfDay()))
                .where(QInvoice.invoice.paymentStatus.eq(PaymentStatus.PAID));

        JPAQuery<Invoice> ThisYear = appRepository.startJPAQuery(QInvoice.invoice)
                .where(QInvoice.invoice.paymentDate.goe(startdate.atStartOfDay()))
                .where(QInvoice.invoice.paymentDate.loe(endYear.atStartOfDay()))
                .where(QInvoice.invoice.paymentStatus.eq(PaymentStatus.PAID));

        if (jwtService.user.getRole().getName().equalsIgnoreCase("MLA")){
            plateNumberRequestJPAQuery1.where(com.app.IVAS.entity.QPlateNumberRequest.plateNumberRequest.createdBy.id.eq(jwtService.user.getId()));
            plateNumberJPAQuery
                    .where(QPlateNumber.plateNumber1.agent.id.eq(jwtService.user.getId()))
                    .where(QPlateNumber.plateNumber1.plateNumberStatus.eq(PlateNumberStatus.ASSIGNED));
            salesJPAQuery.where(QSales.sales.createdBy.id.eq(jwtService.user.getId()));
            invoices.where(QInvoice.invoice.createdBy.id.eq(jwtService.user.getId()));
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.invoice.createdBy.id.eq(jwtService.user.getId()));

            for (Invoice invoice : invoices.fetch()) {
                totalAmount += invoice.getAmount();
            }
            for (InvoiceServiceType fetch : invoiceServiceTypeJPAQuery.fetch()) {
                totalPlateAmount += fetch.getServiceType().getPrice();
            }

            dto.setTotalRequest(plateNumberRequestJPAQuery1.fetch().size());
            dto.setTotalplate(plateNumberJPAQuery.fetch().size());
            dto.setTotalSales(salesJPAQuery.fetch().size());
            dto.setTotalMla(portalUserJPAQuery.fetch().size());
            dto.setTotalStations(zonalOfficeJPAQuery.fetch().size());
            dto.setTotalAmount(totalAmount);
            dto.setTotalPlateAmount(totalPlateAmount);
            return dto;
        }
        if(!jwtService.user.getRole().getName().equalsIgnoreCase("MLA")){


            for (Invoice invoice : invoices.fetch()) {
                totalAmount += invoice.getAmount();
            }

            for (InvoiceServiceType fetch : invoiceServiceTypeJPAQuery.fetch()) {
                totalPlateAmount += fetch.getServiceType().getPrice();
            }
            for (InvoiceServiceType fetch : pToday.fetch()) {
                mypToday += fetch.getAmount();
            }
            for (InvoiceServiceType fetch : pThisWeek.fetch()) {
                mypThisWeek += fetch.getAmount();
            }
            for (InvoiceServiceType fetch : pThisMonth.fetch()) {
                mypThisMonth += fetch.getAmount();
            }
            for (InvoiceServiceType fetch : pThisYear.fetch()) {
                mypThisYear += fetch.getAmount();
            }
            for (Invoice fetch : Today.fetch()) {
                mytoday += fetch.getAmount();
            }
            for (Invoice fetch : ThisWeek.fetch()) {
                mythisWeek += fetch.getAmount();
            }
            for (Invoice fetch : ThisMonth.fetch()) {
                mythisMonth += fetch.getAmount();
            }
            for (Invoice fetch : ThisYear.fetch()) {
                mythisYear += fetch.getAmount();
            }
            dto.setTotalStock(stockJPAQuery.fetch().size());
            dto.setTotalSales(salesJPAQuery.fetch().size());
            dto.setTotalRequest(plateNumberRequestJPAQuery1.fetch().size());
            dto.setTotalplate(plateNumberJPAQuery.fetch().size());
            dto.setTotalAmount(totalAmount);
            dto.setTotalPlateAmount(totalPlateAmount);
            dto.setTotalMla(portalUserJPAQuery.fetch().size());
            dto.setTotalStations(zonalOfficeJPAQuery.fetch().size());
            dto.setPToday(mypToday);
            dto.setPThisWeek(mypThisWeek);
            dto.setPThisMonth(mypThisMonth);
            dto.setPThisYear(mypThisYear);
            dto.setToday(mytoday);
            dto.setThisWeek(mythisWeek);
            dto.setThisMonth(mythisMonth);
            dto.setThisYear(mythisYear);
            return dto;
        }
        dto.setTotalMla(0);
        dto.setTotalStations(0);
        dto.setTotalStock(0);
        dto.setTotalplate(0);
        dto.setTotalSales(0);
        dto.setTotalRequest(0);
        return dto;
    }
}
