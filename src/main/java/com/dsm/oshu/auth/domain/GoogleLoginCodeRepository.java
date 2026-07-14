package com.dsm.oshu.auth.domain;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoogleLoginCodeRepository extends JpaRepository<GoogleLoginCode, String> {
    Optional<GoogleLoginCode> findByCode(String code);

    @Modifying
    @Query("update GoogleLoginCode code set code.used = true where code.code = :value and code.used = false and code.expiresAt > :now")
    int consume(@Param("value") String value, @Param("now") LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
