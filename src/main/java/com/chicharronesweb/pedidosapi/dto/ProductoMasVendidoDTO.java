package com.chicharronesweb.pedidosapi.dto;

import java.math.BigDecimal;

public class ProductoMasVendidoDTO {

    private String nombreProducto;
    private Long cantidadVendida;
    private BigDecimal totalGenerado;

    // Constructor para la query JPQL del PedidoRepository (3 parámetros con BigDecimal)
    public ProductoMasVendidoDTO(String nombreProducto, Long cantidadVendida, BigDecimal totalGenerado) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.totalGenerado = totalGenerado != null ? totalGenerado : BigDecimal.ZERO;
    }

    // Constructor para el DashboardService.buildTopProductos() (2 parámetros)
    public ProductoMasVendidoDTO(String nombreProducto, Integer cantidadVendida) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida.longValue();
        this.totalGenerado = BigDecimal.ZERO;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public BigDecimal getTotalGenerado() {
        return totalGenerado;
    }

    public void setTotalGenerado(BigDecimal totalGenerado) {
        this.totalGenerado = totalGenerado;
    }
}
