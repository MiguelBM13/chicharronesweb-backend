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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chicharronesweb.pedidosapi.dto.PedidoRequestDTO;
import com.chicharronesweb.pedidosapi.dto.ProductoMasVendidoDTO;
import com.chicharronesweb.pedidosapi.entity.Pedido;
import com.chicharronesweb.pedidosapi.repository.PedidoRepository;
import com.chicharronesweb.pedidosapi.service.NotificacionService;
import com.chicharronesweb.pedidosapi.service.PedidoService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private NotificacionService notificacionService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@RequestBody PedidoRequestDTO pedidoRequest) {
        Integer usuarioId = pedidoRequest.getUsuarioId();
        Pedido nuevoPedido = pedidoService.crearPedido(pedidoRequest, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodosLosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAllOrderByFechaAndEntregadoLast();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable Integer id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstadoPedido(
            @PathVariable Integer id,
            @RequestParam("estado") Pedido.EstadoPedido nuevoEstado) {

        return pedidoRepository.findById(id)
                .map(pedido -> {
                    pedido.setEstado(nuevoEstado);
                    Pedido pedidoActualizado = pedidoRepository.save(pedido);
                    // Crear notificación para el usuario
                    notificacionService.crearNotificacionCambioEstado(pedido.getId(), pedido.getUsuario().getId(), nuevoEstado.name());
                    return ResponseEntity.ok(pedidoActualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Pedido> obtenerPedidosPorUsuario(@PathVariable Integer usuarioId) {
        return pedidoService.obtenerPedidosPorUsuario(usuarioId);
    }

    @GetMapping("/reporte/mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> obtenerProductosMasVendidosPorMes(
            @RequestParam("mes") int mes) {
        List<ProductoMasVendidoDTO> reporte = pedidoRepository.obtenerProductosMasVendidosPorMes(mes);
        return ResponseEntity.ok(reporte);
    }
    
}
