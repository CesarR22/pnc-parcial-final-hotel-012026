# PROMPTS.md - Evidencia de uso de IA

## Prompt 1 - Análisis del enunciado

**Herramienta:** ChatGPT

**Prompt usado:**

> Analiza el enunciado del parcial final de Programación N-Capas. Necesito construir una API de reservas de hotel con seguridad, JWT, roles, Docker, GitHub Actions y evidencia de IA. Dame una arquitectura que cumpla la rúbrica.

**Qué generó la IA:**

La IA propuso una arquitectura N-Capas con controladores, servicios, repositorios, entidades, DTOs, mappers, excepciones globales, configuración de seguridad, JWT y Docker.

**Qué corregí o adapté:**

Se decidió usar la regla no trivial de autorización por atributo: el recepcionista solo puede gestionar reservas y habitaciones de su sucursal. Esta regla se eligió porque está directamente relacionada con el negocio del hotel y demuestra que la autorización no depende solo del rol.

---

## Prompt 2 - Diseño de entidades

**Herramienta:** ChatGPT

**Prompt usado:**

> Genera las entidades mínimas para un sistema de reservas de hotel: Hotel/Sucursal, Habitación, Reserva y Usuario con rol. Usa JPA, Lombok y relaciones correctas.

**Qué generó la IA:**

La IA propuso entidades `Hotel`, `Room`, `Reservation`, `User` y `RefreshToken`. También sugirió enums para roles, tipo de habitación y estado de reserva.

**Qué corregí o adapté:**

Se agregó relación entre `User` y `Hotel` solo para usuarios recepcionistas. Esto permite validar la sucursal a la que pertenece el recepcionista. También se separó `Room` de `Hotel` con `@ManyToOne`, porque una sucursal puede tener muchas habitaciones.

---

## Prompt 3 - Autenticación con JWT y Refresh Token

**Herramienta:** ChatGPT

**Prompt usado:**

> Crea el flujo de autenticación para Spring Security: login con email y password, generación de Access Token JWT y Refresh Token, y endpoint para renovar el Access Token.

**Qué generó la IA:**

La IA propuso `AuthController`, `AuthService`, `JwtTokenProvider`, `JwtAuthenticationFilter`, `RefreshToken` y repositorio de refresh tokens.

**Qué corregí o adapté:**

Se decidió que el Access Token sea JWT y el Refresh Token sea un token opaco guardado en base de datos. Esto facilita revocarlo en logout y validar su vencimiento. También se configuró una expiración corta para Access Token y una más larga para Refresh Token.

---

## Prompt 4 - Seguridad por roles

**Herramienta:** ChatGPT

**Prompt usado:**

> Configura Spring Security para proteger endpoints con roles ADMIN, RECEPTIONIST y GUEST. El login y registro deben ser públicos, lo demás debe requerir token.

**Qué generó la IA:**

La IA generó `SecurityConfiguration` con `SecurityFilterChain`, `PasswordEncoder`, `AuthenticationManager`, `JwtAuthenticationFilter`, desactivación de CSRF y uso de `@PreAuthorize`.

**Qué corregí o adapté:**

Se revisó que no bastaba con poner `hasRole('RECEPTIONIST')`, porque un recepcionista podría intentar modificar una reserva de otra sucursal. Por eso se agregó validación adicional en los servicios.

---

## Prompt 5 - Regla de negocio no trivial

**Herramienta:** ChatGPT

**Prompt usado:**

> Implementa autorización por atributo: un recepcionista solo puede confirmar, modificar o cancelar reservas de su propia sucursal. Debe comparar el hotel del usuario autenticado con el hotel de la habitación o reserva.

**Qué generó la IA:**

La IA propuso obtener el usuario autenticado desde el `SecurityContextHolder` y comparar `user.hotel.id` contra `reservation.room.hotel.id`.

**Qué corregí o adapté:**

Se centralizó la validación en métodos privados dentro de los servicios para evitar duplicar lógica. También se aplicó la misma idea a habitaciones: un recepcionista no puede crear, editar ni cambiar disponibilidad de habitaciones de otra sucursal.

---

## Prompt 6 - Docker

**Herramienta:** ChatGPT

**Prompt usado:**

> Genera un Dockerfile y docker-compose.yml para levantar una API Spring Boot con PostgreSQL usando un solo comando.

**Qué generó la IA:**

La IA generó un Dockerfile multi-stage y un docker-compose con servicios `api` y `db`.

**Qué corregí o adapté:**

Se usó puerto `5433:5432` para evitar conflicto con PostgreSQL local. También se usaron variables de entorno para la conexión de la API al contenedor de base de datos.

---

## Prompt 7 - CI/CD con GitHub Actions

**Herramienta:** ChatGPT

**Prompt usado:**

> Crea un workflow de GitHub Actions que compile el proyecto, ejecute pruebas y falle si detecta secretos o vulnerabilidades críticas.

**Qué generó la IA:**

La IA propuso un workflow con `actions/checkout`, `actions/setup-java`, build con Gradle, Gitleaks y Trivy.

**Qué corregí o adapté:**

Se dejó el workflow en `.github/workflows/ci.yml` y se configuró para ejecutarse en push o pull request hacia `main` o `master`. También se agregó el permiso de ejecución para el wrapper de Gradle.

---

## Prompt 8 - Documentación del proyecto

**Herramienta:** ChatGPT

**Prompt usado:**

> Crea un README, PROMPTS.md y REFLEXION.md para explicar cómo levantar el proyecto, cómo funciona la seguridad, los roles y la regla no trivial.

**Qué generó la IA:**

La IA generó documentación inicial explicando Docker, endpoints, roles y flujo de prueba.

**Qué corregí o adapté:**

Se ajustó el README para que coincidiera con los endpoints reales y se dejó la reflexión escrita en un lenguaje sencillo para poder defender el proyecto oralmente.
