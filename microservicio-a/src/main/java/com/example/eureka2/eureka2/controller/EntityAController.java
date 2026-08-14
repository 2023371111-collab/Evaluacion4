package com.example.eureka2.eureka2.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.eureka2.eureka2.dto.EntityADto;
import com.example.eureka2.eureka2.service.EntityAService;

@RestController
@RequestMapping("/api/entity-a")
public class EntityAController {

    @Autowired
    private EntityAService service;

    @PostMapping
    public ResponseEntity<EntityADto> create(@RequestBody EntityADto dto) {
        EntityADto created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/entity-a/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<EntityADto>> getAll() {
        List<EntityADto> list = service.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/by-ids")
    public ResponseEntity<List<EntityADto>> getAllByIds(@RequestBody List<Integer> ids) {
        List<EntityADto> list = service.findAllByIDs(ids);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityADto> getById(@PathVariable int id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityADto> update(@PathVariable int id, @RequestBody EntityADto dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean deleted = service.delete(id);
        if (deleted)
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

}