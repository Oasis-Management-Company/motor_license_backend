package com.app.IVAS.entity;


import com.app.IVAS.entity.userManagement.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "OFFENSE")
public class Offense extends StatusEntity {
    private String name;
    private String offenceCode;
    private Long point;
    private Double fine;
    private String actionTaken;
    private String revenueCode;


}
