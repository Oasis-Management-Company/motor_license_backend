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
//        bindings.bind(root.).as("reviewedBy").first((path, value) -> path.eq(value));
//
//        bindings.including(root.assessor);
    }
}
