package com.chicharronesweb.pedidosapi.service;

import java.util.List;

import com.chicharronesweb.pedidosapi.entity.Notificacion;

public interface NotificacionService {

    List<Notificacion> obtenerNotificacionesPorUsuario(Integer usuarioId);

    Notificacion marcarComoLeida(Integer id);

    Notificacion crearNotificacion(Notificacion notificacion);

    Notificacion crearNotificacionCambioEstado(Integer pedidoId, Integer usuarioId, String nuevoEstado);

    Long contarNotificacionesSinLeer(Integer usuarioId);

}
