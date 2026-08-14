package com.example.microserviciob.microserviciob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.microserviciob.microserviciob.entity.EntityB;

@Repository
public interface EntityBRepository extends JpaRepository<EntityB, Integer> {
}
