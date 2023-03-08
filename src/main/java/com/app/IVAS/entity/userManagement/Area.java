package com.app.IVAS.entity.userManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "AREA")
public class Area implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "NAME", nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "LGA", referencedColumnName = "id")
  private Lga lga;

  @Column(name = "CODE")
  private String code;

}
