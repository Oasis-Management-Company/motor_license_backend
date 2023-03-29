package com.app.AIRS.dto;


import com.app.AIRS.Enum.CardType;
import lombok.Data;

@Data
public class PrintDto {
    private Long id;
    private CardType type;
}
