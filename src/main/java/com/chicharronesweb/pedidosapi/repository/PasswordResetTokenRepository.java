package com.chicharronesweb.pedidosapi.repository;

import com.chicharronesweb.pedidosapi.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByEmail(String email);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.email = ?1")
    void deleteByEmail(String email);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < ?1")
    void deleteAllExpiredSince(Date now);
}