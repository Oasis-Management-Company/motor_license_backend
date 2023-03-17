package com.app.IVAS.service;


import com.app.IVAS.dto.PlateNumberDto;
import com.app.IVAS.dto.PlateNumberPojo;
import com.app.IVAS.dto.StockPojo;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.Stock;

import java.util.List;

public interface PlateNumberService {

    String createStock(PlateNumberDto dto);

    List<PlateNumberPojo> getPlateNumbers(List<PlateNumber> plateNumbers);

    List<StockPojo> getStock(List<Stock> stocks);
}
