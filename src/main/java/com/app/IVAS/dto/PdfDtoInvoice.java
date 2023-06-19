package com.app.IVAS.dto;

import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import lombok.Data;

import java.util.Map;

@Data
public class PdfDtoInvoice {
    private String templateName;
    private InvoiceServiceType card;
    private Map<String, Object> extraParameter;
    private Invoice receipt;
}
