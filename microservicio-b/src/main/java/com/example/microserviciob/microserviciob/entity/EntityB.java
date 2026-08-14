package com.example.microserviciob.microserviciob.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.List;

import lombok.Data;

@Entity
@Table(name = "entity_b", schema = "msb")
@Data
public class EntityB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nombre_b", nullable = false)
    private String nombreB;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "entityb_id")
    private List<EntityBEntityA> listaConEntityA;

}
