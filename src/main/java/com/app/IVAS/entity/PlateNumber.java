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

    private String startCode;
    private Long number;
    private String endCode;

    @Enumerated(EnumType.STRING)
    private PlateNumberStatus plateNumberStatus;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "TYPE_ID", referencedColumnName = "id")
    private PlateNumberType type;

    @JsonIgnore
    @JoinColumn(name = "AGENT_ID", referencedColumnName = "id")
    @ManyToOne
    private PortalUser agent;

    @ManyToOne
    private PlateNumberSubType subType;

    @JsonIgnore
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "id")
    @ManyToOne
    private PortalUser owner;

    @JsonIgnore
    @JoinColumn(name = "STOCK_ID", referencedColumnName = "id")
    @ManyToOne
    private Stock stock;
}
