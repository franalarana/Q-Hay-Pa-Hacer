# Guía de Implementación y Defensa: Apache Kafka (Streaming & Analítica)
## Evaluación Parcial N° 5 — Desarrollo Cloud Native I (DSY1107)

Este documento detalla la arquitectura de streaming y analítica en tiempo real implementada en **Q-Hay-Pa-Hacer**, cómo se complementa con RabbitMQ y cómo probar y defender cada uno de los indicadores de la rúbrica de evaluación.

---

## 1. Justificación Arquitectónica: RabbitMQ vs. Apache Kafka

Uno de los principales criterios evaluados es la justificación de diseño y la coexistencia de ambas tecnologías:

| Criterio | RabbitMQ (Evaluación 4) | Apache Kafka (Evaluación 5) |
|---|---|---|
| **Propósito** | Mensajería transaccional y enrutamiento punto a punto (Commands / Tasks). | Streaming continuo de eventos y analítica en tiempo real (Telemetry / Events). |
| **Flujos en la App** | Generación de tickets de cocinado (`recetas.tickets.queue`) y notificaciones con confirmación (`recetas.notificaciones.queue`). | Análisis continuo de frecuencia de ingredientes en despensas y recetas cocinadas. |
| **Semántica** | Colas de trabajo transaccionales con ACK manual y descarte a DLQ. | Log inmutable, particionado, persistente y reproducible (*replayable*). |
| **Retención** | Los mensajes se eliminan una vez confirmados (*ACK*). | Retención temporal configurable (`retention.ms = 7 días`) independiente del consumo. |
| **Consumo** | Múltiples consumidores compiten por los mensajes de una misma cola. | Múltiples *Consumer Groups* leen de forma independiente con sus propios *offsets*. |

> **Respuesta clave para la defensa:** RabbitMQ atiende los procesos operacionales que requieren confirmación estricta (dar de baja inventario, emitir ticket), mientras que Kafka recibe el torrente continuo de eventos para alimentar motores de agregación y dashboards analíticos sin sobrecargar la base de datos MySQL con consultas `COUNT(*)` o `GROUP BY` recurrentes.

---

## 2. Topología de Kafka en el Proyecto

La rúbrica exige la configuración explícita de **dos tópicos**:

```
+-----------------------------------------------------------------------------------+
|                                 APACHE KAFKA                                      |
+-----------------------------------------------------------------------------------+
  │
  ├── Tópico 1: analitica.despensa.ingredientes
  │     ├── Particiones: 2 | Factor de réplica: 1
  │     ├── Cleanup Policy: delete | Retención: 604800000 ms (7 días)
  │     ├── Productor: DespensaService (eventos de alta/modificación/baja de ingredientes)
  │     └── Consumidores:
  │           ├── Consumer Group 1: quehaypahacer-analitica-group  (calcula Top ingredientes)
  │           └── Consumer Group 2: quehaypahacer-auditoria-group  (auditoría y telemetría)
  │
  └── Tópico 2: analitica.cocina.ingredientes
        ├── Particiones: 2 | Factor de réplica: 1
        ├── Cleanup Policy: delete | Retención: 604800000 ms (7 días)
        ├── Productor: HistorialCocinaService (ingredientes consumidos al cocinar)
        └── Consumidores:
              ├── Consumer Group 1: quehaypahacer-analitica-group  (calcula Top ingredientes cocinados)
              └── Consumer Group 2: quehaypahacer-auditoria-group  (auditoría y telemetría)
```

---

## 3. Infraestructura Docker Compose

El archivo `docker-compose.yml` incluye la arquitectura completa:

1. **Zookeeper (`zookeeper:2181`)**: Coordinador del clúster de Kafka.
2. **Kafka Broker (`kafka:9092` / `kafka:29092`)**: Broker principal configurado con variables de entorno de red interna y externa.
3. **Kafka-UI (`http://localhost:8085`)**: Interfaz gráfica web para visualizar brokers, tópicos, mensajes, particiones y estados de los consumer groups.
4. **Clúster RabbitMQ (`rabbitmq1:5672` y `rabbitmq2:5673`)**: Mantiene la infraestructura intacta de la evaluación anterior.

### Comandos para levantar el entorno:

```powershell
# Levantar Zookeeper, Kafka y Kafka-UI
docker compose up -d zookeeper kafka kafka-ui

# Levantar clúster completo (RabbitMQ + Kafka + Backend + Frontend)
docker compose up -d --build
```

