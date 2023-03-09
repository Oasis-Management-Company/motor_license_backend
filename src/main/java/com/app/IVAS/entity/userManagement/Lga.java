package com.app.IVAS.entity.userManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "LGA")
public class Lga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "STATE_ID")
    private Long stateId;

    @Column(name = "CODE")
    private String code;

    @Column(name = "STATUS")
    private String status;

}
