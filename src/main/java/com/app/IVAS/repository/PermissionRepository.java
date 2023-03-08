package com.app.IVAS.repository;


import com.app.IVAS.entity.userManagement.Permission;
import com.app.IVAS.entity.userManagement.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findAllByRole(Role role);
}
