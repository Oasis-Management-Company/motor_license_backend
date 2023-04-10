package com.app.IVAS.filter;

import com.app.IVAS.dto.filters.BaseSearchDto;
import com.app.IVAS.entity.QInvoice;
import com.app.IVAS.entity.QSales;
import lombok.Data;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Data
public class InvoiceSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QInvoice> {

    private String after;
    private String before;

    @Override
    public void customize(QuerydslBindings bindings, QInvoice root) {

    }
}
