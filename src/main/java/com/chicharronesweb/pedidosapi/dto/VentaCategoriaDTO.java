package com.chicharronesweb.pedidosapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaCategoriaDTO {

    private String nombreCategoria;  // ✅ Private
    private Double totalVentas;      // ✅ Wrapper (permite null)
    private Integer cantidad;        // ✅ Wrapper (permite null)
}
