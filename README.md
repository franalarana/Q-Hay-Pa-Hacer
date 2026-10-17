# Q-Hay-Pa-Hacer

App de recetas según la despensa del usuario. Backend Spring Boot (monolito modular), frontend React, autenticación con Microsoft Entra ID (Azure AD) y base de datos MySQL en Amazon RDS.

## RabbitMQ: mensajería asíncrona

El backend usa RabbitMQ para tareas que no deben bloquear la petición del usuario:

- **Ticket de cocinado**: al registrar que se cocinó una receta, se genera una "ficha" de forma asíncrona.
- **Notificación de despensa**: al agregar, actualizar o eliminar un ingrediente de la despensa, se envía un aviso.

La lógica de negocio (`HistorialCocinaService`, `DespensaService`) no conoce RabbitMQ: solo llama a las interfaces `TicketEventPublisher` y `NotificacionEventPublisher`.

### Topología

```
recetas.exchange (topic)
   ├── recetas.ticket.generar     ──► recetas.tickets.queue         (DLX → recetas.dlx.exchange)
   └── recetas.despensa.notificar ──► recetas.notificaciones.queue  (DLX → recetas.dlx.exchange)

recetas.dlx.exchange (direct)
   ├── recetas.ticket.dlq         ──► recetas.tickets.dlq
   └── recetas.despensa.dlq       ──► recetas.notificaciones.dlq
```

Todos los nombres viven en `application.yml` (`app.rabbitmq.*`) y se leen con `RabbitMQProperties`. Las colas, exchanges y bindings se declaran como beans en `RabbitMQConfig`; `RabbitAdmin` los crea en el broker al arrancar.

### Flujo

1. **Publicar**: el servicio de negocio llama al publisher, que envía el evento JSON al exchange **después del commit** de la transacción. Si RabbitMQ está caído, solo se registra un error en el log y el endpoint de negocio responde normal.
2. **Consumir**: `TicketCocinaConsumer` y `NotificacionConsumer` usan `@RabbitListener` con **ACK manual**.
   - Evento válido → procesa y hace `basicAck`.
   - Evento inválido (por ejemplo `recetaId <= 0`) → `basicNack(tag, false, false)`, sin reencolar.
3. **Dead letter**: un mensaje rechazado, o que no se pueda deserializar, viaja por el DLX a la DLQ de su cola. `DeadLetterQueueConsumer` lo audita en el log (cola, motivo `x-death` y cuerpo).

### Levantar el entorno

```powershell
# 1) Broker RabbitMQ (desde esta carpeta)
docker compose up -d rabbitmq

# 2) Backend (desde backend/)
.\mvnw.cmd spring-boot:run
```

- Panel web: http://localhost:15672 (usuario `quehaypahacer`, clave `quehaypahacer`).
- En la pestaña *Exchanges* y *Queues* se ven los recursos de la topología.
- Variables: copia `.env.example` a `.env` (`RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`). Los valores por defecto sirven para local.
- Para levantar todo (RabbitMQ, backend y frontend) con Docker: `docker compose up -d --build`. Requiere `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en el `.env`.

### Probar el flujo

Importa `Q-Hay-Pa-Hacer-Postman-Collection.json` y define `{{token}}` con un token de Azure AD. Para los endpoints `/api/admin/**` el usuario debe tener el rol `Admin`.

| Qué probar | Cómo | Resultado esperado |
|---|---|---|
| Ticket normal | `POST /api/historial/{recetaId}` | Logs `[PUBLISHER]` y luego `[ACK]` |
| Despensa | `POST /api/despensa` | Logs `[PUBLISHER]` y `[ACK]` de la notificación |
| Forzar DLQ (ticket) | `POST /api/admin/rabbitmq/test/ticket-invalido` | Log `[NACK → DLQ]` y luego `[DLQ AUDIT]` |
| Forzar DLQ (notificación) | `POST /api/admin/rabbitmq/test/notificacion-invalida` | Igual, en `recetas.notificaciones.dlq` |
| Evento válido directo | `POST /api/admin/rabbitmq/test/ticket-valido` | `[ACK]` |

El auditor de DLQ confirma y vacía las DLQ al instante. Para ver los mensajes en el panel, apaga el auditor con `app.rabbitmq.dlq-auditor-enabled=false`.

### Módulo administrador REST

Base `/api/admin/rabbitmq`, solo rol `Admin`. El controlador delega en `RabbitAdminService`, que es el único que usa `RabbitAdmin`.

| Método | Ruta | Cuerpo | Respuesta |
|---|---|---|---|
| POST | `/queues` | `{"name","durable","autoDelete","deadLetterExchange","deadLetterRoutingKey"}` | 201 |
| GET | `/queues/{name}` | — | 200 con `messageCount` y `consumerCount`, o 404 |
| DELETE | `/queues/{name}` | — | 204, 404, o 409 si es una cola del sistema |
| POST | `/exchanges` | `{"name","type","durable"}` (`type`: direct, topic, fanout, headers) | 201 |
| DELETE | `/exchanges/{name}` | — | 204, o 409 si es un exchange del sistema |
| POST | `/bindings` | `{"queue","exchange","routingKey"}` | 201, o 404 si la cola no existe |
| DELETE | `/bindings` | `{"queue","exchange","routingKey"}` | 204 |

Errores: 400 si la validación falla (nombre vacío, caracteres inválidos o JSON malformado), 401 sin token, 403 sin rol Admin y 503 si el broker no responde.

### Conceptos para la defensa

- **ACK**: el consumidor confirma que procesó el mensaje; sin ACK el broker lo considera pendiente. `basicNack` sin reencolar lo envía al DLX.
- **Durabilidad**: la cola o el exchange sobrevive a un reinicio del broker (`durable=true`).
- **Persistencia**: el mensaje se guarda en disco. Spring AMQP publica los mensajes como persistentes por defecto, pero solo sobreviven a un reinicio si además están en una cola durable.
- **DLQ**: cola donde caen los mensajes rechazados, expirados o que fallaron al convertirse, para auditarlos sin perderlos.

Guía de trabajo del equipo: [RABBITMQ_GUIA_EQUIPO.md](RABBITMQ_GUIA_EQUIPO.md).

### Tests

```powershell
cd backend
.\mvnw.cmd test -Dtest="Rabbit*,*ConsumerTest,*PublisherTest"
```

`BackendApplicationTests` necesita una base de datos MySQL accesible; sin ella falla al cargar el contexto.
