package com.chicharronesweb.pedidosapi.controller;

import com.chicharronesweb.pedidosapi.dto.ResetPasswordRequest;
import com.chicharronesweb.pedidosapi.entity.PasswordResetToken;
import com.chicharronesweb.pedidosapi.entity.Usuario;
import com.chicharronesweb.pedidosapi.repository.PasswordResetTokenRepository;
import com.chicharronesweb.pedidosapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PasswordResetController {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    
    // POST /auth/recover
    @PostMapping("/recover")
    @Transactional
    public ResponseEntity<?> recoverPassword(@RequestParam String email) {
        
        // Verificar si el usuario existe
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        
        // Por seguridad, siempre devolvemos el mismo mensaje
        if (usuario != null) {
            // Eliminar tokens previos para este email
            tokenRepository.deleteByEmail(email);
            
            // Generar nuevo token
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, email);
            tokenRepository.save(resetToken);
            
            // Enviar email
            try {
                sendResetEmail(email, token);
                System.out.println("✅ Token generado para: " + email + " - Token: " + token);
            } catch (Exception e) {
                System.err.println("❌ Error enviando email: " + e.getMessage());
            }
        } else {
            System.out.println("❌ Email no encontrado: " + email);
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Si el email existe en nuestro sistema, recibirás un enlace de recuperación");
        return ResponseEntity.ok(response);
    }
    
    // POST /auth/reset - EXACTO como pidió el dueño
    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        
        // Validar nueva contraseña
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La contraseña no puede estar vacía");
        }
        
        if (request.getNewPassword().length() < 6) {
            return ResponseEntity.badRequest().body("La contraseña debe tener al menos 6 caracteres");
        }
        
        // Buscar y validar token
        PasswordResetToken token = tokenRepository.findByToken(request.getToken());
        
        if (token == null) {
            return ResponseEntity.badRequest().body("Token inválido o enlace expirado");
        }
        
        if (token.isUsed()) {
            return ResponseEntity.badRequest().body("Este enlace ya fue utilizado");
        }
        
        if (token.isExpired()) {
            return ResponseEntity.badRequest().body("El enlace ha expirado");
        }
        
        // Buscar usuario y actualizar contraseña
        Usuario usuario = usuarioRepository.findByEmail(token.getEmail()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }
        
        // Actualizar contraseña cifrada con BCrypt 
        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);
        
        // Marcar token como usado
        token.setUsed(true);
        tokenRepository.save(token);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contraseña actualizada exitosamente");
        return ResponseEntity.ok(response);
    }
    
    private void sendResetEmail(String email, String token) {
        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("lobochicharron@gmail.com"); 
            message.setTo(email);
            message.setSubject("Recuperación de Contraseña - ChicharronesWeb");
            message.setText(
                "Hola,\n\n" +
                "Has solicitado restablecer tu contraseña en ChicharronesWeb.\n\n" +
                "Para crear una nueva contraseña, haz clic en el siguiente enlace:\n" +
                resetLink + "\n\n" +
                "Este enlace expirará en 1 hora.\n\n" +
                "Si no solicitaste este cambio, puedes ignorar este mensaje.\n\n" +
                "Saludos,\nEquipo ChicharronesWeb"
            );
            
            mailSender.send(message);
            System.out.println("✅ Email enviado exitosamente a: " + email);
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando email a " + email + ": " + e.getMessage());
            // Fallback: mostrar enlace en consola
            System.out.println("🔗 ENLACE DE RECUPERACIÓN (FALLBACK): " + resetLink);
        }
    }
}