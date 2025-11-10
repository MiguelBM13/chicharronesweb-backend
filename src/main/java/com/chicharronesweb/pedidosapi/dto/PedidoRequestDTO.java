// Paquete: com.chicharronesweb.pedidosapi.dto
package com.chicharronesweb.pedidosapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    private Integer usuarioId; // ✅ se agrega este campo
    private List<DetallePedidoRequestDTO> detalles;

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<DetallePedidoRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoRequestDTO> detalles) {
        this.detalles = detalles;
    }
}
