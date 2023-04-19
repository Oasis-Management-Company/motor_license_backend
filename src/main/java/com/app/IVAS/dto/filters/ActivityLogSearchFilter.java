package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QActivityLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Getter
@Setter
@NoArgsConstructor
public class ActivityLogSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QActivityLog> {
    private String createdAfter;
    private String createdBefore;

    @Override
    public void customize(QuerydslBindings bindings, QActivityLog root){
        bindings.bind(root.createdBy.displayName).as("name").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.action).as("action").first((path, value) -> path.eq(value));
        bindings.including(root.createdBy, root.action);
    }
}
