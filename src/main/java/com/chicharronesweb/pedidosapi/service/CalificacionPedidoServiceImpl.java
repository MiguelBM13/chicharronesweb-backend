package com.chicharronesweb.pedidosapi.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chicharronesweb.pedidosapi.dto.CalificacionPedidoDTO;
import com.chicharronesweb.pedidosapi.entity.CalificacionPedido;
import com.chicharronesweb.pedidosapi.entity.Pedido;
import com.chicharronesweb.pedidosapi.repository.CalificacionPedidoRepository;
import com.chicharronesweb.pedidosapi.repository.PedidoRepository;

@Service
public class CalificacionPedidoServiceImpl implements CalificacionPedidoService {

    @Autowired
    private CalificacionPedidoRepository calificacionRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    @Transactional
    public CalificacionPedido registrarCalificacion(CalificacionPedidoDTO dto) {

        // ✅ Validar puntuación (por si acaso)
        if (dto.getPuntuacion() == null || dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }

        // Buscar el pedido
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Validar que el pedido esté ENTREGADO (no LISTO)
        if (pedido.getEstado() != Pedido.EstadoPedido.ENTREGADO) {
            throw new RuntimeException("Solo se pueden calificar pedidos ENTREGADOS");
        }

        // Verificar si ya existe una calificación
        Optional<CalificacionPedido> existente = calificacionRepository.findByPedidoId(dto.getPedidoId());
        CalificacionPedido calificacion;

        if (existente.isPresent()) {
            // Actualizar calificación existente
            calificacion = existente.get();
            calificacion.setPuntuacion(dto.getPuntuacion());
            calificacion.setComentario(dto.getComentario());
            calificacion.setFechaRegistro(LocalDateTime.now());
        } else {
            // Crear nueva calificación
            calificacion = new CalificacionPedido();
            calificacion.setPuntuacion(dto.getPuntuacion());
            calificacion.setComentario(dto.getComentario());
            calificacion.setFechaRegistro(LocalDateTime.now());
            calificacion.setPedido(pedido);
        }

        return calificacionRepository.save(calificacion);
    }

    @Override
    public Optional<CalificacionPedido> obtenerCalificacionPorPedido(Integer pedidoId) {
        return calificacionRepository.findByPedidoId(pedidoId);
    }
}
