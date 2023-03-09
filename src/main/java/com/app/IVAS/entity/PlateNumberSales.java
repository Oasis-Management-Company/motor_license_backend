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
@Table(name = "PLATE_NUMBER_SALES")
public class PlateNumberSales extends StatusEntity {
  private String referenceNumber;

  @ManyToOne
  private Vehicle vehicle;

  @OneToOne
  private Invoice invoice;
}
