package com.chicharronesweb.pedidosapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionPedidoDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    @Positive(message = "El ID del pedido debe ser positivo")
    private Integer pedidoId;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer puntuacion;  // ✅ Cambiar a Integer

    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String comentario;
}
