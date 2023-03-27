package com.app.IVAS.entity;


import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "PLATE_NUMBER")
public class PlateNumber extends StatusEntity {

    private String plateNumber;

    @Enumerated(EnumType.STRING)
    private PlateNumberStatus plateNumberStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "TYPE_ID", referencedColumnName = "id")
    private PlateNumberType type;

    @JsonIgnore
    @JoinColumn(name = "AGENT_ID", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PortalUser agent;

    @JsonIgnore
    @JoinColumn(name = "SUB_TYPE_ID", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PlateNumberSubType subType;

    @JsonIgnore
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PortalUser owner;

    @JsonIgnore
    @JoinColumn(name = "STOCK_ID", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Stock stock;

    @JsonIgnore
    @JoinColumn(name = "REQUEST_ID", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PlateNumberRequest request;
}
