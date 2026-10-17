# 🐇 RabbitMQ en Q-Hay-Pa-Hacer — Guía de trabajo para el equipo (4 × 25%)

Monolito modular: todo vive en el backend existente (`backend/`, puerto 8080).
No se toca MySQL RDS, Azure AD ni el frontend.

| Parte | Responsable | Indicadores de la rúbrica | Estado |
|---|---|---|---|
| **1. Infraestructura y configuración** | Integrante 1 | 1 (12%) + 2 (13%) | ✅ **HECHA** |
| **2. Publicadores + integración + endpoint de prueba DLQ** | Integrante 2 | 3 (10%) + soporte a 5 | ✅ **HECHA** |
| **3. Consumidores con ACK manual + DLQ** | Integrante 3 | 4 (15%) + 5 (20%) | ⏳ Pendiente |
| **4. Módulo administrador REST** | Integrante 4 | 6 (13%) + 7 (10%) + 8 (7%) | ⏳ Pendiente |

> [!IMPORTANT]
> Las partes 2, 3 y 4 **no dependen entre sí**: solo dependen de la Parte 1 (que ya está lista).
> Pueden trabajar **en paralelo**, cada uno en su rama.

---

## 0. Cómo levantar el entorno (todos)

```powershell
# 1) Levantar SOLO RabbitMQ (desde la carpeta Q-Hay-Pa-Hacer)
docker compose up -d rabbitmq

# 2) Panel web: http://localhost:15672   usuario: quehaypahacer / clave: quehaypahacer

# 3) Correr el backend como siempre (desde backend/)
.\mvnw.cmd spring-boot:run
```

Al arrancar el backend, `RabbitAdmin` crea solo (en el panel → pestaña *Queues* y *Exchanges*):

```
recetas.exchange (topic)
   ├── recetas.ticket.generar     ──► recetas.tickets.queue         (DLX → recetas.dlx.exchange)
   └── recetas.despensa.notificar ──► recetas.notificaciones.queue  (DLX → recetas.dlx.exchange)

recetas.dlx.exchange (direct)
   ├── recetas.ticket.dlq         ──► recetas.tickets.dlq
   └── recetas.despensa.dlq       ──► recetas.notificaciones.dlq
```

### Flujo de Git sugerido

```powershell
git pull origin main
git checkout -b feature/rabbitmq-parte-2   # (o parte-3 / parte-4)
# ... trabajar ...
.\mvnw.cmd -DskipTests compile             # DEBE compilar antes de hacer push
git push origin feature/rabbitmq-parte-2   # y abrir Pull Request
```

---

## 1. ✅ Parte 1 — Lo que YA está hecho (contrato común)

| Archivo | Qué contiene |
|---|---|
| `docker-compose.yml` | Servicio `rabbitmq:3.13-management` (5672 / 15672), healthcheck, el backend espera a que esté sano |
| `backend/pom.xml` | `spring-boot-starter-amqp` |
| `application.yml` | Conexión `spring.rabbitmq.*`, **`acknowledge-mode: manual`**, `default-requeue-rejected: false` y todos los nombres en `app.rabbitmq.*` |
| `config/rabbitmq/RabbitMQProperties.java` | Record tipado y validado con todos los nombres |
| `config/rabbitmq/RabbitMQConfig.java` | Beans: 2 exchanges, 2 colas + 2 DLQ, 4 bindings, `JacksonJsonMessageConverter`, `RabbitTemplate`, `RabbitAdmin` |
| `messaging/event/TicketCocinaEvent.java` | Evento de ticket de cocinado |
| `messaging/event/NotificacionDespensaEvent.java` | Evento de despensa (con enum `Accion`) |
| `.env.example` (raíz y backend) | Variables `RABBITMQ_*` |

### 🚫 Reglas de oro (si se rompen, se pierden puntos)

1. **Cero "magic strings"**: nunca escribir `"recetas.tickets.queue"` en Java.
   - En clases normales → inyectar `RabbitMQProperties` y usar `props.queues().tickets()`.
   - En anotaciones `@RabbitListener` → usar placeholder: `queues = "${app.rabbitmq.queues.tickets}"`.
