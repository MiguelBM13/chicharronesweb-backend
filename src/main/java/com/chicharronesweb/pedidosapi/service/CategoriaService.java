package com.chicharronesweb.pedidosapi.service;

import java.util.List;
import java.util.Optional;

import com.chicharronesweb.pedidosapi.entity.Categoria;

public interface CategoriaService {

    List<Categoria> findAll();

    Optional<Categoria> findById(Integer id);

    Categoria save(Categoria categoria);

    void deleteById(Integer id);
}
