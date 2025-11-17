package com.chicharronesweb.pedidosapi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chicharronesweb.pedidosapi.dto.ProductoMasVendidoDTO;
import com.chicharronesweb.pedidosapi.entity.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    @Query("SELECT p FROM Pedido p ORDER BY CASE WHEN p.estado = 'ENTREGADO' THEN 1 ELSE 0 END, p.fechaHora ASC")
    List<Pedido> findAllOrderByFechaAndEntregadoLast();

    List<Pedido> findByUsuarioIdOrderByFechaHoraDesc(Integer usuarioId);

    // 🔹 Nuevo método para el reporte
    @Query("""
                SELECT new com.chicharronesweb.pedidosapi.dto.ProductoMasVendidoDTO(
                    d.producto.nombre,
                    SUM(d.cantidad),
                    SUM(d.cantidad * d.precioUnitario)
                )
                FROM Pedido p
                JOIN p.detalles d
                WHERE FUNCTION('MONTH', p.fechaHora) = :mes
                GROUP BY d.producto.nombre
                ORDER BY SUM(d.cantidad) DESC
            """)
    List<ProductoMasVendidoDTO> obtenerProductosMasVendidosPorMes(@Param("mes") int mes);

}