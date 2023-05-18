package com.app.IVAS.filter;

import com.app.IVAS.dto.filters.BaseSearchDto;
import com.app.IVAS.entity.QSales;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Data
@NoArgsConstructor
public class SalesSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QSales> {

    private String after;
    private String before;

    @Override
    public void customize(QuerydslBindings bindings, QSales root) {
        bindings.bind(root.vehicle.plateNumber.plateNumber).as("plateNumber").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.vehicle.portalUser.asin).as("asin").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.vehicle.portalUser.email).as("email").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.invoice.paymentStatus).as("paymentStatus").first((path, value) -> path.eq(value));
        bindings.bind(root.invoice.invoiceNumber).as("invoiceNo").first((path, value) -> path.containsIgnoreCase(value));
        bindings.including(root.vehicle.plateNumber, root.invoice.paymentStatus, root.invoice.invoiceNumber, root.vehicle.portalUser);
    }
}