2. **Los servicios de negocio (`HistorialCocinaService`, `DespensaService`) nunca importan nada de `org.springframework.amqp`**. Solo conocen las interfaces `*Publisher`.
3. **Usar los records de `messaging/event`** tal cual. Si necesitas un campo nuevo, avisa al grupo (afecta a la Parte 2 y la 3).
4. Spring Boot es **4.1.1** → Spring AMQP 4 + Jackson 3. **No usar `Jackson2JsonMessageConverter`** (está deprecado; ya está configurado el correcto).

### Accesos rápidos a las propiedades

```java
props.exchanges().principal()          // recetas.exchange
props.exchanges().deadLetter()         // recetas.dlx.exchange
props.queues().tickets()               // recetas.tickets.queue
props.queues().notificaciones()        // recetas.notificaciones.queue
props.queues().ticketsDlq()            // recetas.tickets.dlq
props.queues().notificacionesDlq()     // recetas.notificaciones.dlq
props.routingKeys().tickets()          // recetas.ticket.generar
props.routingKeys().notificaciones()   // recetas.despensa.notificar
```

### 🤝 Contrato de validación (lo acuerdan las Partes 2 y 3)

Un `TicketCocinaEvent` es **inválido** si `recetaId == null || recetaId <= 0`.
- La Parte 2 envía uno así a propósito desde el endpoint de prueba.
- La Parte 3 lo detecta, hace `basicNack(tag, false, false)` y el mensaje cae en `recetas.tickets.dlq`.

Lo mismo para `NotificacionDespensaEvent`: inválido si `ingredienteUsuarioId == null || ingredienteUsuarioId <= 0 || accion == null`.

---

## 2. ⏳ Parte 2 — Publicadores + integración + endpoint de prueba (Integrante 2)

**Indicador 3**: la lógica de negocio queda separada de la mensajería.

### Archivos a crear

```
backend/src/main/java/cl/quehaypahacer/backend/
├── messaging/publisher/
│   ├── TicketEventPublisher.java              (interface)
│   ├── NotificacionEventPublisher.java        (interface)
│   └── impl/
│       ├── RabbitTicketEventPublisher.java
│       └── RabbitNotificacionEventPublisher.java
└── admin/rabbitmq/controller/
    └── RabbitTestController.java              (endpoint para forzar la DLQ)
```

### 2.1 Interfaces (lo único que ve el negocio)

```java
public interface TicketEventPublisher {
    void publicarTicketCocina(TicketCocinaEvent event);
}

public interface NotificacionEventPublisher {
    void publicarNotificacionDespensa(NotificacionDespensaEvent event);
}
```

### 2.2 Implementación (ejemplo completo para tickets)

Se publica **después del commit** de la transacción. Así, si la BD hace rollback, no sale un evento "fantasma". Además, si RabbitMQ está caído, **el endpoint de negocio no debe fallar**.

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitTicketEventPublisher implements TicketEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties props;

    @Override
    public void publicarTicketCocina(TicketCocinaEvent event) {
        Runnable envio = () -> {
            try {
                rabbitTemplate.convertAndSend(
                        props.exchanges().principal(),
                        props.routingKeys().tickets(),
                        event);
                log.info("[PUBLISHER] TicketCocinaEvent enviado: historialId={}", event.historialId());
            } catch (AmqpException e) {
                log.error("[PUBLISHER] No se pudo publicar TicketCocinaEvent {}: {}", event, e.getMessage());
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { envio.run(); }
            });
        } else {
            envio.run();
        }
    }
}
```

`RabbitNotificacionEventPublisher` es igual, pero usa `props.routingKeys().notificaciones()`.

### 2.3 Integración en los servicios (cambios mínimos)

**`HistorialCocinaService.registrarCocinado(...)`**: agregar `private final TicketEventPublisher ticketEventPublisher;` y, después del `save`:

```java
HistorialCocina guardado = historialCocinaRepository.save(nuevoRegistro);
ticketEventPublisher.publicarTicketCocina(new TicketCocinaEvent(
        guardado.getId(), receta.getId(), receta.getNombre(),
        usuario.getCorreo(), guardado.getNotas(), guardado.getCocinadoAt()));
