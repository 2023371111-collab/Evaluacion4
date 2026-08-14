package com.example.microserviciob.microserviciob.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.microserviciob.microserviciob.dto.EntityADto;
import java.util.List;

@FeignClient(name = "ms-damian")
public interface ClientFeignMSA {

    @PostMapping("/api/entity-a/by-ids")
    public List<EntityADto> obtenerDTOsDelMSA(@RequestBody List<Integer> ids);
}
