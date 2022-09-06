package org.example.gateway.feign;

import org.example.common.entity.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auth-service")
@RequestMapping("/auth")
public interface AuthService {
    @GetMapping("/token")
    ResponseData<String> checkToken(@RequestParam("token") String token);
}