- **Panel Kafka-UI:** [http://localhost:8085](http://localhost:8085)
- **Panel RabbitMQ:** [http://localhost:15672](http://localhost:15672)

---

## 4. Estructura de Clases Implementadas (Backend)

```
cl.quehaypahacer.backend/
├── config/kafka/
│   ├── KafkaProperties.java                 # Mapeo tipado y validado de app.kafka.* en application.yml
│   └── KafkaConfig.java                     # Declaración de beans NewTopic, AdminClient y DefaultErrorHandler
├── messaging/kafka/
│   ├── event/
│   │   ├── IngredienteDespensaAnaliticaEvent.java  # DTO tipado para Tópico 1
│   │   └── IngredienteCocinaAnaliticaEvent.java    # DTO tipado para Tópico 2
│   ├── publisher/
│   │   ├── KafkaAnaliticaEventPublisher.java       # Interfaz desacoplada del publicador
│   │   └── impl/SpringKafkaAnaliticaEventPublisher.java # Envío con KafkaTemplate y clave de particionado
│   └── consumer/
│       ├── analitica/IngredienteAnaliticaConsumer.java  # Consumer Group: quehaypahacer-analitica-group
│       └── auditoria/KafkaAuditoriaConsumer.java        # Consumer Group: quehaypahacer-auditoria-group
├── admin/kafka/
│   ├── controller/
│   │   ├── KafkaAdminController.java        # Endpoints REST para gestión de tópicos y consumer groups
│   │   └── KafkaTestController.java         # Endpoints de prueba y simulación de fallas
│   ├── dto/
│   │   ├── KafkaTopicRequestDTO.java        # DTO de creación de tópicos
│   │   ├── KafkaTopicInfoDTO.java           # DTO de detalle/inspección de tópicos
│   │   └── KafkaConsumerGroupDTO.java       # DTO de inspección de grupos
│   ├── exception/
│   │   ├── KafkaTopicNotFoundException.java # Excepción 404
│   │   └── KafkaAdminExceptionHandler.java  # ControllerAdvice para errores de Kafka
│   └── service/
│       ├── KafkaAdminService.java           # Contrato de administración
│       └── impl/KafkaAdminServiceImpl.java  # Implementación nativa con AdminClient
├── service/
│   └── IngredienteAnaliticaService.java     # Motor de agregación en tiempo real (LongAdder / ConcurrentHashMap)
└── controller/
    └── AnaliticaController.java             # Endpoints públicos para consultar Top ingredientes
```

---

## 5. Endpoints Disponibles y Pruebas en Postman

Todos los endpoints han sido incorporados a la colección `Q-Hay-Pa-Hacer-Postman-Collection.json` en la carpeta **"Kafka - Streaming & Analítica (Evaluación 5)"**:

### A. Consultas Analíticas (Públicas)
- `GET /api/analitica/resumen`: Estadísticas consolidadas (total de eventos, top 5 despensa, top 5 cocina).
- `GET /api/analitica/ingredientes/mas-comunes?limite=10`: Ranking de ingredientes más presentes en las despensas.
- `GET /api/analitica/ingredientes/mas-cocinados?limite=10`: Ranking de ingredientes más consumidos al cocinar.

### B. Módulo Administrador de Kafka (Rol Admin - Azure AD)
- `GET /api/admin/kafka/topics`: Lista todos los tópicos del clúster con sus particiones y factor de réplica.
- `GET /api/admin/kafka/topics/{name}`: Detalle de particiones y parámetros de configuración (`retention.ms`, `cleanup.policy`).
- `POST /api/admin/kafka/topics`: Crea un nuevo tópico en caliente (`name`, `partitions`, `replicationFactor`, `retentionMs`, `cleanupPolicy`).
- `DELETE /api/admin/kafka/topics/{name}`: Elimina un tópico en caliente.
- `GET /api/admin/kafka/consumer-groups`: Lista los grupos de consumidores y su estado (`STABLE`, `EMPTY`, etc.).

### C. Pruebas y Resiliencia (Rol Admin)
- `POST /api/admin/kafka/test/evento-despensa?ingrediente=Palta`: Emite un evento al tópico de despensa.
- `POST /api/admin/kafka/test/evento-cocina?receta=Completos&ingrediente=Palta`: Emite un evento al tópico de cocina.
- `POST /api/admin/kafka/test/evento-invalido`: Envía un payload corrupto para demostrar que `DefaultErrorHandler` captura el error y lo registra en log sin tumbar el contenedor de escuchas.
- `POST /api/admin/kafka/test/reset-estadisticas`: Reinicia los acumuladores analíticos en memoria.

---

## 6. Verificación de Pauta (100% de la Rúbrica)

| Indicador | % | Implementación en el Proyecto |
|---|---|---|
| **Configuración del Cliente Kafka** | 12% | `application.yml` (`spring.kafka.*`), `KafkaConfig.java`, bootstrap-servers, serializers JSON, deserializers confiables. |
| **Interacción con los DOS tópicos** | 12% | `analitica.despensa.ingredientes` y `analitica.cocina.ingredientes` con DTOs estructurados. |
| **Microservicio Administrador (Operaciones)** | 6% | `KafkaAdminController` y `KafkaAdminServiceImpl` (crear, listar y mantener tópicos). |
| **Productores Kafka** | 14% | `SpringKafkaAnaliticaEventPublisher`, desacoplado en `DespensaService` y `HistorialCocinaService`. |
| **Consumidores Kafka** | 14% | `IngredienteAnaliticaConsumer` y `KafkaAuditoriaConsumer` con `@KafkaListener`, desacoplados de la lógica de dominio. |
| **Manejo de Errores** | 12% | `DefaultErrorHandler` con reintentos controlados (`FixedBackOff`), validación de DTOs y logging contextual. |
| **Parámetros de Tópicos** | 12% | `retention.ms=604800000` (7 días), `cleanup.policy=delete`, 2 particiones y 1 réplica declaradas como beans. |
| **Consumer Groups** | 12% | `quehaypahacer-analitica-group` (cálculo analítico) y `quehaypahacer-auditoria-group` (auditoría). |
| **Inspección de Infraestructura** | 6% | Endpoint `GET /api/admin/kafka/consumer-groups`, inspección en caliente con `AdminClient` y panel web Kafka-UI. |
| **Total** | **100%** | **Nota máxima (7.0)** |
