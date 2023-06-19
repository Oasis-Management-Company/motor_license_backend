package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.Utils.PDFRenderToMultiplePages;
import com.app.IVAS.configuration.AppConfigurationProperties;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoice;
import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.entity.QPlateNumber;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.ReportService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private final PDFRenderToMultiplePages pdfRenderToMultiplePages;
    private final AppConfigurationProperties appConfigurationProperties;

    @Override
    public List<SalesReportDto> getSales(List<InvoiceServiceType> invoiceServiceTypes) {
        List<SalesReportDto> dtos = new ArrayList<>();
        for (InvoiceServiceType invoiceService: invoiceServiceTypes){

               SalesReportDto dto = new SalesReportDto();
               dto.setMla(invoiceService.getInvoice().getCreatedBy().getDisplayName());
               dto.setPlateNumber(invoiceService.getInvoice().getVehicle().getPlateNumber().getPlateNumber());
               dto.setPlateType(invoiceService.getInvoice().getVehicle().getPlateNumber().getType().getName());
               dto.setMlaStation(invoiceService.getInvoice().getCreatedBy().getOffice() != null ? invoiceService.getInvoice().getCreatedBy().getOffice().getName() : "_");
               dto.setDateSold(invoiceService.getPaymentDate().format(df));
               dto.setAmount(invoiceService.getServiceType().getPrice());
               dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<StockReportPojo> getStockReport(List<PortalUser> users) {
        return users.stream().map(portalUser -> {

            JPAQuery<PlateNumber> plateNumberJPAQuery = appRepository.startJPAQuery(QPlateNumber.plateNumber1)
                    .where(QPlateNumber.plateNumber1.stock.isNotNull())
                    .where(QPlateNumber.plateNumber1.agent.eq(portalUser));

            StockReportPojo pojo =  new StockReportPojo();
            pojo.setMla(portalUser.getDisplayName());
            pojo.setStation(portalUser.getOffice() != null ? portalUser.getOffice().getName() : "-");
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
            pojo.setRegType(invoiceService.getRegType());
            pojo.setInvoiceID(invoiceService.getReference());
            pojo.setMlaStation(invoiceService.getInvoice().getCreatedBy().getOffice() != null ? invoiceService.getInvoice().getCreatedBy().getOffice().getName() : "_");
            pojo.setDateSold(invoiceService.getPaymentDate().format(df));
            pojo.setAmount(invoiceService.getAmount());
            pojos.add(pojo);
        }
        return pojos;
    }

    @Override
    public List<OffenseReportPojo> getOffenseReport(List<InvoiceOffenseType> invoiceOffenseTypes) {
        return invoiceOffenseTypes.stream().map(invoiceOffense -> {
            OffenseReportPojo pojo = new OffenseReportPojo();
            pojo.setVio(invoiceOffense.getInvoice().getCreatedBy().getDisplayName());
            pojo.setTaxPayer(invoiceOffense.getInvoice().getPayer().getDisplayName());
            pojo.setPlateNumber(invoiceOffense.getInvoice().getVehicle() != null ? invoiceOffense.getInvoice().getVehicle().getPlateNumber().getPlateNumber() : "");
            pojo.setOffense(invoiceOffense.getOffense().getName());
            pojo.setInvoiceID(invoiceOffense.getReference());
            pojo.setDateSold(invoiceOffense.getPaymentDate().format(df));
            pojo.setAmount(invoiceOffense.getAmount());

            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<VIOReportPojo> getVIOReport(List<PortalUser> users,  String createdBefore,  String createdAfter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return users.stream().map(portalUser -> {
            JPAQuery<Invoice> invoiceJPAQuery = appRepository.startJPAQuery(QInvoice.invoice)
                    .where(QInvoice.invoice.createdBy.eq(portalUser));


            if (createdAfter != null){
                invoiceJPAQuery.where(QInvoice.invoice.createdAt.goe(LocalDate.parse(createdAfter, formatter).atStartOfDay()));
            }

            if (createdBefore != null){
                invoiceJPAQuery.where(QInvoice.invoice.createdAt.loe(LocalDate.parse(createdBefore, formatter).atTime(LocalTime.MAX)));
            }

            List<Invoice> invoices =  invoiceJPAQuery.fetch();

            VIOReportPojo pojo = new VIOReportPojo();
            pojo.setName(portalUser.getDisplayName());
            pojo.setAssessmentDone(invoices.size());

            List<Double> amount = new ArrayList<>();
            List<Double> paid = new ArrayList<>();
            List<Double> owed = new ArrayList<>();

            for(Invoice invoice:invoices){
                amount.add(invoice.getAmount());
                if (invoice.getPaymentStatus() == PaymentStatus.PAID){
                    paid.add(invoice.getAmount());
                } else {
                    owed.add(invoice.getAmount());
                }
            }

            pojo.setTotalAmount(amount.stream().mapToDouble(Double::doubleValue).sum());
            pojo.setTotalPaid(paid.stream().mapToDouble(Double::doubleValue).sum());
            pojo.setTotalOwed(owed.stream().mapToDouble(Double::doubleValue).sum());

            return pojo;
        }).collect(Collectors.toList());
    }

    @Override
    public Resource exportStockReport(List<StockReportPojo> pojos, String type) throws IOException {
        if (type.equals("pdf")) {
            String file  = appConfigurationProperties.getPrintDirectory() + "mla_stock_report.pdf";

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));

            Document doc = new Document(pdfDoc);

            Table table = new Table(6);
            int i = 1;

            Paragraph header = new Paragraph("MLA STOCK REPORT")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);

            table.setFontSize(10);
            table.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
            table.addHeaderCell("S/N");
            table.addHeaderCell("MLA");
            table.addHeaderCell("STATION");
            table.addHeaderCell("ASSIGNED PLATES");
            table.addHeaderCell("SOLD PLATES");
            table.addHeaderCell("STOCK LEVEL");
            table.getHeader().setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);

            for (StockReportPojo reportPojo:pojos) {
                table.addCell(Integer.toString(i++));
                table.addCell(reportPojo.getMla()).setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);
                table.addCell(reportPojo.getStation());
                table.addCell(String.valueOf(reportPojo.getAssigned()));
                table.addCell(String.valueOf(reportPojo.getSold()));
                table.addCell(String.valueOf(reportPojo.getCurrentQuantity()));
            }

            doc.add(header);
            doc.add(table);
            doc.close();
            log.info("Table created successfully..");

            return pdfRenderToMultiplePages.loadFileAsResource(file);
        } else {

            final int SN = 1;
            final int MLA = 2;
            final int STATION = 3;
            final int ASSIGNED_PLATES = 4;
            final int SOLD_PLATES = 5;
            final int STOCK_LEVEL = 6;

            String uploadTemplatePath = "/excel/mla_stock_report.xlsx";
            InputStream inputStream = getClass().getResourceAsStream(uploadTemplatePath);
            if (inputStream == null) throw new IllegalArgumentException("File not found");

            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet("sheet1");
            int firstRow = sheet.getFirstRowNum() + 2;
            Cell cell = null;
            Row row = null;

            for (StockReportPojo reportPojo : pojos) {
                row = sheet.createRow(firstRow++);
                int columnNumber = 0;
                for (int i = 0; i <= 7; i++) {
                    cell = row.createCell(columnNumber++);
                    switch (columnNumber) {
                        case SN:
                            cell.setCellValue(row.getRowNum() - 1);
                            break;
                        case MLA:
                            cell.setCellValue(reportPojo.getMla());
                            break;
                        case STATION:
                            cell.setCellValue(reportPojo.getStation());
                            break;
                        case ASSIGNED_PLATES:
                            cell.setCellValue(reportPojo.getAssigned());
                            break;
                        case SOLD_PLATES:
                            cell.setCellValue(reportPojo.getSold());
                            break;
                        case STOCK_LEVEL:
                            cell.setCellValue(reportPojo.getCurrentQuantity());
                            break;
                    }
                }
            }
            return pdfRenderToMultiplePages.loadFileAsResource(pdfRenderToMultiplePages.createDirectory(workbook));
        }
    }

    @Override
    public Resource exportServiceSalesReport(List<SalesReportPojo> pojos, String type) throws IOException {
        if (type.equals("pdf")) {
            String file  = appConfigurationProperties.getPrintDirectory() + "service_sales_report.pdf";

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));

            Document doc = new Document(pdfDoc, PageSize.A3);

            Table table = new Table(9);
            int i = 1;

            Paragraph header = new Paragraph("SERVICE SALES REPORT")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);

            table.setFontSize(10);
            table.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
            table.addHeaderCell("S/N");
            table.addHeaderCell("TRANSACTION REF");
            table.addHeaderCell("MLA");
            table.addHeaderCell("STATION");
            table.addHeaderCell("BUYER");
            table.addHeaderCell("SERVICE TYPE");
            table.addHeaderCell("REGISTRATION TYPE");
            table.addHeaderCell("TRANSACTION DATE");
            table.addHeaderCell("AMOUNT");
            table.getHeader().setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);

            for (SalesReportPojo reportPojo:pojos) {
                table.addCell(Integer.toString(i++));
                table.addCell(reportPojo.getInvoiceID());
                table.addCell(reportPojo.getMla()).setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);
                table.addCell(reportPojo.getMlaStation());
                table.addCell(reportPojo.getTaxPayer());
                table.addCell(reportPojo.getServiceType());
                table.addCell(reportPojo.getRegType().name());
                table.addCell(reportPojo.getDateSold());
                table.addCell("N" + reportPojo.getAmount().toString());
            }

            doc.add(header);
            doc.add(table);
            doc.close();
            log.info("Table created successfully..");

            return pdfRenderToMultiplePages.loadFileAsResource(file);
        } else {

            final int SN = 1;
            final int TRANSACTION_REF = 2;
            final int MLA = 3;
            final int STATION = 4;
            final int BUYER = 5;
            final int SERVICE_TYPE = 6;
            final int REGISTRATION_TYPE = 7;
            final int TRANSACTION_DATE = 8;
            final int AMOUNT = 9;

            String uploadTemplatePath = "/excel/service_sales_report.xlsx";
            InputStream inputStream = getClass().getResourceAsStream(uploadTemplatePath);
            if (inputStream == null) throw new IllegalArgumentException("File not found");

            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet("sheet1");
            int firstRow = sheet.getFirstRowNum() + 2;
            Cell cell = null;
            Row row = null;

            for (SalesReportPojo reportPojo : pojos) {
                row = sheet.createRow(firstRow++);
                int columnNumber = 0;
                for (int i = 0; i <= 10; i++) {
                    cell = row.createCell(columnNumber++);
                    switch (columnNumber) {
                        case SN:
                            cell.setCellValue(row.getRowNum() - 1);
                            break;
                        case TRANSACTION_REF:
                            cell.setCellValue(reportPojo.getInvoiceID());
                            break;
                        case MLA:
                            cell.setCellValue(reportPojo.getMla());
                            break;
                        case STATION:
                            cell.setCellValue(reportPojo.getMlaStation());
                            break;
                        case BUYER:
                            cell.setCellValue(reportPojo.getTaxPayer());
                            break;
                        case SERVICE_TYPE:
                            cell.setCellValue(reportPojo.getServiceType());
                            break;
                        case REGISTRATION_TYPE:
                            cell.setCellValue(reportPojo.getRegType().name());
                            break;
                        case TRANSACTION_DATE:
                            cell.setCellValue(reportPojo.getDateSold());
                            break;
                        case AMOUNT:
                            cell.setCellValue("N" + reportPojo.getAmount());
                            break;
                    }
                }
            }
            return pdfRenderToMultiplePages.loadFileAsResource(pdfRenderToMultiplePages.createDirectory(workbook));
        }
    }

    @Override
    public Resource exportPlateNumberSalesReport(List<SalesReportDto> pojos, String type) throws IOException {
        if (type.equals("pdf")) {
            String file  = appConfigurationProperties.getPrintDirectory() + "plate-number_sales_report.pdf";

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));

            Document doc = new Document(pdfDoc);

            Table table = new Table(7);
            int i = 1;

            Paragraph header = new Paragraph("PLATE NUMBER SALES REPORT")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);

            table.setFontSize(10);
            table.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
            table.addHeaderCell("S/N");
            table.addHeaderCell("MLA");
            table.addHeaderCell("PLATE NUMBER");
            table.addHeaderCell("PLATE TYPE");
            table.addHeaderCell("STATION");
            table.addHeaderCell("TRANSACTION DATE");
            table.addHeaderCell("AMOUNT");
            table.getHeader().setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);

            for (SalesReportDto reportPojo:pojos) {
                table.addCell(Integer.toString(i++));
                table.addCell(reportPojo.getMla()).setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);
                table.addCell(reportPojo.getPlateNumber());
                table.addCell(reportPojo.getPlateType());
                table.addCell(reportPojo.getMlaStation());
                table.addCell(reportPojo.getDateSold());
                table.addCell("N" + reportPojo.getAmount().toString());
            }

            doc.add(header);
            doc.add(table);
            doc.close();
            log.info("Table created successfully..");

            return pdfRenderToMultiplePages.loadFileAsResource(file);
        }else {

            final int SN = 1;
            final int MLA = 2;
            final int PLATE_NUMBER = 3;
            final int PLATE_TYPE = 4;
            final int STATION = 5;
            final int TRANSACTION_DATE = 6;
            final int AMOUNT = 7;

            String uploadTemplatePath = "/excel/plate-number_sales_report.xlsx";
            InputStream inputStream = getClass().getResourceAsStream(uploadTemplatePath);
            if (inputStream == null) throw new IllegalArgumentException("File not found");

            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet("sheet1");
            int firstRow = sheet.getFirstRowNum() + 2;
            Cell cell = null;
            Row row = null;

            for (SalesReportDto reportPojo : pojos) {
                row = sheet.createRow(firstRow++);
                int columnNumber = 0;
                for (int i = 0; i <= 8; i++) {
                    cell = row.createCell(columnNumber++);
                    switch (columnNumber) {
                        case SN:
                            cell.setCellValue(row.getRowNum() - 1);
                            break;
                        case MLA:
                            cell.setCellValue(reportPojo.getMla());
                            break;
                        case STATION:
                            cell.setCellValue(reportPojo.getMlaStation());
                            break;
                        case PLATE_NUMBER:
                            cell.setCellValue(reportPojo.getPlateNumber());
                            break;
                        case PLATE_TYPE:
                            cell.setCellValue(reportPojo.getPlateType());
                            break;
                        case TRANSACTION_DATE:
                            cell.setCellValue(reportPojo.getDateSold());
                            break;
                        case AMOUNT:
                            cell.setCellValue("N" + reportPojo.getAmount());
                            break;
                    }
                }
            }
            return pdfRenderToMultiplePages.loadFileAsResource(pdfRenderToMultiplePages.createDirectory(workbook));
        }
    }

    @Override
    public Resource exportVIOReport(List<VIOReportPojo> pojos, String type) throws IOException {
        if (type.equals("pdf")) {
            String file  = appConfigurationProperties.getPrintDirectory() + "vio_report.pdf";

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));

            Document doc = new Document(pdfDoc);

            Table table = new Table(6);
            int i = 1;

            Paragraph header = new Paragraph("VIO ASSESSMENT REPORT")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);

            table.setFontSize(10);
            table.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
            table.addHeaderCell("S/N");
            table.addHeaderCell("VIO");
            table.addHeaderCell("TOTAL ASSESSMENT");
            table.addHeaderCell("TOTAL AMOUNT");
            table.addHeaderCell("TOTAL AMOUNT PAID");
            table.addHeaderCell("TOTAL AMOUNT OWED");
            table.getHeader().setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);

            for (VIOReportPojo reportPojo:pojos) {
                table.addCell(Integer.toString(i++));
                table.addCell(reportPojo.getName()).setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);
                table.addCell(String.valueOf(reportPojo.getAssessmentDone()));
                table.addCell("N" + reportPojo.getTotalAmount());
                table.addCell("N" + reportPojo.getTotalPaid());
                table.addCell("N" + reportPojo.getTotalOwed());
            }

            doc.add(header);
            doc.add(table);
            doc.close();
            log.info("Table created successfully..");

            return pdfRenderToMultiplePages.loadFileAsResource(file);
        } else {

            final int SN = 1;
            final int VIO = 2;
            final int TOTAL_ASSESSMENT = 3;
            final int TOTAL_AMOUNT = 4;
            final int TOTAL_AMOUNT_PAID = 5;
            final int TOTAL_AMOUNT_OWED = 6;

            String uploadTemplatePath = "/excel/vio_report.xlsx";
            InputStream inputStream = getClass().getResourceAsStream(uploadTemplatePath);
            if (inputStream == null) throw new IllegalArgumentException("File not found");

            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet("sheet1");
            int firstRow = sheet.getFirstRowNum() + 2;
            Cell cell = null;
            Row row = null;

            for (VIOReportPojo reportPojo : pojos) {
                row = sheet.createRow(firstRow++);
                int columnNumber = 0;
                for (int i = 0; i <= 7; i++) {
                    cell = row.createCell(columnNumber++);
                    switch (columnNumber) {
                        case SN:
                            cell.setCellValue(row.getRowNum() - 1);
                            break;
                        case VIO:
                            cell.setCellValue(reportPojo.getName());
                            break;
                        case TOTAL_ASSESSMENT:
                            cell.setCellValue(reportPojo.getAssessmentDone());
                            break;
                        case TOTAL_AMOUNT:
                            cell.setCellValue("N" + reportPojo.getTotalAmount());
                            break;
                        case TOTAL_AMOUNT_PAID:
                            cell.setCellValue("N" + reportPojo.getTotalPaid());
                            break;
                        case TOTAL_AMOUNT_OWED:
                            cell.setCellValue("N" + reportPojo.getTotalOwed());
                            break;
                    }
                }
            }
            return pdfRenderToMultiplePages.loadFileAsResource(pdfRenderToMultiplePages.createDirectory(workbook));
        }
    }

    @Override
    public Resource exportOffenseReport(List<OffenseReportPojo> pojos, String type) throws IOException {
        if (type.equals("pdf")) {
            String file  = appConfigurationProperties.getPrintDirectory() + "offense_report.pdf";

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));

            Document doc = new Document(pdfDoc);

            Table table = new Table(8);
            int i = 1;

            Paragraph header = new Paragraph("OFFENSE REPORT")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);

            table.setFontSize(10);
            table.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
            table.addHeaderCell("S/N");
            table.addHeaderCell("PLATE NUMBER");
            table.addHeaderCell("TRANSACTION REF");
            table.addHeaderCell("VIO");
            table.addHeaderCell("PAYER");
            table.addHeaderCell("OFFENSE");
            table.addHeaderCell("TRANSACTION DATE");
            table.addHeaderCell("AMOUNT");
            table.getHeader().setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);

            for (OffenseReportPojo reportPojo:pojos) {
                table.addCell(Integer.toString(i++));
                table.addCell(reportPojo.getPlateNumber()).setBorder(new SolidBorder(2)).setTextAlignment(TextAlignment.CENTER);
                table.addCell(reportPojo.getInvoiceID());
                table.addCell(reportPojo.getVio());
                table.addCell(reportPojo.getTaxPayer());
                table.addCell(reportPojo.getOffense());
                table.addCell(reportPojo.getDateSold());
                table.addCell("N" + reportPojo.getAmount().toString());
            }

            doc.add(header);
            doc.add(table);
            doc.close();
            log.info("Table created successfully..");

            return pdfRenderToMultiplePages.loadFileAsResource(file);
        } else {

            final int SN = 1;
            final int PLATE_NUMBER = 2;
            final int TRANSACTION_REF = 3;
            final int VIO = 4;
            final int PAYER = 5;
            final int OFFENSE = 6;
            final int TRANSACTION_DATE = 7;
            final int AMOUNT = 8;

            String uploadTemplatePath = "/excel/offenses_report.xlsx";
            InputStream inputStream = getClass().getResourceAsStream(uploadTemplatePath);
            if (inputStream == null) throw new IllegalArgumentException("File not found");

            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet("sheet1");
            int firstRow = sheet.getFirstRowNum() + 2;
            Cell cell = null;
            Row row = null;

            for (OffenseReportPojo reportPojo : pojos) {
                row = sheet.createRow(firstRow++);
                int columnNumber = 0;
                for (int i = 0; i <= 9; i++) {
                    cell = row.createCell(columnNumber++);
                    switch (columnNumber) {
                        case SN:
                            cell.setCellValue(row.getRowNum() - 1);
                            break;
                        case PLATE_NUMBER:
                            cell.setCellValue(reportPojo.getPlateNumber());
                            break;
                        case TRANSACTION_REF:
                            cell.setCellValue(reportPojo.getInvoiceID());
                            break;
                        case VIO:
                            cell.setCellValue(reportPojo.getVio());
                            break;
                        case PAYER:
                            cell.setCellValue(reportPojo.getTaxPayer());
                            break;
                        case OFFENSE:
                            cell.setCellValue(reportPojo.getOffense());
                            break;
                        case TRANSACTION_DATE:
                            cell.setCellValue(reportPojo.getDateSold());
                            break;
                        case AMOUNT:
                            cell.setCellValue("N" + reportPojo.getAmount());
                            break;
                    }
                }
            }
            return pdfRenderToMultiplePages.loadFileAsResource(pdfRenderToMultiplePages.createDirectory(workbook));
        }
    }
}
