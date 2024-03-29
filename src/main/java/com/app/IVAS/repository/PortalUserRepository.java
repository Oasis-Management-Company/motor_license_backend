package com.app.IVAS.repository;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.entity.userManagement.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {
    Optional<PortalUser> findByUsernameIgnoreCaseAndStatus(String username, GenericStatusConstant status);

    Optional<PortalUser> findByIdAndStatus(Long id,GenericStatusConstant status );

    @Query("select p from PortalUser p where concat(p.nationalIdentificationNumber, p.username, p.phoneNumber) like %?1%")
    PortalUser findByUsernameIgnoreCase(String email);

    PortalUser findFirstByUsernameIgnoreCase(String email);

    PortalUser findByPhoneNumber(String number);

    PortalUser findFirstByPhoneNumber(String number);
}
