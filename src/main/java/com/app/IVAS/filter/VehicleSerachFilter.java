package com.app.IVAS.filter;

import com.app.IVAS.dto.filters.BaseSearchDto;
import com.app.IVAS.entity.QVehicle;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Data
@NoArgsConstructor
public class VehicleSerachFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QVehicle> {

    private String after;
    private String before;
    @Override
    public void customize(QuerydslBindings bindings, QVehicle root) {

        bindings.bind(root.plateNumber.plateNumber).as("plateNumber").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.portalUser.asin).as("asin").first((path, value) -> path.containsIgnoreCase(value));

        bindings.including(root.plateNumber, root.portalUser);

    }

}
