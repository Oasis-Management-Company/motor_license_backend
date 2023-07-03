package com.app.IVAS.entity;

import com.app.IVAS.Enum.ApprovalStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LEGACY_ASSESSMENT")
@Data
public class LegacyAssessment extends StatusEntity {

    private String reference_no = "IVS-LA-" + LocalDate.now().getYear()+ (int)(Math.random()* 12345607);

    @ManyToOne
    @JoinColumn(name = "VEHICLE_ID", referencedColumnName = "id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "INVOICE_ID", referencedColumnName = "id")
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;

    private LocalDateTime approvedDate;

    @ManyToOne
    @JoinColumn(name = "APPROVED_ID", referencedColumnName = "id")
    private PortalUser approvedBy;

    private String reason;

    @Enumerated(EnumType.STRING)
    private RegType plateType;

}
