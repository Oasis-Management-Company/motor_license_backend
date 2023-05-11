package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QSales;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;


@Getter
@Setter
@NoArgsConstructor
public class SalesReportSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QSales> {
    private String createdAfter;
    private String createdBefore;
    private String downloadType;
    private Long type;


    @Override
    public void customize(QuerydslBindings bindings, QSales root){
        bindings.bind(root.createdBy.displayName).as("name").first((path, value) -> path.equalsIgnoreCase(value));
        bindings.bind(root.createdBy.office.id).as("zone").first((path, value) -> path.eq(value));
        bindings.bind(root.vehicle.plateNumber.plateNumber).as("plateNumber").first((path, value) -> path.equalsIgnoreCase(value));

        bindings.including(root.createdBy, root.vehicle.plateNumber);
    }
}
