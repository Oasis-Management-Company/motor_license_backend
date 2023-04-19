package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.ActivityStatusConstant;
import com.app.IVAS.Enum.AssignmentStatusConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.dto.PlateNumberDto;
import com.app.IVAS.dto.PlateNumberPojo;
import com.app.IVAS.dto.StockPojo;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QStock;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.ActivityLogService;
import com.app.IVAS.service.PlateNumberService;
import com.app.IVAS.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlateNumberServiceImpl implements PlateNumberService {

    private final JwtService jwtService;
    private final StockRepository stockRepository;
    private final AppRepository appRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final PlateNumberTypeRepository plateNumberTypeRepository;
    private final PlateNumberSubTypeRepository plateNumberSubTypeRepository;
    private final PortalUserRepository portalUserRepository;
    private final PlateNumberRequestRepository plateNumberRequestRepository;
    private final PrefixRepository prefixRepository;
    private final ActivityLogService activityLogService;

    @Override
    public Map<String,Object> createStock(PlateNumberDto dto) {
        Map<String,Object> body = new HashMap<>();

        Prefix startCode = prefixRepository.findById(dto.getStartCode()).orElseThrow(RuntimeException::new);

       List<Stock> findStock = appRepository.startJPAQuery(QStock.stock)
                .where(QStock.stock.startCode.eq(startCode))
                .where(QStock.stock.endCode.equalsIgnoreCase(dto.getEndCode()))
                .where((QStock.stock.startRange.loe(dto.getFirstNumber()).and(QStock.stock.endRange.goe(dto.getFirstNumber()))).or
                        (QStock.stock.endRange.goe(dto.getLastNumber()).and(QStock.stock.startRange.loe(dto.getLastNumber()))))
                .fetch();

       if (findStock.isEmpty()){
           Stock stock = new Stock();
           stock.setStartCode(startCode);
           stock.setEndCode(dto.getEndCode());
           stock.setType(plateNumberTypeRepository.findById(dto.getType()).orElseThrow(RuntimeException::new));
           if(dto.getSubType() != null){
               stock.setSubType(plateNumberSubTypeRepository.findById(dto.getSubType()).orElseThrow(RuntimeException::new));
           }
           stock.setStartRange(dto.getFirstNumber());
           stock.setEndRange(dto.getLastNumber());
           stock.setQuantity(dto.getLastNumber() - dto.getFirstNumber() + 1);
           stock.setCreatedBy(jwtService.user);
           generatePlateNumbers(stockRepository.save(stock));


           body.put("Message", "Stock created successfully");
           activityLogService.createActivityLog(("Stock series: " + stock.getStartCode() + " " + stock.getStartRange() + "-"
                   + stock.getEndRange() + " " + stock.getEndCode() + " of plate number type: " + stock.getType() + " was created"), ActivityStatusConstant.CREATE);

       } else {
           body.put("Error", "Stock already exists");
       }
        return body;
    }

    @Override
    public List<PlateNumberPojo> getPlateNumbers(List<PlateNumber> plateNumbers) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");
        return plateNumbers.stream().map(plateNumber -> {
            PlateNumberPojo pojo = new PlateNumberPojo();
            pojo.setId(plateNumber.getId());
            pojo.setPlateNumber(plateNumber.getPlateNumber());
            pojo.setType(plateNumber.getType().getName());
            pojo.setSubType(plateNumber.getSubType() != null ? plateNumber.getSubType().getName() : null);
            pojo.setDateCreated(plateNumber.getCreatedAt().format(df));
            pojo.setStatus(plateNumber.getPlateNumberStatus());
            pojo.setAgent(plateNumber.getAgent() != null ? plateNumber.getAgent().getDisplayName() : null);
            pojo.setOwner(plateNumber.getOwner() != null ? plateNumber.getOwner().getDisplayName() : null);
            return pojo;

        }).collect(Collectors.toList());
    }

    @Override
    public List<StockPojo> getStock(List<Stock> stocks) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");
        return stocks.stream().map(stock -> {
            StockPojo pojo = new StockPojo();
            pojo.setId(stock.getId());
            pojo.setLga(stock.getStartCode().getCode());
            pojo.setRange(stock.getStartRange() + " - " + stock.getEndRange());
            pojo.setEndCode(stock.getEndCode());
            pojo.setType(stock.getType().getName());
            pojo.setSubType(stock.getSubType() != null ? stock.getSubType().getName() : null);
            pojo.setDateCreated(stock.getCreatedAt().format(df));
            pojo.setQuantity(stock.getQuantity());
            pojo.setCreatedBy(stock.getCreatedBy().getDisplayName());
            pojo.setInitialQuantity(stock.getEndRange() - stock.getStartRange() + 1);
            pojo.setAssigned((long) plateNumberRepository.findByStockAndPlateNumberStatus(stock, PlateNumberStatus.ASSIGNED).size());
            pojo.setSold(pojo.getInitialQuantity() - pojo.getQuantity());
            return pojo;

        }).collect(Collectors.toList());
    }

    @Override
    public void assignPlateNumbers(List<Long> plateNumbers, Long mlaId, Long requestId) {
        PortalUser mla = portalUserRepository.findById(mlaId).get();
        PlateNumberRequest request = plateNumberRequestRepository.findById(requestId).get();
        for (Long id:plateNumbers){
            PlateNumber plateNumber = plateNumberRepository.findById(id).get();
            plateNumber.setAgent(mla);
            plateNumber.setRequest(request);
            plateNumber.setPlateNumberStatus(PlateNumberStatus.ASSIGNED);
            plateNumber.setLastUpdatedBy(jwtService.user);
            plateNumberRepository.save(plateNumber);
            activityLogService.createActivityLog(("Plate number: " + plateNumber.getPlateNumber() + " of type "
                    + plateNumber.getType() + " was assigned to MLA with name: " + plateNumber.getAgent().getDisplayName()), ActivityStatusConstant.ASSIGNED);
        }
        request.setAssignmentStatus(AssignmentStatusConstant.ASSIGNED);
        activityLogService.createActivityLog(("Plate number request with tracking id: " + request.getTrackingId() + " has been assigned plate numbers"), ActivityStatusConstant.ASSIGNED);
        plateNumberRequestRepository.save(request);
    }

    @Override
    public void assignCustomPlateNumber(Long mlaId, Long requestId) {
        PortalUser mla = portalUserRepository.findById(mlaId).get();
        PlateNumberRequest request = plateNumberRequestRepository.findById(requestId).get();

        PlateNumber plateNumber = new PlateNumber();
        plateNumber.setPlateNumber(request.getFancyPlate());
        plateNumber.setPlateNumberStatus(PlateNumberStatus.ASSIGNED);
        plateNumber.setType(request.getPlateNumberType());
        plateNumber.setAgent(mla);
        plateNumber.setRequest(request);
        plateNumber.setStatus(GenericStatusConstant.ACTIVE);
        plateNumber.setCreatedBy(jwtService.user);
        plateNumberRepository.save(plateNumber);
        activityLogService.createActivityLog(("Plate number: " + plateNumber.getPlateNumber() + " of type "
                + plateNumber.getType() + " was assigned to MLA with name: " + plateNumber.getAgent().getDisplayName()), ActivityStatusConstant.ASSIGNED);

        request.setAssignmentStatus(AssignmentStatusConstant.ASSIGNED);
        activityLogService.createActivityLog(("Plate number request with tracking id: " + request.getTrackingId() + " has been assigned plate numbers"), ActivityStatusConstant.ASSIGNED);
        plateNumberRequestRepository.save(request);
    }

    @Override
    public void deleteStock(Long stockId) {
       Stock stock = stockRepository.findById(stockId).orElseThrow(RuntimeException::new);

       List<PlateNumber> plateNumberList = plateNumberRepository.findByStock(stock);

        int count = (int) plateNumberList.stream().filter(plateNumber -> plateNumber.getPlateNumberStatus().equals(PlateNumberStatus.ASSIGNED) ||
                plateNumber.getPlateNumberStatus().equals(PlateNumberStatus.SOLD)).count();

        if (count == 0){
            plateNumberRepository.deleteAll(plateNumberList);
            activityLogService.createActivityLog(("Stock series: " + stock.getStartCode() + " " + stock.getStartRange() + "-"
                    + stock.getEndRange() + " " + stock.getEndCode() + " of plate number type: " + stock.getType() + " was deleted"), ActivityStatusConstant.DELETE);
            stockRepository.delete(stock);
        }
    }

    @Override
    public void recallPlateNumber(Long id) {
        PlateNumber plateNumber = plateNumberRepository.findById(id).orElseThrow(RuntimeException::new);

        plateNumber.setPlateNumberStatus(PlateNumberStatus.UNASSIGNED);
        activityLogService.createActivityLog(("Plate number: " + plateNumber.getPlateNumber() + " of type "
                + plateNumber.getType() + " was unassigned from MLA with name: " + plateNumber.getAgent().getDisplayName()), ActivityStatusConstant.UNASSIGNED);
        plateNumber.setAgent(null);
        plateNumberRepository.save(plateNumber);
    }


    private void generatePlateNumbers(Stock stock){
        for (int i = 0; i<=(stock.getQuantity().intValue() - 1); i++){

            PlateNumber plateNumber = new PlateNumber();
            plateNumber.setPlateNumber(stock.getStartCode().getCode() +  ((stock.getStartRange() + i) < 10 ? "0" + (stock.getStartRange() + i) : stock.getStartRange() + i) + stock.getEndCode());
            plateNumber.setPlateNumberStatus(PlateNumberStatus.UNASSIGNED);
            plateNumber.setType(stock.getType());
            if (stock.getSubType() != null){
                plateNumber.setSubType(stock.getSubType());
            }
            plateNumber.setStock(stock);
            plateNumber.setStatus(GenericStatusConstant.ACTIVE);
            plateNumber.setCreatedBy(jwtService.user);
            plateNumberRepository.save(plateNumber);
            activityLogService.createActivityLog(("Plate number: " + plateNumber.getPlateNumber() + " of type " + plateNumber.getType() + " was created"), ActivityStatusConstant.CREATE);
        }
    }
}
