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
@Table(name = "WORK_FLOW_LOG")
public class WorkFLowLog {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    private String action;

    @ManyToOne
    private PortalUser createdBy;

    @ManyToOne
    private PlateNumberRequest request;

    @Basic
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
