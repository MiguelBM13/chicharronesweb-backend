package com.chicharronesweb.pedidosapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference; // <-- IMPORTAR
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    @JsonManagedReference
    private CalificacionPedido calificacion;

    private LocalDateTime fechaHora;
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetallePedido> detalles;

    public enum EstadoPedido {
        PENDIENTE,
        EN_PREPARACION,
        LISTO,
        ENTREGADO,
        CANCELADO
    }


    
}