package com.app.IVAS.service;


import com.app.IVAS.dto.PlateNumberDto;
import com.app.IVAS.dto.PlateNumberPojo;
import com.app.IVAS.dto.StockPojo;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.Stock;

import java.util.List;
import java.util.Map;

public interface PlateNumberService {

    Map<String,Object> createStock(PlateNumberDto dto);

    List<PlateNumberPojo> getPlateNumbers(List<PlateNumber> plateNumbers);

    List<StockPojo> getStock(List<Stock> stocks);

    void assignPlateNumbers(List<Long> plateNumbers, Long mlaId, Long requestId);

    void assignCustomPlateNumber(Long mlaId, Long requestId);

    void deleteStock(Long stockId);

    void changeStockPlateNumberType(Long stockId, Long typeId);

    void recallPlateNumber(Long id);
}
