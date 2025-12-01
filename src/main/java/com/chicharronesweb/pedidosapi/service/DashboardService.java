package com.chicharronesweb.pedidosapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chicharronesweb.pedidosapi.dto.DashboardResponseDTO;
import com.chicharronesweb.pedidosapi.dto.EstadisticasGeneralesDTO;
import com.chicharronesweb.pedidosapi.dto.PedidoEstadoDTO;
import com.chicharronesweb.pedidosapi.dto.ProductoMasVendidoDTO;
import com.chicharronesweb.pedidosapi.dto.VentaCategoriaDTO;
import com.chicharronesweb.pedidosapi.dto.VentaDiaDTO;
import com.chicharronesweb.pedidosapi.entity.DetallePedido;
import com.chicharronesweb.pedidosapi.entity.Pedido;
import com.chicharronesweb.pedidosapi.entity.Usuario.Rol;
import com.chicharronesweb.pedidosapi.repository.DetallePedidoRepository;
import com.chicharronesweb.pedidosapi.repository.PedidoRepository;
import com.chicharronesweb.pedidosapi.repository.ProductoRepository;
import com.chicharronesweb.pedidosapi.repository.UsuarioRepository;

@Service
public class DashboardService {

    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private DetallePedidoRepository detalleRepo;

    public DashboardResponseDTO obtenerDatosDashboard() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.minusDays(6);

        List<Pedido> todos = pedidoRepo.findAll();

        List<Pedido> pedidosUlt7 = todos.stream()
                .filter(p -> {
                    LocalDate d = p.getFechaHora().toLocalDate();
                    return !d.isBefore(inicio) && !d.isAfter(hoy);
                })
                .toList();

        double ventasTotales = todos.stream()
                .filter(p -> p.getEstado() != Pedido.EstadoPedido.CANCELADO)
                .map(Pedido::getTotal)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        long totalPedidos = todos.size();
        long pedidosHoy = todos.stream()
                .filter(p -> p.getFechaHora().toLocalDate().isEqual(hoy))
                .count();
        long pedidosNoCancelados = todos.stream()
                .filter(p -> p.getEstado() != Pedido.EstadoPedido.CANCELADO)
                .count();
        double promedio = pedidosNoCancelados > 0 ? ventasTotales / pedidosNoCancelados : 0.0;

        int totalProductos = (int) productoRepo.count();
        int totalClientes = (int) usuarioRepo.findAll().stream()
                .filter(u -> u.getRol() == Rol.CLIENTE)
                .count();

        EstadisticasGeneralesDTO eg = new EstadisticasGeneralesDTO(
                round2(ventasTotales),
                (int) totalPedidos,
                (int) pedidosHoy,
                totalProductos,
                totalClientes,
                round2(promedio)
        );

        List<VentaDiaDTO> ventasPorDia = buildVentasPorDia(inicio, hoy, pedidosUlt7);
        List<PedidoEstadoDTO> pedidosPorEstado = buildPedidosPorEstado(todos);
        List<ProductoMasVendidoDTO> productosMasVendidos = buildTopProductos();
        List<VentaCategoriaDTO> ventasPorCategoria = buildVentasPorCategoria();

        return new DashboardResponseDTO(
                eg, ventasPorDia, pedidosPorEstado, productosMasVendidos, ventasPorCategoria
        );
    }

    private List<VentaDiaDTO> buildVentasPorDia(LocalDate inicio, LocalDate fin, List<Pedido> pedidos) {
        Map<LocalDate, List<Pedido>> porDia = pedidos.stream()
                .collect(Collectors.groupingBy(p -> p.getFechaHora().toLocalDate()));

        List<VentaDiaDTO> out = new ArrayList<>();
        for (LocalDate d = inicio; !d.isAfter(fin); d = d.plusDays(1)) {
            List<Pedido> delDia = porDia.getOrDefault(d, Collections.emptyList());
            double total = delDia.stream()
                    .filter(p -> p.getEstado() != Pedido.EstadoPedido.CANCELADO)
                    .map(Pedido::getTotal)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum();
            int cantidad = delDia.size();
            out.add(new VentaDiaDTO(d, round2(total), cantidad));
        }
        return out;
    }

    private List<PedidoEstadoDTO> buildPedidosPorEstado(List<Pedido> pedidos) {
        Map<Pedido.EstadoPedido, Long> cont = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getEstado, Collectors.counting()));

        Map<Pedido.EstadoPedido, String> colores = Map.of(
                Pedido.EstadoPedido.PENDIENTE, "#FFC107",
                Pedido.EstadoPedido.EN_PREPARACION, "#03A9F4",
                Pedido.EstadoPedido.LISTO, "#9C27B0",
                Pedido.EstadoPedido.ENTREGADO, "#4CAF50",
                Pedido.EstadoPedido.CANCELADO, "#F44336"
        );

        return cont.entrySet().stream()
                .map(e -> new PedidoEstadoDTO(
                e.getKey().name(),
                e.getValue().intValue(),
                colores.getOrDefault(e.getKey(), "#999999")
        ))
                .sorted(Comparator.comparing(PedidoEstadoDTO::getEstado))
                .collect(Collectors.toList());
    }

    private List<ProductoMasVendidoDTO> buildTopProductos() {
        return detalleRepo.findAll().stream()
                // Filtrar detalles válidos
                .filter(d -> d.getPedido() != null)
                .filter(d -> d.getPedido().getEstado() != Pedido.EstadoPedido.CANCELADO)
                .filter(d -> d.getProducto() != null)
                .filter(d -> d.getProducto().getNombre() != null)
                // Agrupar por nombre de producto y sumar cantidades
                .collect(Collectors.groupingBy(
                        d -> d.getProducto().getNombre(),
                        Collectors.summingInt(DetallePedido::getCantidad)
                ))
                .entrySet().stream()
                // Ordenar, limitar y mapear a DTO
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(e -> new ProductoMasVendidoDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<VentaCategoriaDTO> buildVentasPorCategoria() {
        List<DetallePedido> detalles = detalleRepo.findAll();
        Map<String, CategoriaAgg> agg = new HashMap<>();

        for (DetallePedido d : detalles) {
            Pedido p = d.getPedido();
            if (p == null || p.getEstado() == Pedido.EstadoPedido.CANCELADO) {
                continue;
            }

            String categoria = "Sin categoría";
            if (d.getProducto() != null && d.getProducto().getCategoria() != null) {
                categoria = d.getProducto().getCategoria().getNombre();
            }

            int cant = d.getCantidad();
            BigDecimal precio = d.getPrecioUnitario();
            if (precio == null && d.getProducto() != null) {
                precio = d.getProducto().getPrecio();
            }
            double subtotal = (precio != null ? precio.doubleValue() : 0.0) * cant;

            agg.computeIfAbsent(categoria, k -> new CategoriaAgg()).add(cant, subtotal);
        }

        return agg.entrySet().stream()
                .map(e -> new VentaCategoriaDTO(
                e.getKey(), // String nombreCategoria ✅
                round2(e.getValue().totalVentas), // Double totalVentas ✅
                e.getValue().cantidad // Integer cantidad ✅
        ))
                .sorted(Comparator.comparing(VentaCategoriaDTO::getTotalVentas).reversed())
                .collect(Collectors.toList());
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static class CategoriaAgg {

        int cantidad = 0;
        double totalVentas = 0.0;

        void add(int c, double t) {
            this.cantidad += c;
            this.totalVentas += t;
        }
    }
}
