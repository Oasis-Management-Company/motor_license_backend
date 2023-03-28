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
@Table(name = "SERVICE_TYPE")
public class ServiceType extends StatusEntity {
    private String name;
    private Double price;
    private Long durationInMonth;

    @ManyToOne
    private VehicleCategory category;

<<<<<<< HEAD
    @ManyToOne
    @JoinColumn(name = "plate_number_type_id", referencedColumnName = "ID")
    private PlateNumberType plateNumberType;
=======
>>>>>>> ce3f2697037dcac6c65b86cce5ea5602e25c2c80
}
