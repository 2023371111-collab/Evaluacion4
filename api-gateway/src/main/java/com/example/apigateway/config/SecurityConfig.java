package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad del API Gateway como OAuth2 Resource Server.
 *
 * Flujo:
 *  1. El Frontend obtiene un JWT del Authorization Server (puerto 9000)
 *  2. El Frontend envía ese JWT en el header: Authorization: Bearer <token>
 *  3. Este filtro valida el JWT usando las claves públicas del Authorization Server
 *  4. Si el JWT es válido, la petición se enruta al microservicio correspondiente
 *  5. Si no hay JWT o es inválido, se devuelve 401 Unauthorized
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (no aplica para APIs REST / Bearer tokens)
            .csrf(csrf -> csrf.disable())

            // Todas las rutas requieren autenticación
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .anyRequest().authenticated()
            )

            // Configurar como OAuth2 Resource Server con validación JWT
            // El gateway descarga las claves públicas de http://localhost:9000/oauth2/jwks
            // y usa esas claves para verificar la firma de cada JWT recibido
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return http.build();
    }
}
