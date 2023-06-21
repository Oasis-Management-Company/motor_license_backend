package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QVehicleModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Data
@NoArgsConstructor
public class VehicleModelSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QVehicleModel> {

    private String make;

    @Override
    public void customize(QuerydslBindings bindings, QVehicleModel root) {
        bindings.bind(root.name).as("model").first((path, value) -> path.containsIgnoreCase(value));
        bindings.including(root.vehicleMake.name, root.name);
    }
}
