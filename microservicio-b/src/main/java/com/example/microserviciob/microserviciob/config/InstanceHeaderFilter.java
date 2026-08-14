package com.example.microserviciob.microserviciob.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class InstanceHeaderFilter extends OncePerRequestFilter {

    private final String instanceId = System.getenv().getOrDefault("HOSTNAME", "microservicio-b-local");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("X-Service-Instance", instanceId);
        filterChain.doFilter(request, response);
    }
}
