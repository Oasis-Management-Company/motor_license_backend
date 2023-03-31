package com.app.IVAS.entity;

import com.app.IVAS.entity.userManagement.PortalUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "STOCK")
public class Stock {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    private Prefix startCode;

    private String endCode;
    private Long startRange;
    private Long endRange;
    private Long quantity;

    @ManyToOne
    private PlateNumberType type;
    @ManyToOne
    private PlateNumberSubType subType;

    @ManyToOne
    private PortalUser createdBy;

    @Basic
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
