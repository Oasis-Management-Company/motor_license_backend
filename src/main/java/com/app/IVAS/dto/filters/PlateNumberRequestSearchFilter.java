package com.app.IVAS.dto.filters;


import com.app.IVAS.entity.QPlateNumberRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;


@Getter
@Setter
@NoArgsConstructor
public class PlateNumberRequestSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QPlateNumberRequest> {
    private String createdAfter;
    private String createdBefore;

    @Override
    public void customize(QuerydslBindings bindings, QPlateNumberRequest root) {
        bindings.bind(root.trackingId).as("trackingId").first((path, value) -> path.eq(value));
        bindings.including(root.trackingId);
    }
}
