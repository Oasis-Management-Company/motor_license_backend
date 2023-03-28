package com.app.IVAS.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "OTHER_SERVICE_TYPE")
public class OtherServiceType {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    private String name;
    private Double price;

    @ManyToOne
    private VehicleCategory category;
}
