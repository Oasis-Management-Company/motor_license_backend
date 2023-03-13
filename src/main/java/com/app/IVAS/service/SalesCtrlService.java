package com.app.IVAS.service;

import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.entity.Sales;

import java.util.List;

public interface SalesCtrlService {
    Sales SaveSales(SalesDto sales);

    List<SalesDto> GetSales(List<Sales> results);
}
