package com.app.IVAS.entity;

import com.app.IVAS.Enum.RegType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "INVOICE_OFFENCE_TYPE")
public class InvoiceOffenseType {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    private Invoice invoice;

    @ManyToOne
    private Offense offense;

    private String reference;
    private String revenuecode;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private RegType regType;

    private LocalDateTime expiryDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime PaymentDate;

}
