# Parcial Final - Sistema de Reservas de Hotel

Backend REST API para un sistema de reservas de hotel desarrollado con Spring Boot, Spring Security, JWT, PostgreSQL, Docker y GitHub Actions.

## 1. Tecnologías

- Java 21
- Spring Boot 4.1.0
- Spring Security
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Docker y Docker Compose
- GitHub Actions

## 2. Arquitectura N-Capas

El proyecto respeta la separación vista en clase:

```text
controller   -> capa de presentación, define endpoints HTTP
service      -> capa de lógica de negocio y reglas de seguridad por sucursal
repository   -> capa de acceso a datos con JpaRepository
entities     -> entidades JPA que representan tablas
dto          -> objetos para recibir y devolver datos sin exponer entidades
mappers      -> transformación Entity <-> DTO
exception    -> manejo global de errores con @RestControllerAdvice
security     -> JWT, filtros y configuración de Spring Security
```

## 3. Regla de negocio no trivial implementada

Se implementó la **Opción B: Autorización por atributo, no solo por rol**.

Un usuario con rol `RECEPTIONIST` solo puede gestionar habitaciones y reservas de la sucursal/hotel al que pertenece. Esto se valida en la capa de servicio comparando:

```text
hotelId del recepcionista autenticado
contra
hotelId de la habitación o reserva que quiere gestionar
```

Esto es más fuerte que revisar solo el rol, porque dos recepcionistas pueden tener el mismo rol, pero pertenecer a sucursales diferentes.

## 4. Roles

| Rol | Permisos |
|---|---|
| ADMIN | Acceso total a hoteles, habitaciones, usuarios y reservas |
| RECEPTIONIST | Gestiona habitaciones, disponibilidad y reservas solo de su sucursal |
| GUEST | Crea, ve y cancela solo sus propias reservas |

## 5. Autenticación

El login devuelve:

- Access Token JWT con expiración corta: 15 minutos.
- Refresh Token con expiración larga: 7 días.

El cliente debe enviar el Access Token en endpoints protegidos:

```http
Authorization: Bearer <access_token>
```

## 6. Levantar con Docker

Ejecutar desde la raíz del proyecto:

```bash
docker-compose up --build
```

La API queda disponible en:

```text
http://localhost:8080
```

PostgreSQL queda expuesto en:

```text
localhost:5433
```

Credenciales de base de datos en Docker:

```text
Database: hotel_reservations_db
User: postgres
Password: root
```

## 7. Levantar local sin Docker

Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE hotel_reservations_db;
```

Luego ejecutar:

```bash
./gradlew bootRun
```

En Windows:

```powershell
.\gradlew.bat bootRun
```

## 8. Usuarios de prueba

El sistema crea datos iniciales automáticamente:

| Rol | Email | Password |
|---|---|---|
| ADMIN | admin@hotel.com | 123456 |
| RECEPTIONIST | reception@hotel.com | 123456 |
| GUEST | guest@hotel.com | 123456 |

También crea dos hoteles de prueba y algunas habitaciones.

## 9. Endpoints principales

### Auth

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
```

### Hoteles

```http
POST   /api/hotels          ADMIN
GET    /api/hotels          Público
GET    /api/hotels/{id}     Público
PUT    /api/hotels/{id}     ADMIN
DELETE /api/hotels/{id}     ADMIN
```

### Habitaciones

```http
POST   /api/rooms                  ADMIN / RECEPTIONIST
GET    /api/rooms                  Público
GET    /api/rooms/{id}             Público
PUT    /api/rooms/{id}             ADMIN / RECEPTIONIST
PATCH  /api/rooms/{id}/availability ADMIN / RECEPTIONIST
DELETE /api/rooms/{id}             ADMIN / RECEPTIONIST
```

Nota: si es `RECEPTIONIST`, la capa de servicio valida que la habitación pertenezca a su hotel.

### Usuarios

```http
POST   /api/users       ADMIN
GET    /api/users       ADMIN
GET    /api/users/{id}  ADMIN
DELETE /api/users/{id}  ADMIN
```

### Reservas

```http
POST  /api/reservations              ADMIN / RECEPTIONIST / GUEST
GET   /api/reservations              ADMIN / RECEPTIONIST / GUEST con filtros de autorización
GET   /api/reservations/my           GUEST
GET   /api/reservations/{id}         ADMIN / RECEPTIONIST / GUEST según permisos
PUT   /api/reservations/{id}         ADMIN / RECEPTIONIST
PATCH /api/reservations/{id}/confirm ADMIN / RECEPTIONIST
PATCH /api/reservations/{id}/cancel  ADMIN / RECEPTIONIST / GUEST según permisos
```

## 10. Flujo rápido de prueba

### 1. Login ADMIN

```http
POST /api/auth/login
```

```json
{
  "email": "admin@hotel.com",
  "password": "123456"
}
```

### 2. Login RECEPTIONIST

```json
{
  "email": "reception@hotel.com",
  "password": "123456"
}
```

### 3. Login GUEST

```json
{
  "email": "guest@hotel.com",
  "password": "123456"
}
```

### 4. Crear reserva como huésped

```http
POST /api/reservations
Authorization: Bearer <guest_token>
```

```json
{
  "roomId": 1,
  "startDate": "2026-08-10",
  "endDate": "2026-08-12"
}
```

### 5. Confirmar reserva como recepcionista

```http
PATCH /api/reservations/1/confirm
Authorization: Bearer <receptionist_token>
```

Si la reserva pertenece al hotel del recepcionista, funciona. Si pertenece a otra sucursal, responde `403 Forbidden`.

## 11. CI/CD

El workflow está en:

```text
.github/workflows/ci.yml
```

Se ejecuta en cada push a `main` o `master` y realiza:

1. Checkout del repositorio.
2. Escaneo de secretos con Gitleaks.
3. Configuración de JDK 21.
4. Build y tests con Gradle.
5. Escaneo de vulnerabilidades críticas con Trivy.

## 12. Evidencia de IA

Este proyecto incluye:

```text
PROMPTS.md
REFLEXION.md
```

Estos archivos documentan el uso de IA, qué generó, qué se corrigió y cómo se justificaron las decisiones de seguridad.
