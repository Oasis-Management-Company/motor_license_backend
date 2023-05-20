package com.app.IVAS.entity;


import com.app.IVAS.Enum.ApprovalStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "VEHCILE")
public class Vehicle extends StatusEntity {

    private String chasisNumber;
    private String engineNumber;

    @OneToOne
    private PlateNumber plateNumber;

    @JsonIgnore
    @ManyToOne
    private PortalUser portalUser;

    @ManyToOne
    private InsuranceCompany insurance;

    @ManyToOne
    private VehicleModel vehicleModel;

    @ManyToOne
    private VehicleCategory vehicleCategory;

    private String policySector;
    private Long passengers;
    private String color;
    private String year;
    private String load;
    private String capacity;
    private String insuranceNumber;
    private String permit;

    private Long parentId;

    private String plateEdit;

    @Enumerated(EnumType.STRING)
    private RegType regType = RegType.REGISTRATION;

}
