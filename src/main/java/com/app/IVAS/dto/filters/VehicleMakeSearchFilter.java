package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QVehicleMake;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public class VehicleMakeSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QVehicleMake> {
    @Override
    public void customize(QuerydslBindings bindings, QVehicleMake root) {

        bindings.bind(root.name).as("name").first((path, value) -> path.containsIgnoreCase(value));
        bindings.including(root.name);

    }
}
