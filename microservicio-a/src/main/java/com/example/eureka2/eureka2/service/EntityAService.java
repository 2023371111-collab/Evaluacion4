package com.example.eureka2.eureka2.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.eureka2.eureka2.dto.EntityADto;
import com.example.eureka2.eureka2.entity.EntityA;
import com.example.eureka2.eureka2.repository.EntityARepository;

@Service
public class EntityAService {

    @Autowired
    private EntityARepository repository;

    // Create
    @Transactional
    public EntityADto create(EntityADto dto) {
        EntityA entity = toEntity(dto);
        EntityA saved = repository.save(entity);
        return toDto(saved);
    }

    // Read all
    @Transactional(readOnly = true)
    public List<EntityADto> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntityADto> findAllByIDs(List<Integer> ids) {
        return repository.findAllById(ids)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Read by id
    @Transactional(readOnly = true)
    public Optional<EntityADto> findById(int id) {
        return repository.findById(id).map(this::toDto);
    }

    // Update
    @Transactional
    public Optional<EntityADto> update(int id, EntityADto dto) {
        return repository.findById(id).map(existing -> {
            existing.setNombreA(dto.getNombreA());
            EntityA saved = repository.save(existing);
            return toDto(saved);
        });
    }

    // Delete
    @Transactional
    public boolean delete(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // Converters
    private EntityADto toDto(EntityA e) {
        if (e == null)
            return null;
        EntityADto d = new EntityADto();
        d.setId(e.getId());
        d.setNombreA(e.getNombreA());
        return d;
    }

    private EntityA toEntity(EntityADto d) {
        if (d == null)
            return null;
        EntityA e = new EntityA();
        e.setId(d.getId());
        e.setNombreA(d.getNombreA());
        return e;
    }

}