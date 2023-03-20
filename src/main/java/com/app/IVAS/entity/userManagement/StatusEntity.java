package com.app.IVAS.entity.userManagement;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class StatusEntity implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    @Basic
    protected LocalDateTime dateDeactivated;
    @Basic
    @Column(
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    protected GenericStatusConstant status = GenericStatusConstant.ACTIVE;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private PortalUser deactivatedBy;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private PortalUser lastUpdatedBy;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private PortalUser createdBy;
    @Basic
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @Basic
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastUpdatedAt;

}