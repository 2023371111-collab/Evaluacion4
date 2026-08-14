package com.example.microserviciob.microserviciob.dto;

import lombok.Data;

@Data
public class EntityBEntityADto {
    private Integer id;
    private Integer entityAId;

    public EntityBEntityADto() {
    }

    public EntityBEntityADto(Integer id, Integer entityAid) {
        this.id = id;
        this.entityAId = entityAid;
    }

    @Override
    public String toString() {
        return "EntityBEntityADto [id=" + id + ", entityAId=" + entityAId + "]";
    }

}
