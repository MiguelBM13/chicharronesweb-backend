package com.chicharronesweb.pedidosapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasGeneralesDTO {
        double ventasTotales;
        int totalPedidos;
        int pedidosHoy;
        int totalProductos;
        int totalClientes;
        double promedioVentaPorPedido;
}