package com.chicharronesweb.pedidosapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chicharronesweb.pedidosapi.entity.Notificacion;
import com.chicharronesweb.pedidosapi.service.NotificacionService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesPorUsuario(
            @PathVariable Integer usuarioId) {
        List<Notificacion> notificaciones
                = notificacionService.obtenerNotificacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Integer id) {
        try {
            Notificacion notificacion = notificacionService.marcarComoLeida(id);
            return ResponseEntity.ok(notificacion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Notificacion> crearNotificacion(
            @RequestBody Notificacion notificacion) {
        Notificacion nuevaNotificacion
                = notificacionService.crearNotificacion(notificacion);
        return ResponseEntity.status(HttpStatus.CREATED) // ✅ Usa 201 en lugar de 200
                .body(nuevaNotificacion);
    }

    @GetMapping("/{usuarioId}/sin-leer/count")
    public ResponseEntity<Long> contarNotificacionesSinLeer(@PathVariable Integer usuarioId) {
        Long count = notificacionService.contarNotificacionesSinLeer(usuarioId);
        return ResponseEntity.ok(count);
    }

}
