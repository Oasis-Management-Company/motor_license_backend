package com.app.IVAS.entity;

import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "SALES_TABLE")
public class Sales extends StatusEntity {

    private String reference_no = "IVS" + LocalDate.now().getYear()+ (int)(Math.random()* 12345607);

    @ManyToOne
    @JoinColumn(name = "VEHICLE_ID", referencedColumnName = "id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "INVOICE_ID", referencedColumnName = "id")
    private Invoice invoice;

}
