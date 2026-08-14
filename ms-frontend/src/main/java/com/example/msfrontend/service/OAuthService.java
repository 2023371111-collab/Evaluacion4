package com.example.msfrontend.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servicio que encapsula el flujo OAuth2 (client_credentials) y las
 * llamadas al API Gateway.
 *
 * Cada paso importante se imprime en CONSOLA con un prefijo visual
 * para que sea claro durante la demo.
 */
@Service
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    // ──── Configuración OAuth2 ────────────────────────────────────────────────
    private static final String GRANT_TYPE       = "client_credentials";
    private static final String SCOPE            = "read write";

    @Value("${app.auth.token-url}")
    private String authServerUrl;

    @Value("${app.auth.client-id}")
    private String clientId;

    @Value("${app.auth.client-secret}")
    private String clientSecret;

    @Value("${app.gateway.base-url}")
    private String gatewayBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper  = new ObjectMapper();

    // ─────────────────────────────────────────────────────────────────────────
    // OBTENER TOKEN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Solicita un access_token al Authorization Server usando el flujo
     * OAuth2 client_credentials.
     *
     * @return el access_token JWT como String
     */
    public String obtenerToken() {
        separator("INICIO FLUJO OAUTH2 — client_credentials");

        // Paso 1: Preparar credenciales en Basic Auth (Base64)
        String credenciales = clientId + ":" + clientSecret;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credenciales.getBytes());

        log.info("┌─────────────────────────────────────────────────────────");
        log.info("│ [AUTH] PASO 1 — Preparando solicitud de token");
        log.info("│ [AUTH] URL del Authorization Server : {}", authServerUrl);
        log.info("│ [AUTH] client_id                   : {}", clientId);
        log.info("│ [AUTH] client_secret               : ********");
        log.info("│ [AUTH] grant_type                  : {}", GRANT_TYPE);
        log.info("│ [AUTH] scope                       : {}", SCOPE);
        log.info("│ [AUTH] Authorization Header (Basic Auth → Base64):");
        log.info("│         credenciales del cliente codificadas con Basic Auth");
        log.info("└─────────────────────────────────────────────────────────");

        // Paso 2: Construir headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", basicAuth);

        // Paso 3: Construir body del formulario
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", GRANT_TYPE);
        body.add("scope", SCOPE);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // Paso 4: Enviar POST al Authorization Server
        log.info("┌─────────────────────────────────────────────────────────");
        log.info("│ [AUTH] PASO 2 — Enviando POST a: {}", authServerUrl);
        log.info("│ [AUTH] Body: grant_type={}&scope={}", GRANT_TYPE, SCOPE);
        log.info("└─────────────────────────────────────────────────────────");

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    authServerUrl, HttpMethod.POST, request, String.class);

            // Paso 5: Parsear la respuesta
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();
            String tokenType   = jsonNode.get("token_type").asText();
            long expiresIn     = jsonNode.get("expires_in").asLong();

            log.info("┌─────────────────────────────────────────────────────────");
            log.info("│ [AUTH] PASO 3 — ✅ TOKEN RECIBIDO EXITOSAMENTE");
            log.info("│ [AUTH] token_type : {}", tokenType);
            log.info("│ [AUTH] expires_in : {} segundos", expiresIn);
            log.info("│ [AUTH] access_token (primeros 80 chars):");
            log.info("│         {}...", accessToken.substring(0, Math.min(80, accessToken.length())));
            log.info("│ [AUTH] El token completo no se imprime para evitar filtrarlo en logs.");
            log.info("└─────────────────────────────────────────────────────────");

            return accessToken;

        } catch (Exception e) {
            log.error("│ [AUTH] ❌ ERROR al obtener token: {}", e.getMessage());
            throw new RuntimeException("Error obteniendo token OAuth2: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LLAMADAS AL GATEWAY
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Llama al API Gateway para obtener todos los registros de Entity-A (ms-damian).
     * Incluye el JWT en el header Authorization.
     */
    public String obtenerEntityA() {
        String token = obtenerToken();
        String url   = gatewayBaseUrl + "/api/entity-a";

        log.info("┌─────────────────────────────────────────────────────────");
        log.info("│ [GATEWAY] PASO 4 — Llamando al API Gateway");
        log.info("│ [GATEWAY] Método : GET");
        log.info("│ [GATEWAY] URL    : {}", url);
        log.info("│ [GATEWAY] Header : Authorization: Bearer {}...", token.substring(0, 40));
        log.info("└─────────────────────────────────────────────────────────");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            log.info("┌─────────────────────────────────────────────────────────");
            log.info("│ [GATEWAY] PASO 5 — ✅ RESPUESTA DEL MICROSERVICIO ms-damian");
            log.info("│ [GATEWAY] HTTP Status : {}", response.getStatusCode());
            log.info("│ [GATEWAY] Body        : {}", response.getBody());
            log.info("└─────────────────────────────────────────────────────────");

            return response.getBody();

        } catch (Exception e) {
            log.error("│ [GATEWAY] ❌ ERROR: {}", e.getMessage());
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Llama al API Gateway para obtener todos los registros de Entity-B (microserviciob).
     * Incluye el JWT en el header Authorization.
     */
    public String obtenerEntityB() {
        String token = obtenerToken();
        String url   = gatewayBaseUrl + "/api/entity-b";

        log.info("┌─────────────────────────────────────────────────────────");
        log.info("│ [GATEWAY] PASO 4 — Llamando al API Gateway");
        log.info("│ [GATEWAY] Método : GET");
        log.info("│ [GATEWAY] URL    : {}", url);
        log.info("│ [GATEWAY] Header : Authorization: Bearer {}...", token.substring(0, 40));
        log.info("└─────────────────────────────────────────────────────────");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            log.info("┌─────────────────────────────────────────────────────────");
            log.info("│ [GATEWAY] PASO 5 — ✅ RESPUESTA DEL MICROSERVICIO microserviciob");
            log.info("│ [GATEWAY] HTTP Status : {}", response.getStatusCode());
            log.info("│ [GATEWAY] Body        : {}", response.getBody());
            log.info("└─────────────────────────────────────────────────────────");

            return response.getBody();

        } catch (Exception e) {
            log.error("│ [GATEWAY] ❌ ERROR: {}", e.getMessage());
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Demuestra el rechazo 401: llama al Gateway SIN token.
     */
    public Map<String, String> demostrarRechazo401() {
        String url = gatewayBaseUrl + "/api/entity-a";

        log.info("┌─────────────────────────────────────────────────────────");
        log.info("│ [GATEWAY] DEMO 401 — Llamando al Gateway SIN token");
        log.info("│ [GATEWAY] URL    : {}", url);
        log.info("│ [GATEWAY] Header : (sin Authorization)");
        log.info("└─────────────────────────────────────────────────────────");

        try {
            HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());
            restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            return Map.of("resultado", "❌ Debería haber dado 401");
        } catch (Exception e) {
            log.info("┌─────────────────────────────────────────────────────────");
            log.info("│ [GATEWAY] ✅ CORRECTO — Gateway rechazó la petición");
            log.info("│ [GATEWAY] Error : {}", e.getMessage());
            log.info("│ [GATEWAY] Sin token válido = 401 Unauthorized");
            log.info("└─────────────────────────────────────────────────────────");
            return Map.of("resultado", "✅ 401 Unauthorized — El gateway rechazó la petición sin token");
        }
    }

    private void separator(String mensaje) {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("   {}", mensaje);
        log.info("═══════════════════════════════════════════════════════════");
    }
}
