package com.app.IVAS.entity;


import com.app.IVAS.Enum.RegType;
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
@Table(name = "SERVICE_TYPE")
public class ServiceType extends StatusEntity {
    private String name;
    private Double price;
    private Long durationInMonth;
    private String code;

    @Enumerated(EnumType.STRING)
    private RegType regType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "vehicle_category_id", referencedColumnName = "ID")
    private VehicleCategory category;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "plate_number_type_id", referencedColumnName = "ID")
    private PlateNumberType plateNumberType;

}
