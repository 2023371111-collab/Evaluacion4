# Mapa sugerido para la calificación /80

| Sección | Evidencia | Valor sugerido |
|---|---|---:|
| 1. Contenedores | Seis Dockerfiles multi-etapa e imágenes construidas | 10 |
| 2. Infraestructura | `compose.yaml`, red, volumen, variables y health checks | 10 |
| 3. Tomcat + Web | Frontend y servicios Spring Boot en ejecución | 10 |
| 4. Base de datos | PostgreSQL saludable, esquemas `msa` y `msb`, datos persistentes | 10 |
| 5. Eureka | A y B con dos instancias registradas | 10 |
| 6. Gateway y balanceo | Rutas `lb://` y encabezados de instancias alternantes | 10 |
| 7. Autenticación | JWT válido y rechazo 401 sin token | 10 |
| 8. Integración y liberación | B consume A con Feign, smoke test PASS y enlace del repositorio | 10 |
| **Total** |  | **80** |

El PDF de entrega debe mantener estas ocho secciones y colocar debajo de cada una las capturas correspondientes.
