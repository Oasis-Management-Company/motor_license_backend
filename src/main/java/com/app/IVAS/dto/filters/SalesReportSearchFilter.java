package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.entity.QSales;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;


@Getter
@Setter
@NoArgsConstructor
public class SalesReportSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QInvoiceServiceType> {
    private String createdAfter;
    private String createdBefore;
    private String downloadType;
    private String plateNumber;
    private Long type;
    private Long zone;


    @Override
    public void customize(QuerydslBindings bindings, QInvoiceServiceType root){
        bindings.bind(root.invoice.createdBy.displayName).as("name").first((path, value) -> path.equalsIgnoreCase(value));

        bindings.including(root.invoice.createdBy);
    }
}
