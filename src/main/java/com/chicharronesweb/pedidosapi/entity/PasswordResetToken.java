package com.chicharronesweb.pedidosapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private Date expiryDate;
    
    private boolean used = false;
    
    // Constructor que establece expiración en 1 hora
    public PasswordResetToken() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 1); // 1 hora de expiración
        this.expiryDate = cal.getTime();
    }
    
    public PasswordResetToken(String token, String email) {
        this();
        this.token = token;
        this.email = email;
    }
    
    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}