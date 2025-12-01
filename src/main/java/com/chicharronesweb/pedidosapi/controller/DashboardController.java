package com.chicharronesweb.pedidosapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chicharronesweb.pedidosapi.dto.DashboardResponseDTO;
import com.chicharronesweb.pedidosapi.service.DashboardService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obtenerDatosDashboard() {
        try {
            DashboardResponseDTO dashboard = dashboardService.obtenerDatosDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            // Log del error
            System.err.println("Error al obtener datos del dashboard: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
