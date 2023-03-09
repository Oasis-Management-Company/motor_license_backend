package com.app.IVAS.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "PREFERRED_PLATE")
public class PreferredPlate {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    private String code;
    private Long numberOfPlates;

    @ManyToOne
    private PlateNumberRequest request;
}
