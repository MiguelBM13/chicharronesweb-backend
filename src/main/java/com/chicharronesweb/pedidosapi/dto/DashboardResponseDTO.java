package com.chicharronesweb.pedidosapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO{
        EstadisticasGeneralesDTO estadisticasGenerales;
        List<VentaDiaDTO> ventasPorDia;
        List<PedidoEstadoDTO> pedidosPorEstado;
        List<ProductoMasVendidoDTO> productosMasVendidos;
        List<VentaCategoriaDTO> ventasPorCategoria;
}





