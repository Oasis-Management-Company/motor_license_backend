package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.Utils.PDFRenderToMultiplePages;
import com.app.IVAS.configuration.AppConfigurationProperties;
import com.app.IVAS.dto.AssignedReportPojo;
import com.app.IVAS.dto.SalesReportDto;
import com.app.IVAS.dto.SalesReportPojo;
import com.app.IVAS.dto.StockReportPojo;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.entity.QPlateNumber;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.ReportService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
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
               dto.setPlateType(salesList.getVehicle().getPlateNumber().getType().getName());
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
            pojo.setRegType(invoiceService.getRegType());
            pojo.setInvoiceID(invoiceService.getReference());
            pojo.setMlaStation(invoiceService.getInvoice().getCreatedBy().getOffice().getName());
            pojo.setDateSold(invoiceService.getPaymentDate().format(df));
            pojo.setAmount(invoiceService.getAmount());
            pojos.add(pojo);
        }
        return pojos;
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

            Document doc = new Document(pdfDoc);

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
}