return HistorialCocinaResponse.fromEntity(guardado);
```

**`DespensaService`**: agregar `private final NotificacionEventPublisher notificacionEventPublisher;` y publicar en:
- `agregarOActualizar` → `AGREGADO` si era nuevo, `ACTUALIZADO` si ya existía (pista: revisar si `item.getId() == null` antes del `save`).
- `actualizarCantidad` → `ACTUALIZADO`.
- `eliminarDeDespensa` → `ELIMINADO`. Hay que armar el evento **antes** del `delete`, con `cantidad = null`.

> [!WARNING]
> Verifica los getters reales (`receta.getNombre()`, `item.getIngrediente().getNombre()`, `item.getUnidad().name()`) contra las clases de `model/`.

### 2.4 Endpoint de prueba DLQ (`RabbitTestController`)

Base: `/api/admin/rabbitmq/test`. **Todo con `@PreAuthorize("hasRole('Admin')")`**.

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/ticket-valido` | Publica un `TicketCocinaEvent` válido (`recetaId = 1`) → consumidor hace ACK |
| POST | `/ticket-invalido` | Publica uno con `recetaId = -1` → consumidor hace NACK → **cae en `recetas.tickets.dlq`** |
| POST | `/notificacion-invalida` | Publica un `NotificacionDespensaEvent` con `accion = null` → `recetas.notificaciones.dlq` |

Respuesta: `202 Accepted` con un JSON tipo `{"mensaje": "Evento inválido publicado", "routingKey": "..."}`.
El controlador **solo llama a los publishers**, nunca a `RabbitTemplate` directo.

### ✅ Listo cuando
- [ ] `POST /api/historial/{recetaId}` aparece en el panel (Queues → `recetas.tickets.queue` → gráfico *Message rates*).
- [ ] Cambios en la despensa publican en `recetas.notificaciones.queue`.
- [ ] Con RabbitMQ apagado, `POST /api/historial/{id}` sigue respondiendo 200 (solo deja un log de error).
- [ ] Ningún archivo de `service/` importa `org.springframework.amqp`.

---

## 3. ⏳ Parte 3 — Consumidores con ACK manual + DLQ (Integrante 3)

**Indicadores 4 y 5 (35%, el bloque que más pesa).**

### Archivos a crear

```
backend/src/main/java/cl/quehaypahacer/backend/messaging/consumer/
├── ticket/TicketCocinaConsumer.java
├── notificacion/NotificacionConsumer.java
└── dlq/DeadLetterQueueConsumer.java
```

### 3.1 Plantilla del consumidor (tickets)

```java
@Slf4j
@Component
public class TicketCocinaConsumer {

    @RabbitListener(queues = "${app.rabbitmq.queues.tickets}", ackMode = "MANUAL")
    public void onTicketCocina(TicketCocinaEvent event,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            validar(event);                    // lanza IllegalArgumentException si es inválido
            generarTicket(event);              // simulación: log con la "ficha de cocinado"
            channel.basicAck(deliveryTag, false);
            log.info("[ACK] Ticket generado historialId={} receta='{}'", event.historialId(), event.recetaNombre());
        } catch (Exception e) {
            log.error("[NACK → DLQ] TicketCocinaEvent rechazado: {} | motivo: {}", event, e.getMessage());
            channel.basicNack(deliveryTag, false, false);   // requeue=false → va al DLX
        }
    }
}
```

Imports clave: `com.rabbitmq.client.Channel`, `org.springframework.amqp.support.AmqpHeaders`, `org.springframework.messaging.handler.annotation.Header`.

> [!CAUTION]
> - **Nunca** relanzar la excepción después de un `basicNack`, porque se produce un doble ACK y el canal se cierra.
> - **Nunca** usar `requeue = true` con errores de negocio, porque genera un bucle infinito.
> - Validar según el **contrato** de la sección 1 (`recetaId <= 0` → inválido).

`NotificacionConsumer` sigue la misma estructura: escucha `${app.rabbitmq.queues.notificaciones}` y simula el envío del aviso, por ejemplo `"🔔 [usuario] tu despensa: Tomate AGREGADO (3 UNIDAD)"`.

### 3.2 Consumidor de auditoría de la DLQ

Escucha **las dos DLQ** y recibe el `Message` crudo para poder leer el header `x-death` (motivo, cola de origen, cantidad de intentos):

