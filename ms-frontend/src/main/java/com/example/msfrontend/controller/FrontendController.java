package com.example.msfrontend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.msfrontend.service.OAuthService;

/**
 * Controlador principal del frontend.
 * Cada endpoint corresponde a un botón en la página web.
 * Al hacer click, el flujo OAuth2 se ejecuta y los pasos
 * se muestran en la consola del servidor Spring Boot.
 */
@Controller
public class FrontendController {

    @Autowired
    private OAuthService oAuthService;

    /**
     * GET / → Página principal con los botones de demo
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("titulo", "Demo OAuth2 — API Gateway");
        model.addAttribute("mensaje", "Haz click en un botón para ver el flujo OAuth2 en la consola del servidor.");
        return "index";
    }

    /**
     * POST /login → Obtiene un token del Authorization Server y lo muestra
     */
    @PostMapping("/login")
    public String login(Model model) {
        try {
            String token = oAuthService.obtenerToken();
            model.addAttribute("accion", "🔐 Login — Obtención de Token");
            model.addAttribute("resultado", token);
            model.addAttribute("descripcion",
                "El frontend se autenticó ante el Authorization Server mediante " +
                "client_credentials. " +
                "El servidor devolvió el JWT mostrado arriba. Revisa la consola del servidor para " +
                "ver cada paso detallado.");
            model.addAttribute("exito", true);
        } catch (Exception e) {
            model.addAttribute("accion", "🔐 Login — Error");
            model.addAttribute("resultado", e.getMessage());
            model.addAttribute("exito", false);
        }
        return "resultado";
    }

    /**
     * POST /ver-entity-a → Obtiene token y llama al Gateway para /api/entity-a (ms-Damian)
     */
    @PostMapping("/ver-entity-a")
    public String verEntityA(Model model) {
        try {
            String datos = oAuthService.obtenerEntityA();
            model.addAttribute("accion", "📋 Entity-A — ms-damian (via Gateway)");
            model.addAttribute("resultado", datos);
            model.addAttribute("descripcion",
                "El frontend obtuvo un token JWT del Authorization Server y luego llamó a " +
                "la ruta /api/entity-a con el header 'Authorization: Bearer <token>'. " +
                "El API Gateway validó el JWT y balanceó la petición entre las instancias de ms-damian. " +
                "Revisa la consola para ver el flujo completo.");
            model.addAttribute("exito", true);
        } catch (Exception e) {
            model.addAttribute("accion", "📋 Entity-A — Error");
            model.addAttribute("resultado", e.getMessage());
            model.addAttribute("exito", false);
        }
        return "resultado";
    }

    /**
     * POST /ver-entity-b → Obtiene token y llama al Gateway para /api/entity-b (microserviciob)
     */
    @PostMapping("/ver-entity-b")
    public String verEntityB(Model model) {
        try {
            String datos = oAuthService.obtenerEntityB();
            model.addAttribute("accion", "📋 Entity-B — microserviciob (via Gateway)");
            model.addAttribute("resultado", datos);
            model.addAttribute("descripcion",
                "El frontend obtuvo un token JWT del Authorization Server y luego llamó a " +
                "la ruta /api/entity-b con el header 'Authorization: Bearer <token>'. " +
                "El API Gateway validó el JWT y balanceó la petición entre las instancias de microserviciob. " +
                "Revisa la consola para ver el flujo completo.");
            model.addAttribute("exito", true);
        } catch (Exception e) {
            model.addAttribute("accion", "📋 Entity-B — Error");
            model.addAttribute("resultado", e.getMessage());
            model.addAttribute("exito", false);
        }
        return "resultado";
    }

    /**
     * POST /demo-401 → Llama al Gateway SIN token para demostrar el rechazo 401
     */
    @PostMapping("/demo-401")
    public String demo401(Model model) {
        Map<String, String> resultado = oAuthService.demostrarRechazo401();
        model.addAttribute("accion", "🚫 Demo 401 — Petición sin token");
        model.addAttribute("resultado", resultado.get("resultado"));
        model.addAttribute("descripcion",
            "Se envió una petición al API Gateway SIN el header Authorization. " +
            "El gateway devolvió 401 Unauthorized porque no hay JWT. " +
            "Revisa la consola para ver el error.");
        model.addAttribute("exito", false);
        return "resultado";
    }
}
