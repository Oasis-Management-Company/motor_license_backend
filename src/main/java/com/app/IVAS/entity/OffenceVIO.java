package com.app.IVAS.entity;

import com.app.IVAS.entity.userManagement.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "OFFENCE_VIO")
public class OffenceVIO extends StatusEntity {
    private String offenceName;
    private String offenceCode;
    private Long point;
    private Double fine;
    private String actionTaken;
    private String revenueCode;

}
