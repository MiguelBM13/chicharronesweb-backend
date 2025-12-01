package com.chicharronesweb.pedidosapi.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chicharronesweb.pedidosapi.dto.CalificacionPedidoDTO;
import com.chicharronesweb.pedidosapi.entity.CalificacionPedido;
import com.chicharronesweb.pedidosapi.service.CalificacionPedidoService;

@CrossOrigin(origins = "http://localhost:4200") // Ajusta según tu frontend
@RestController
@RequestMapping("/api/calificaciones")

public class CalificacionPedidoController {

    @Autowired
    private CalificacionPedidoService calificacionService;

    @PostMapping
    public ResponseEntity<?> crearOActualizarCalificacion(@RequestBody CalificacionPedidoDTO dto) {
        try {
            CalificacionPedido calificacion = calificacionService.registrarCalificacion(dto);
            return ResponseEntity.ok(calificacion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> obtenerCalificacionPorPedido(@PathVariable Integer pedidoId) {
        Optional<CalificacionPedido> calificacion = calificacionService.obtenerCalificacionPorPedido(pedidoId);

        if (calificacion.isPresent()) {
            return ResponseEntity.ok(calificacion.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró calificación para este pedido");
        }
    }
}