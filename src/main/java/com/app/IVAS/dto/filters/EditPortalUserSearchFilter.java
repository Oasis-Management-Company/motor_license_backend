package com.app.IVAS.dto.filters;

import com.app.IVAS.entity.QEditPortalUser;
import com.app.IVAS.entity.userManagement.QPortalUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Getter
@Setter
@NoArgsConstructor
public class EditPortalUserSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QEditPortalUser> {
    private String createdAfter;
    private String createdBefore;

    @Override
    public void customize(QuerydslBindings bindings, QEditPortalUser root) {
        bindings.bind(root.email).as("email").first((path, value) -> path.eq(value));
        bindings.bind(root.phoneNumber).as("phoneNumber").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.firstName).as("firstName").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.lastName).as("lastName").first((path, value) -> path.containsIgnoreCase(value));
        bindings.including(root.email, root.phoneNumber, root.firstName, root.lastName);
    }
}
