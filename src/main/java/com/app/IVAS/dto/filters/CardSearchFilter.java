package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QCard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Getter
@Setter
@NoArgsConstructor
public class CardSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QCard> {
    private String createdAfter;
    private String createdBefore;
    private String plate;

    @Override
    public void customize(QuerydslBindings bindings, QCard root) {
        bindings.bind(root.cardStatus).as("status").first((path, value) -> path.eq(value));
        bindings.bind(root.cardType).as("type").first((path, value) -> path.eq(value));
        bindings.bind(root.createdBy.office.id).as("zonalId").first((path, value) -> path.eq(value));
        bindings.including(root.cardStatus, root.cardType, root.createdBy);
    }
}