```java
@RabbitListener(queues = {"${app.rabbitmq.queues.tickets-dlq}",
                          "${app.rabbitmq.queues.notificaciones-dlq}"}, ackMode = "MANUAL")
public void auditar(Message message, Channel channel,
                    @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
    // log.warn con: cola consumida, body (new String(message.getBody(), UTF_8)), headers x-death
    channel.basicAck(tag, false);
}
```

> [!NOTE]
> Si prefieres que los mensajes **se queden visibles** en la DLQ del panel durante la defensa, se puede agregar `autoStartup = "${app.rabbitmq.dlq-auditor-enabled:true}"` y apagarlo con esa propiedad. Coordinarlo con el grupo.

### ✅ Listo cuando
- [ ] Evento válido → log `[ACK]` y la cola queda en 0.
- [ ] `POST /api/admin/rabbitmq/test/ticket-invalido` (Parte 2) → log `[NACK → DLQ]` y luego log de auditoría de la DLQ.
- [ ] Un payload corrupto publicado a mano desde el panel (Queues → *Publish message* con body `hola`) también termina en la DLQ: Spring rechaza los errores de conversión sin reencolar.
- [ ] Cada consumidor está en su subpaquete por dominio (`ticket`, `notificacion`, `dlq`).

---

## 4. ⏳ Parte 4 — Módulo administrador REST (Integrante 4)

**Indicadores 6, 7 y 8.**

### Archivos a crear

```
backend/src/main/java/cl/quehaypahacer/backend/admin/rabbitmq/
├── controller/RabbitAdminController.java
├── service/RabbitAdminService.java
├── service/impl/RabbitAdminServiceImpl.java
├── dto/QueueRequestDTO.java
├── dto/ExchangeRequestDTO.java
├── dto/BindingRequestDTO.java
├── dto/QueueStatusDTO.java                 (respuesta del GET de estado)
├── exception/RabbitResourceNotFoundException.java
└── exception/RabbitAdminExceptionHandler.java
```

### 4.1 Endpoints (base `/api/admin/rabbitmq`, todos `@PreAuthorize("hasRole('Admin')")`)

| Método | Ruta | Body | Respuesta |
|---|---|---|---|
| POST | `/queues` | `QueueRequestDTO` | 201 |
| GET | `/queues/{name}` | — | 200 `QueueStatusDTO` / 404 |
| DELETE | `/queues/{name}` | — | 204 / 404 |
| POST | `/exchanges` | `ExchangeRequestDTO` | 201 |
| DELETE | `/exchanges/{name}` | — | 204 |
| POST | `/bindings` | `BindingRequestDTO` | 201 |
| DELETE | `/bindings` | `BindingRequestDTO` | 204 |

**El controlador solo delega** (no usa `RabbitAdmin` directamente). Ejemplo:

```java
@PostMapping("/queues")
public ResponseEntity<Map<String, String>> crearCola(@Valid @RequestBody QueueRequestDTO dto) {
    rabbitAdminService.crearCola(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Cola creada", "nombre", dto.name()));
}
```

### 4.2 Servicio (encapsula `RabbitAdmin`, Indicador 7)

El bean `RabbitAdmin` **ya existe** en `RabbitMQConfig`: solo se inyecta.

```java
public interface RabbitAdminService {
    void crearCola(QueueRequestDTO dto);
    void eliminarCola(String nombre);
    QueueStatusDTO obtenerEstadoCola(String nombre);
    void crearExchange(ExchangeRequestDTO dto);
    void eliminarExchange(String nombre);
    void crearBinding(BindingRequestDTO dto);
    void eliminarBinding(BindingRequestDTO dto);
}
```

Pistas para la implementación:
- **Cola:** `QueueBuilder.durable(name)` o `.nonDurable(name)`, luego `.autoDelete()` si se pide, `.deadLetterExchange(...)` si viene; terminar con `rabbitAdmin.declareQueue(queue)`.
- **Exchange:** `ExchangeBuilder.directExchange / topicExchange / fanoutExchange / headersExchange(name).durable(d).build()`, luego `rabbitAdmin.declareExchange(...)`.
- **Binding:** `new Binding(queue, Binding.DestinationType.QUEUE, exchange, routingKey, null)`, luego `declareBinding` / `removeBinding`.
- **Estado:** `rabbitAdmin.getQueueInfo(name)`. Si devuelve `null`, lanzar `RabbitResourceNotFoundException` (→ 404). Si no, devolver `messageCount` y `consumerCount`.
- **Eliminar cola:** `rabbitAdmin.deleteQueue(name)` devuelve `false` si no existía, y eso también es 404.
- 💡 Extra para la defensa: impedir borrar las colas del sistema (las de `RabbitMQProperties`) y responder 409 Conflict.

