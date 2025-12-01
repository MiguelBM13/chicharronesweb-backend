package com.chicharronesweb.pedidosapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chicharronesweb.pedidosapi.entity.Categoria;
import com.chicharronesweb.pedidosapi.repository.CategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findById(Integer id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        try {
            categoriaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("La categoría tiene productos asociados");
        }
    }
}
