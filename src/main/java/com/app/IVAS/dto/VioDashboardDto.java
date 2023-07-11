package com.app.IVAS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VioDashboardDto {
    private Double rw;
    private Double dtt;
    private Long vehicles;
    private Double offence;
    private Double permit;
    private Long approvals;
}
