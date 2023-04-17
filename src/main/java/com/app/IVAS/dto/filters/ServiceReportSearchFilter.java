package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QInvoiceServiceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Getter
@Setter
@NoArgsConstructor
public class ServiceReportSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QInvoiceServiceType> {
    private String createdAfter;
    private String createdBefore;
    private String downloadType;

    @Override
    public void customize(QuerydslBindings bindings, QInvoiceServiceType root){
        bindings.bind(root.invoice.createdBy.displayName).as("name").first((path, value) -> path.equalsIgnoreCase(value));
        bindings.bind(root.serviceType.name).as("serviceType").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.regType).as("regType").first((path, value) -> path.eq(value));
        if(root.invoice.createdBy.office != null){
            bindings.bind(root.invoice.createdBy.office.id).as("zone").first((path, value) -> path.eq(value));
        }

        bindings.including(root.invoice.createdBy);
    }
}
