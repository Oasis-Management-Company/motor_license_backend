package com.app.IVAS.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "INVOICE_SERVICE_TYPE")
public class InvoiceServiceType {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    private Invoice invoice;

    @ManyToOne
    private ServiceType serviceType;
}
