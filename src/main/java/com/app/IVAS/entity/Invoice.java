package com.app.IVAS.entity;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "INVOICE")
public class Invoice extends StatusEntity {
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String paymentRef;

    @JsonIgnore
    @JoinColumn(name = "PAYER_ID", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PortalUser payer;

    private Double amount;
    private LocalDateTime paymentDate;

    @JsonIgnore
    @JoinColumn(name = "VEHICLE_ID", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;
}
