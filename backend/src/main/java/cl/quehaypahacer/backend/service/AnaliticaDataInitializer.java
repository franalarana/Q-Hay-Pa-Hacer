package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.repository.HistorialCocinaRepository;
import cl.quehaypahacer.backend.repository.IngredienteUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Inicializa las estadísticas en memoria de IngredienteAnaliticaService con los datos
 * históricos existentes en la base de datos (despensa e historial de cocina), asegurando
 * métricas en tiempo real inmediatas incluso si Kafka o el broker se reinician.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AnaliticaDataInitializer {

    private final IngredienteAnaliticaService analiticaService;
    private final HistorialCocinaRepository historialCocinaRepository;
    private final IngredienteUsuarioRepository ingredienteUsuarioRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void inicializarMetricasDesdeBaseDeDatos() {
        try {
            List<Object[]> despensa = ingredienteUsuarioRepository.findConteoIngredientesDespensa();
            if (despensa != null) {
                for (Object[] row : despensa) {
                    if (row != null && row.length >= 2 && row[0] != null && row[1] != null) {
                        String nombre = row[0].toString();
                        long count = ((Number) row[1]).longValue();
                        for (int i = 0; i < count; i++) {
                            analiticaService.registrarIngredienteDespensa(nombre);
                        }
                    }
                }
            }

            List<Object[]> cocina = historialCocinaRepository.findConteoIngredientesCocinados();
            if (cocina != null) {
                for (Object[] row : cocina) {
                    if (row != null && row.length >= 2 && row[0] != null && row[1] != null) {
                        String nombre = row[0].toString();
                        long count = ((Number) row[1]).longValue();
                        for (int i = 0; i < count; i++) {
                            analiticaService.registrarIngredienteCocina(nombre);
                        }
                    }
                }
            }

            log.info("[ANALITICA INITIALIZER] Métricas inicializadas desde BD: {} tipos en despensa, {} tipos en cocina",
                    despensa != null ? despensa.size() : 0,
                    cocina != null ? cocina.size() : 0);
        } catch (Exception e) {
            log.warn("[ANALITICA INITIALIZER] No se pudieron precargar métricas desde BD: {}", e.getMessage());
        }
    }
}
