package com.app.IVAS.entity;


import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "PLATE_NUMBER")
public class PlateNumber extends StatusEntity {

    private String plateNumber;

    @Enumerated(EnumType.STRING)
    private PlateNumberStatus plateNumberStatus;

    @ManyToOne
    private PlateNumberType type;
    @ManyToOne
    private PlateNumberSubType subType;
    @ManyToOne
    private PortalUser agent;
    @ManyToOne
    private PortalUser owner;
    @ManyToOne
    private Stock stock;
}
