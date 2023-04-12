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


    @Override
    public void customize(QuerydslBindings bindings, QSales root){

    }
}
