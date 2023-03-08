package com.app.IVAS.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "ZONAL_OFFICE")
public class ZonalOffice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    private String address;
    private String head;
    private String contact;

    @ManyToOne
    @JoinColumn(name = "ZONE", referencedColumnName = "id")
    private Zone zone;
}
