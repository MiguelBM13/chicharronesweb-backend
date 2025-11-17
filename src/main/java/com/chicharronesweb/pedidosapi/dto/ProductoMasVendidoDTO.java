package com.chicharronesweb.pedidosapi.dto;

import java.math.BigDecimal;

public class ProductoMasVendidoDTO {
    private String nombreProducto;
    private Long cantidadVendida;
    private BigDecimal totalGenerado;

    public ProductoMasVendidoDTO(String nombreProducto, Long cantidadVendida, BigDecimal totalGenerado) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.totalGenerado = totalGenerado;
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
