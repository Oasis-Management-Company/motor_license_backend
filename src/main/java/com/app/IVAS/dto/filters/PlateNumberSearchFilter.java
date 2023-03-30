package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QPlateNumber;
import com.app.IVAS.security.JwtService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Getter
@Setter
@NoArgsConstructor
public class PlateNumberSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QPlateNumber> {
    private String createdAfter;
    private String createdBefore;
    private String isAgent;

    @Override
    public void customize(QuerydslBindings bindings, QPlateNumber root){
        bindings.bind(root.plateNumberStatus).as("status").first((path, value) -> path.eq(value));
        bindings.bind(root.plateNumber).as("plateNumber").first((path, value) -> path.equalsIgnoreCase(value));
        bindings.bind(root.stock.id).as("id").first((path, value) -> path.eq(value));
        bindings.bind(root.plateNumber).as("lga").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.type.id).as("type").first((path, value) -> path.eq(value));
        bindings.bind(root.type.name).as("name").first((path, value) -> path.equalsIgnoreCase(value));
        bindings.bind(root.subType.name).as("subType").first((path, value) -> path.equalsIgnoreCase(value));
        bindings.including(root.plateNumberStatus, root.plateNumber, root.stock, root.agent, root.type, root.subType);
    }
}
