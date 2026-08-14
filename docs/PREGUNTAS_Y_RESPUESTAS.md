# Preguntas probables del profesor

## 1. ¿Cuál es la diferencia entre una imagen y un contenedor?

Una imagen es una plantilla inmutable con la aplicación y sus dependencias. Un contenedor es una instancia en ejecución de esa imagen, con proceso, red y sistema de archivos aislados.

## 2. ¿Por qué se usan Dockerfiles multi-etapa?

La primera etapa compila con Gradle o Maven y JDK; la imagen final contiene únicamente JRE y el JAR. Así se reduce tamaño, superficie de ataque y herramientas innecesarias en producción.

## 3. ¿Qué orquesta Docker Compose?

Nueve contenedores: PostgreSQL, Eureka, dos réplicas de A, dos de B, OAuth2 Auth Server, API Gateway y Frontend. También crea la red, el volumen persistente, variables de entorno, dependencias y health checks.

## 4. ¿Dónde está Tomcat embebido?

Los proyectos Spring Boot Web MVC lo incluyen mediante sus starters. A, B, Eureka, Gateway MVC, Auth y Frontend ejecutan un JAR autónomo con Tomcat embebido; no se instala un Tomcat externo.

## 5. ¿Para qué sirve Eureka?

Mantiene el registro dinámico de servicios. A y B registran sus instancias; Gateway y Feign consultan el registro por nombre lógico, evitando IP y puerto fijos.

## 6. ¿Cómo se comunica B con A?

`microserviciob` declara `@FeignClient(name = "ms-damian")`. Spring Cloud obtiene de Eureka las instancias disponibles y Spring Cloud LoadBalancer selecciona una para ejecutar `POST /api/entity-a/by-ids`.

## 7. ¿Cómo realiza balanceo el Gateway?

Las rutas usan `lb://ms-damian` y `lb://microserviciob`. El prefijo `lb://` activa Spring Cloud LoadBalancer, que elige una instancia registrada en Eureka por cada petición.

## 8. ¿Cómo se demuestra el balanceo?

Compose levanta dos réplicas de cada microservicio. Cada respuesta contiene `X-Service-Instance`. La prueba repite peticiones y exige observar al menos dos identificadores distintos para A y para B.

## 9. ¿Por qué A y B no publican puertos?

Reduce la superficie expuesta y obliga a entrar por el Gateway. Dentro de la red `microservices-net` se comunican mediante DNS de Compose y Eureka.

## 10. ¿Cómo funciona la autenticación?

El Frontend se autentica ante Spring Authorization Server con `client_credentials`. Obtiene un JWT y lo envía como Bearer token. Gateway valida firma y vigencia usando la llave pública JWK del servidor de autenticación. Sin token responde 401.

## 11. ¿Autentica personas o aplicaciones?

En este caso autentica la aplicación Frontend, no a una persona, porque se usa `client_credentials`. Para usuarios se usaría Authorization Code con PKCE y un repositorio de usuarios.

## 12. ¿Por qué PostgreSQL y no H2?

H2 es embebida y adecuada para pruebas. La rúbrica pide un servidor de base de datos; PostgreSQL corre en un contenedor independiente, persiste en un volumen y es compartido por las réplicas.

## 13. ¿Cómo se separan los datos de A y B?

Se usa una sola instancia PostgreSQL con dos esquemas: `msa` y `msb`. Cada microservicio tiene su URL JDBC y sus tablas. B guarda únicamente el identificador de A y obtiene el nombre mediante la API de A.

## 14. ¿Qué sucede si una réplica falla?

Su health check cambia a no saludable y deja de renovar el lease en Eureka. Después de expirar, Eureka la retira; Gateway y Feign continúan con las instancias sanas.

## 15. ¿Qué persiste al ejecutar `docker compose down`?

El volumen `postgres-data`. Los contenedores se recrean, pero los datos permanecen. `docker compose down -v` elimina el volumen y reinicia la base.

## 16. ¿Qué mejorarían para producción?

TLS, secretos administrados, Authorization Code/PKCE para usuarios, claves RSA persistentes, migraciones Flyway, observabilidad centralizada, límites de recursos, copias de seguridad, rotación de credenciales y alta disponibilidad de Eureka/PostgreSQL/Auth.

