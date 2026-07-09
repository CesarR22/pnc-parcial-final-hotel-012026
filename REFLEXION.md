# REFLEXION.md - Reflexión sobre uso de IA

## 1. ¿Qué partes generó bien la IA sin necesidad de corrección?

La IA ayudó bastante bien con la estructura base del proyecto. La división en capas quedó alineada con lo visto en clase: controller, service, repository, entities, dto, mappers, exception y security. También generó una buena base para los DTOs, las entidades JPA y los endpoints principales.

Otra parte que generó bien fue la idea general del flujo de autenticación: login, generación de Access Token, Refresh Token y uso del header `Authorization: Bearer <token>` para consumir endpoints protegidos.

## 2. ¿Qué errores o decisiones incorrectas tomó la IA, especialmente en seguridad?

La principal decisión que podía quedar mal era pensar que con solo usar `hasRole('RECEPTIONIST')` ya estaba resuelta la seguridad. Eso no era suficiente para este caso, porque el enunciado pide que el recepcionista solo gestione reservas de su propia sucursal.

Si se dejaba solo la validación por rol, cualquier recepcionista podría modificar reservas de cualquier hotel. Eso sería un error de autorización, porque todos los recepcionistas tienen el mismo rol, pero no deberían tener acceso a las mismas sucursales.

También se revisó que el Refresh Token no debía ser tratado igual que el Access Token. El Access Token es corto y viaja en cada request, mientras que el Refresh Token debe durar más y poder invalidarse.

## 3. ¿Cómo detectaron esos errores y cómo los corrigieron?

Se detectó revisando el enunciado y comparándolo con lo generado por la IA. El enunciado decía claramente que el recepcionista gestiona únicamente su sucursal, entonces no bastaba con validar el rol.

Para corregirlo, se agregó una relación entre el usuario recepcionista y el hotel al que pertenece. Luego, en los servicios, se compara el `hotelId` del usuario autenticado contra el `hotelId` de la habitación o reserva que intenta gestionar.

Si los hoteles no coinciden, el backend lanza un `AccessDeniedException`, que se responde como `403 Forbidden`.

## 4. Explicación corta de la autorización por sucursal

La autorización por sucursal significa que no solo se revisa si el usuario es recepcionista, sino también a qué hotel pertenece. Cuando un recepcionista intenta confirmar, modificar o cancelar una reserva, el sistema compara la sucursal del recepcionista con la sucursal de la habitación reservada. Si no coinciden, se bloquea la operación con un error 403.

## 5. ¿Por qué esta regla es importante?

Porque en un sistema con varias sucursales no todos los usuarios con el mismo rol deben tener acceso a todos los datos. Dos recepcionistas pueden tener el mismo rol, pero uno pertenece al Hotel Central y otro al Hotel Playa. Cada uno solo debe gestionar su propia sucursal para proteger la información y evitar cambios incorrectos.
