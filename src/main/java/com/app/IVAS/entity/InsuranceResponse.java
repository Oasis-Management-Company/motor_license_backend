package com.app.IVAS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "INSURANCE_RESPONSE")
public class InsuranceResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_ID", referencedColumnName = "id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID", referencedColumnName = "id")
    private Invoice invoice;

    private String transId;
    private String reprintCode;
    private String policyNumber;
    private String certificateNumber;
    private String fullName;
    private String registrationNumber;
    private String chassisNumber;
    private String engineNumber;
    private String vehicleMake;
    private String vehicleModel;
    private String yearOfMake;
    private String commencementDate;
    private String expirationDate;
    private String renewalDate;
    private String niidResponse;
}
