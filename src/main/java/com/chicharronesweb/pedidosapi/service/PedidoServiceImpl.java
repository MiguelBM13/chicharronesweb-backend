// Paquete: com.chicharronesweb.pedidosapi.service
package com.chicharronesweb.pedidosapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chicharronesweb.pedidosapi.dto.PedidoRequestDTO;
import com.chicharronesweb.pedidosapi.entity.DetallePedido;
import com.chicharronesweb.pedidosapi.entity.Notificacion;
import com.chicharronesweb.pedidosapi.entity.Pedido;
import com.chicharronesweb.pedidosapi.entity.Producto;
import com.chicharronesweb.pedidosapi.entity.Usuario;
import com.chicharronesweb.pedidosapi.repository.PedidoRepository;
import com.chicharronesweb.pedidosapi.repository.ProductoRepository;
import com.chicharronesweb.pedidosapi.repository.UsuarioRepository;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private NotificacionService notificacionService;  // 🔔 AGREGADO

    @Override
    public List<Pedido> obtenerPedidosPorUsuario(Integer usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaHoraDesc(usuarioId);
    }

    @Override
    @Transactional // Anotación clave: si algo falla, toda la operación se deshace (rollback).
    public Pedido crearPedido(PedidoRequestDTO pedidoRequest, Integer usuarioId) {
        // 1. Obtener el usuario que hace el pedido.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Crear la cabecera del pedido.
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaHora(LocalDateTime.now());
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);

        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal totalPedido = BigDecimal.ZERO;

        // 3. Procesar cada ítem del "carrito" (DTO).
        for (var detalleDto : pedidoRequest.getDetalles()) {
            // Buscar el producto en la BD para obtener su precio actual.
            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: ID " + detalleDto.getProductoId()));

            // Crear el detalle del pedido.
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio()); // Usamos el precio de la BD.
            detalle.setPedido(pedido); // Vinculamos el detalle con su cabecera.

            detalles.add(detalle);

            // 4. Calcular el subtotal y sumarlo al total general.
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalleDto.getCantidad()));
            totalPedido = totalPedido.add(subtotal);
        }

        // 5. Asignar la lista de detalles y el total al pedido.
        pedido.setDetalles(detalles);
        pedido.setTotal(totalPedido);

        // 6. Guardar el pedido. Gracias a CascadeType.ALL, los detalles se guardan automáticamente.
        Pedido pedidoGuardado = pedidoRepository.save(pedido);  // 🔔 MODIFICADO

        // 🔔 7. NOTIFICAR AL CLIENTE
        try {
            Notificacion notifCliente = new Notificacion(
                    usuarioId,
                    "PEDIDO CREADO",
                    "Tu pedido #" + pedidoGuardado.getId() + " fue registrado",
                    pedidoGuardado.getId()
            );
            notificacionService.crearNotificacion(notifCliente);
        } catch (Exception e) {
            // Si falla la notificación, solo lo registramos pero no detenemos el pedido
            System.err.println("Error al crear notificación para cliente: " + e.getMessage());
        }

        // 🔔 8. NOTIFICAR A LOS ADMINS
        try {
            List<Usuario> admins = usuarioRepository.findByRol(Usuario.Rol.ADMIN);
            for (Usuario admin : admins) {
                Notificacion notifAdmin = new Notificacion(
                        admin.getId(),
                        "NUEVO PEDIDO",
                        "Nuevo pedido #" + pedidoGuardado.getId() + " recibido",
                        pedidoGuardado.getId()
                );
                notificacionService.crearNotificacion(notifAdmin);
            }
        } catch (Exception e) {
            // Si falla la notificación, solo lo registramos pero no detenemos el pedido
            System.err.println("Error al crear notificaciones para admins: " + e.getMessage());
        }

        return pedidoGuardado;
    }

    @Override
    public void notificarAdminsCambioEstado(Pedido pedido, String estadoAnterior, String nuevoEstado) {
        try {
            List<Usuario> admins = usuarioRepository.findByRol(Usuario.Rol.ADMIN);
            for (Usuario admin : admins) {
                Notificacion notif = new Notificacion(
                        admin.getId(),
                        "ESTADO PEDIDO",
                        "Pedido #" + pedido.getId() + " esta  " + nuevoEstado,
                        pedido.getId()
                );
                notificacionService.crearNotificacion(notif);
            }
        } catch (Exception e) {
            System.err.println("Error al notificar admins sobre cambio de estado: " + e.getMessage());
        }
    }
}
