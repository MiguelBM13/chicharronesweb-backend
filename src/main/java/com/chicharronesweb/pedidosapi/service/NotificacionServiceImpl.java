package com.chicharronesweb.pedidosapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chicharronesweb.pedidosapi.entity.Notificacion;
import com.chicharronesweb.pedidosapi.repository.NotificacionRepository;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Override
    public List<Notificacion> obtenerNotificacionesPorUsuario(Integer usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Override
    public Notificacion marcarComoLeida(Integer id) {
        return notificacionRepository.findById(id)
                .map(notificacion -> {
                    notificacion.setLeida(true);
                    return notificacionRepository.save(notificacion);
                })
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    @Override
    public Notificacion crearNotificacion(Notificacion notificacion) {
        try {
            System.out.println("🔍 Intentando crear notificación:");
            System.out.println("   Usuario ID: " + notificacion.getUsuarioId());
            System.out.println("   Tipo: " + notificacion.getTipo());
            System.out.println("   Pedido ID: " + notificacion.getPedidoId());

            Notificacion guardada = notificacionRepository.save(notificacion);
            System.out.println("✅ Notificación creada con ID: " + guardada.getId());
            return guardada;

        } catch (Exception e) {
            System.err.println("❌ ERROR al crear notificación:");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Notificacion crearNotificacionCambioEstado(Integer pedidoId, Integer usuarioId, String nuevoEstado) {
        String mensaje = "Su pedido #" + pedidoId + " está: " + nuevoEstado;

        // 🔔 BUSCAR SI YA EXISTE UNA NOTIFICACIÓN PARA ESTE PEDIDO
        return notificacionRepository
                .findByPedidoIdAndUsuarioIdAndTipo(pedidoId, usuarioId, "CAMBIO_ESTADO_PEDIDO")
                .map(notificacionExistente -> {
                    // ✅ ACTUALIZAR LA NOTIFICACIÓN EXISTENTE
                    notificacionExistente.setMensaje(mensaje);
                    notificacionExistente.setLeida(false); // Marcar como no leída
                    notificacionExistente.setFechaCreacion(LocalDateTime.now()); // Actualizar fecha
                    return notificacionRepository.save(notificacionExistente);
                })
                .orElseGet(() -> {
                    // ✅ CREAR NUEVA SI NO EXISTE
                    Notificacion nuevaNotificacion = new Notificacion(
                            usuarioId,
                            "CAMBIO_ESTADO_PEDIDO",
                            mensaje,
                            pedidoId
                    );
                    return notificacionRepository.save(nuevaNotificacion);
                });
    }

    @Override
    public Long contarNotificacionesSinLeer(Integer usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

}
