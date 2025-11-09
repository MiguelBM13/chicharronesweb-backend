package com.chicharronesweb.pedidosapi.controller;

import com.chicharronesweb.pedidosapi.entity.Usuario;
import com.chicharronesweb.pedidosapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    /**
     * Endpoint para registrar un nuevo usuario (cliente).
     * @param nuevoUsuario Datos del usuario a registrar (nombre, email, password).
     * @return El usuario creado o un error si el email ya existe.
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        // 1. Verificar si el email ya está en uso
        if (usuarioRepository.findByEmail(nuevoUsuario.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo electrónico ya está registrado.");
        }
        // 2. Establecer el rol por defecto como CLIENTE
        nuevoUsuario.setRol(Usuario.Rol.CLIENTE);
        // ✅ CORREGIDO - Codificar la contraseña antes de guardarla
        nuevoUsuario.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));
        // 3. Guardar el nuevo usuario en la base de datos
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        // 4. Devolver una respuesta exitosa (sin la contraseña)
        usuarioGuardado.setPassword(null); // No devolver la contraseña
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
    }

    /**
     * Endpoint para manejar el inicio de sesión de los usuarios.
     * @param loginRequest Un objeto Usuario que contiene el email y la contraseña.
     * @return Los datos del usuario (sin contraseña) si el login es exitoso, o un error en caso contrario.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuarioOptional.isPresent()) {
            Usuario usuarioEncontrado = usuarioOptional.get();
            
            if (passwordEncoder.matches(loginRequest.getPassword(), usuarioEncontrado.getPassword())) {
                
                Usuario respuestaUsuario = new Usuario();
                respuestaUsuario.setId(usuarioEncontrado.getId());
                respuestaUsuario.setNombre(usuarioEncontrado.getNombre());
                respuestaUsuario.setEmail(usuarioEncontrado.getEmail());
                respuestaUsuario.setRol(usuarioEncontrado.getRol());
                
                return ResponseEntity.ok(respuestaUsuario);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
}