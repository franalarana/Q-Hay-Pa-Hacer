package cl.quehaypahacer.backend.dto;

public enum EstadoReceta {
    VERDE,     // 100% de los ingredientes listos para cocinar
    AMARILLO,  // Tienes ingredientes pero faltan cantidades
    ROJO       // Faltan ingredientes principales
}
