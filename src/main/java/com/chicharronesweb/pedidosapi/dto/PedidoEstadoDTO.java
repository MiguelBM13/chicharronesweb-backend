package com.chicharronesweb.pedidosapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEstadoDTO{
    String estado; int cantidad; String color;
}
