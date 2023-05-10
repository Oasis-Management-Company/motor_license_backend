package com.app.IVAS.repository;

import com.app.IVAS.entity.Prefix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrefixRepository extends JpaRepository<Prefix, Long> {
    Prefix findByCode(String name);
}