### 4.3 DTOs con validación (Indicador 8), como `record`

```java
public record QueueRequestDTO(
        @NotBlank(message = "El nombre de la cola es obligatorio")
        @Size(max = 255)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Solo letras, números, punto, guion y guion bajo")
        String name,
        boolean durable,
        boolean autoDelete,
        @Pattern(regexp = "^[a-zA-Z0-9._-]*$") String deadLetterExchange,
        @Pattern(regexp = "^[a-zA-Z0-9._-]*$") String deadLetterRoutingKey
) {}

public record ExchangeRequestDTO(
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9._-]+$") String name,
        @NotBlank @Pattern(regexp = "direct|topic|fanout|headers",
                           message = "Tipo debe ser direct, topic, fanout o headers") String type,
        boolean durable
) {}

public record BindingRequestDTO(
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9._-]+$") String queue,
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9._-]+$") String exchange,
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9._*#-]+$") String routingKey
) {}
```

Para validar los `@PathVariable {name}`: anotar el controlador con `@Validated` y el parámetro con `@Pattern(...)`.

### 4.4 Manejo de errores (solo del módulo admin)

> [!WARNING]
> Usar `@RestControllerAdvice(basePackages = "cl.quehaypahacer.backend.admin.rabbitmq")`. **No hacerlo global**, porque cambiaría las respuestas de los endpoints que ya existen.

| Excepción | HTTP | Body |
|---|---|---|
| `MethodArgumentNotValidException` | 400 | `{"error":"Validación fallida","campos":{"name":"El nombre de la cola es obligatorio"}}` |
| `HandlerMethodValidationException` (path variable) | 400 | mensaje de validación |
| `HttpMessageNotReadableException` (JSON mal formado) | 400 | `{"error":"JSON inválido"}` |
| `RabbitResourceNotFoundException` | 404 | `{"error":"Cola 'x' no existe"}` |
| `AmqpException` | 503 | `{"error":"Broker RabbitMQ no disponible"}` |

### ✅ Listo cuando
- [ ] Crear una cola por Postman → aparece en el panel. DELETE → desaparece.
- [ ] `{"name": ""}` → 400 con detalle del campo; `{"name": "mi cola!"}` → 400.
- [ ] Sin token → 401; con token sin rol Admin → 403.
- [ ] `GET /queues/recetas.tickets.queue` devuelve el conteo de mensajes y consumidores.

---

## 5. Postman (Partes 2 y 4)

Para evitar conflictos de merge en `Q-Hay-Pa-Hacer-Postman-Collection.json`, **cada uno agrega su propia carpeta**:
- **Parte 2** → carpeta `RabbitMQ - Eventos y DLQ` (historial, despensa, ticket-válido, ticket-inválido, notificación-inválida).
- **Parte 4** → carpeta `RabbitMQ - Admin` (CRUD + 3 casos de error 400 + caso 404).

Usar la variable `{{token}}` (token de un usuario con rol **Admin**) en `Authorization: Bearer {{token}}`.

---

## 6. Guion para la defensa (cuando todo esté integrado)

1. `docker compose up -d` → abrir `http://localhost:15672` → mostrar exchanges, colas, DLQ y sus argumentos `x-dead-letter-*`.
2. Mostrar `application.yml` + `RabbitMQProperties` (Ind. 1) y `RabbitMQConfig` (Ind. 2).
3. Mostrar `HistorialCocinaService`: no importa AMQP, solo la interfaz del publisher (Ind. 3).
4. Postman `POST /api/historial/1` → consola `[PUBLISHER]` + `[ACK]` (Ind. 4 y 5).
5. Postman `ticket-invalido` → consola `[NACK → DLQ]` + auditoría; panel → `recetas.tickets.dlq` (Ind. 5).
6. Postman admin: crear cola → panel; `name` vacío → 400; borrar → 204 (Ind. 6, 7 y 8).
