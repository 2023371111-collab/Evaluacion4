package com.example.eureka2.eureka2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.eureka2.eureka2.entity.EntityA;

@Repository
public interface EntityARepository extends JpaRepository<EntityA, Integer> {
}
