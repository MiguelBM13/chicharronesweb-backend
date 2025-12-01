package com.chicharronesweb.pedidosapi.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDiaDTO {

    LocalDate fecha;
    double total;
    int cantidadPedidos;

}
