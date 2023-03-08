package com.app.IVAS.entity.userManagement;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
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
    protected GenericStatusConstant status;
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
    private LocalDateTime createdAt;
    @Basic
    private LocalDateTime lastUpdatedAt;

}