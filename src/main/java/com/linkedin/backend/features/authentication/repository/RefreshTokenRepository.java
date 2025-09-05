package com.linkedin.backend.features.authentication.repository;

import com.linkedin.backend.features.authentication.model.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByJti(String jti);

    Optional<RefreshToken> findByUserIdAndDeviceId(Long userId, String deviceId);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.jti = :jti")
    void deleteByJti(@Param("jti") String jti);
}
