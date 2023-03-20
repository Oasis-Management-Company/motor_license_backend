package com.app.IVAS.entity;


import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
    @ManyToOne
    private PortalUser User;
    @ManyToOne
    private VehicleModel vehicleModel;
    @ManyToOne
    private VehicleCategory vehicleCategory;
    private String policySector;
    private Long passengers;
    private String color;
}
