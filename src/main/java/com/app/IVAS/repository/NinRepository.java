package com.app.IVAS.repository;

import com.app.IVAS.entity.NIN;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NinRepository extends JpaRepository<NIN, Long> {
    NIN findByNin(String nin);

    NIN findByTelePhoneNumber(String phoneNumber);
}
