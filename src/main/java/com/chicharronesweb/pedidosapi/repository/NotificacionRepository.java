package com.chicharronesweb.pedidosapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chicharronesweb.pedidosapi.entity.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Integer usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidaOrderByFechaCreacionDesc(Integer usuarioId, Boolean leida);

    Long countByUsuarioIdAndLeidaFalse(Integer usuarioId);

    Optional<Notificacion> findByPedidoIdAndUsuarioIdAndTipo(
            Integer pedidoId,
            Integer usuarioId,
            String tipo
    );
}
