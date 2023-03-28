package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.dto.PlateNumberDto;
import com.app.IVAS.dto.PlateNumberPojo;
import com.app.IVAS.dto.StockPojo;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.PlateNumberRequest;
import com.app.IVAS.entity.QStock;
import com.app.IVAS.entity.Stock;
import com.app.IVAS.entity.userManagement.Lga;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.response.JsonResponse;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.PlateNumberService;
import com.google.inject.internal.ErrorsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlateNumberServiceImpl implements PlateNumberService {

    private final LgaRepository lgaRepository;
    private final JwtService jwtService;
    private final StockRepository stockRepository;
    private final AppRepository appRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final PlateNumberTypeRepository plateNumberTypeRepository;
    private final PlateNumberSubTypeRepository plateNumberSubTypeRepository;
    private final PortalUserRepository portalUserRepository;
    private final PlateNumberRequestRepository plateNumberRequestRepository;

    @Override
    public Map<String,Object> createStock(PlateNumberDto dto) {
        Map<String,Object> body = new HashMap<>();
        Lga lga = lgaRepository.findById(dto.getLgaId()).orElseThrow(RuntimeException::new);

       List<Stock> findStock = appRepository.startJPAQuery(QStock.stock)
                .where(QStock.stock.lga.eq(lga))
                .where(QStock.stock.endCode.equalsIgnoreCase(dto.getEndCode()))
                .where((QStock.stock.startRange.loe(dto.getFirstNumber()).and(QStock.stock.endRange.goe(dto.getFirstNumber()))).or
                        (QStock.stock.endRange.goe(dto.getLastNumber()).and(QStock.stock.startRange.loe(dto.getLastNumber()))))
                .fetch();

       if (findStock.isEmpty()){
           Stock stock = new Stock();
           stock.setLga(lga);
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
            pojo.setLga(stock.getLga().getCode());
            pojo.setRange(stock.getStartRange() + " - " + stock.getEndRange());
            pojo.setEndCode(stock.getEndCode());
            pojo.setType(stock.getType().getName());
            pojo.setSubType(stock.getSubType() != null ? stock.getSubType().getName() : null);
            pojo.setDateCreated(stock.getCreatedAt().format(df));
            pojo.setQuantity(String.valueOf(stock.getQuantity()));
            pojo.setCreatedBy(stock.getCreatedBy().getDisplayName());
            pojo.setInitialQuantity(String.valueOf(stock.getEndRange() - stock.getStartRange() + 1));
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
        }
    }


    private void generatePlateNumbers(Stock stock){
        for (int i = 0; i<=stock.getQuantity().intValue(); i++){

            PlateNumber plateNumber = new PlateNumber();
            plateNumber.setPlateNumber(stock.getLga().getCode() + (stock.getStartRange() + i) + stock.getEndCode());
            plateNumber.setPlateNumberStatus(PlateNumberStatus.UNASSIGNED);
            plateNumber.setType(stock.getType());
            if (stock.getSubType() != null){
                plateNumber.setSubType(stock.getSubType());
            }
            plateNumber.setStock(stock);
            plateNumber.setStatus(GenericStatusConstant.ACTIVE);
            plateNumber.setCreatedBy(jwtService.user);
            plateNumberRepository.save(plateNumber);
        }
    }
}
