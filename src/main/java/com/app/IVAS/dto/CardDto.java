package com.app.IVAS.dto;

import com.app.IVAS.Enum.CardStatusConstant;
import com.app.IVAS.Enum.CardTypeConstant;
import lombok.Data;

@Data
public class CardDto {
    private Long id;
    private String owner;
    private CardTypeConstant type;
    private CardStatusConstant statusConstant;
    private String dateActivated;
    private String zonalOffice;
    private String expiryDate;
    private String createdBy;
    private String plateNumber;
}
