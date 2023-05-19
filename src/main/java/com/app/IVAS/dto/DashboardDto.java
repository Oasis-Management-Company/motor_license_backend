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
    private Double today;
    private Double thisWeek;
    private Double thisMonth;
    private Double thisYear;
    private Double pToday;
    private Double pThisWeek;
    private Double pThisMonth;
    private Double pThisYear;
    private int taxPayer;
    private int vehicles;
    private int offenses;

}
