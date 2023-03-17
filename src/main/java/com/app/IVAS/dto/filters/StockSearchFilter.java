package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QStock;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Getter
@Setter
@NoArgsConstructor
public class StockSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QStock> {
    private String createdAfter;
    private String createdBefore;

    @Override
    public void customize(QuerydslBindings bindings, QStock root){
        bindings.bind(root.lga.id).as("lga").first((path, value) -> path.eq(value));
        bindings.including(root.lga);
    }
}
