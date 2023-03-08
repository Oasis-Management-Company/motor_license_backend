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
@Table(name = "SERVICE_TYPE")
public class ServiceType extends StatusEntity {
    private String name;
    private Double price;
}
