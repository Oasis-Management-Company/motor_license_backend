package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QInvoiceOffenseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Getter
@Setter
@NoArgsConstructor
public class OffenseReportSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QInvoiceOffenseType> {
    private String createdAfter;
    private String createdBefore;
    private String downloadType;
    private String plateNumber;


    @Override
    public void customize(QuerydslBindings bindings, QInvoiceOffenseType root){
        bindings.bind(root.offense.name).as("offense").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.reference).as("invoiceID").first((path, value) -> path.equalsIgnoreCase(value));

        bindings.including(root.offense, root.reference, root.invoice.vehicle);
    }
}
