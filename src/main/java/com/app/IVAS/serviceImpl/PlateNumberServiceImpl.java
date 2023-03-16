package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.dto.PlateNumberDto;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.PlateNumberType;
import com.app.IVAS.entity.QStock;
import com.app.IVAS.entity.Stock;
import com.app.IVAS.entity.userManagement.Lga;
import com.app.IVAS.repository.*;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.PlateNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public String createStock(PlateNumberDto dto) {
        Lga lga = lgaRepository.findById(dto.getLgaId()).orElseThrow(RuntimeException::new);

       List<Stock>  findStock = appRepository.startJPAQuery(QStock.stock)
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
           stock.setQuantity(dto.getLastNumber() - dto.getFirstNumber());
           stock.setCreatedBy(jwtService.user);
           generatePlateNumbers(stockRepository.save(stock));

           return "Stock created successfully";

       } else {
           return "Stock already exists";
       }
    }


    private void generatePlateNumbers(Stock stock){
        for (int i = 0; i<=stock.getQuantity().intValue(); i++){

            PlateNumber plateNumber = new PlateNumber();
            plateNumber.setStartCode(stock.getLga().getCode());
            plateNumber.setNumber(stock.getStartRange() + i);
            plateNumber.setEndCode(stock.getEndCode());
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
