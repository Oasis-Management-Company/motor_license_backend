package com.app.IVAS.dto.filters;


import com.app.IVAS.entity.userManagement.QPortalUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Getter
@Setter
@NoArgsConstructor
public class PortalUserSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QPortalUser> {
    private String createdAfter;
    private String createdBefore;
    private String downloadType;

    @Override
    public void customize(QuerydslBindings bindings, QPortalUser root) {
        bindings.bind(root.status).as("status").first((path, value) -> path.eq(value));
        bindings.bind(root.email).as("email").first((path, value) -> path.eq(value));
        bindings.bind(root.firstName).as("firstName").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.lastName).as("lastName").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.displayName).as("name").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.role.name).as("role").first((path, value) -> path.equalsIgnoreCase(value));
        bindings.bind(root.office.zone.id).as("zone").first((path, value) -> path.eq(value));
        bindings.including(root.status, root.email, root.firstName, root.lastName, root.role.name);
    }
}
