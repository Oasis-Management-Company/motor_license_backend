package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QVehicleModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Data
@NoArgsConstructor
public class VehicleModelSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QVehicleModel> {

    private String name;

    @Override
    public void customize(QuerydslBindings bindings, QVehicleModel root) {
        bindings.including(root.vehicleMake.name);
    }
}
