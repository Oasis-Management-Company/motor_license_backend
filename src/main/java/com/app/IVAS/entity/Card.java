package com.app.IVAS.entity;

import com.app.IVAS.Enum.CardStatusConstant;
import com.app.IVAS.Enum.CardTypeConstant;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.entity.userManagement.StatusEntity;
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
@Table(name = "CARD")
public class Card extends StatusEntity {

    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    private CardStatusConstant cardStatus;

    @Enumerated(EnumType.STRING)
    private CardTypeConstant cardType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private RegType regType = RegType.REGISTRATION;
}
