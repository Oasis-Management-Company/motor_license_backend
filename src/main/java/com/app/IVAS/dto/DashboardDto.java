package com.app.IVAS.dto;

import lombok.Data;

@Data
public class DashboardDto {
    private int totalMla;
    private int totalplate;
    private int totalStations;
    private int totalSales;
    private int totalStock;
    private int totalRequest;
    private Double totalPlateAmount;
    private Double totalAmount;
}
