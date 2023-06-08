package com.app.IVAS.repository;

import com.app.IVAS.entity.EditPortalUser;
import com.app.IVAS.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EditPortalUserDao extends JpaRepository<EditPortalUser, Long> {

    Optional<EditPortalUser> findByParentId(Long parentId);

    List<EditPortalUser> findAllByParentId(Long id);
}
