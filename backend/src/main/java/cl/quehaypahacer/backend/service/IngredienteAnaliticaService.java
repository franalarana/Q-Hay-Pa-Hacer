package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.analitica.IngredienteRankingDTO;
import cl.quehaypahacer.backend.dto.analitica.ResumenAnaliticaDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * Servicio en memoria de agregación analítica en tiempo real alimentado por eventos de Kafka.
 * Emplea estructuras concurrentes de alto rendimiento (LongAdder, ConcurrentHashMap).
 */
@Service
public class IngredienteAnaliticaService {

    private final ConcurrentHashMap<String, LongAdder> conteoDespensa = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongAdder> conteoCocina = new ConcurrentHashMap<>();
    private final LongAdder totalDespensa = new LongAdder();
    private final LongAdder totalCocina = new LongAdder();

    public void registrarIngredienteDespensa(String ingrediente) {
        if (ingrediente == null || ingrediente.isBlank()) return;
        String normalizado = normalizar(ingrediente);
        conteoDespensa.computeIfAbsent(normalizado, k -> new LongAdder()).increment();
        totalDespensa.increment();
    }

    public void registrarIngredienteCocina(String ingrediente) {
        if (ingrediente == null || ingrediente.isBlank()) return;
        String normalizado = normalizar(ingrediente);
        conteoCocina.computeIfAbsent(normalizado, k -> new LongAdder()).increment();
        totalCocina.increment();
    }

    public List<IngredienteRankingDTO> obtenerTopIngredientesDespensa(int limite) {
        long total = totalDespensa.sum();
        return calcularRanking(conteoDespensa, total, limite);
    }

    public List<IngredienteRankingDTO> obtenerTopIngredientesCocina(int limite) {
        long total = totalCocina.sum();
        return calcularRanking(conteoCocina, total, limite);
    }

    public ResumenAnaliticaDTO obtenerResumen() {
        long td = totalDespensa.sum();
        long tc = totalCocina.sum();
        return new ResumenAnaliticaDTO(
                td + tc,
                td,
                tc,
                obtenerTopIngredientesDespensa(5),
                obtenerTopIngredientesCocina(5)
        );
    }

    public void reiniciarEstadisticas() {
        conteoDespensa.clear();
        conteoCocina.clear();
        totalDespensa.reset();
        totalCocina.reset();
    }

    private List<IngredienteRankingDTO> calcularRanking(Map<String, LongAdder> conteo, long total, int limite) {
        return conteo.entrySet().stream()
                .sorted(Comparator.comparingLong((Map.Entry<String, LongAdder> e) -> e.getValue().sum()).reversed())
                .limit(limite)
                .map(e -> {
                    long cant = e.getValue().sum();
                    double pct = total > 0 ? (double) cant / total * 100.0 : 0.0;
                    return new IngredienteRankingDTO(capitalizar(e.getKey()), cant, Math.round(pct * 100.0) / 100.0);
                })
                .collect(Collectors.toList());
    }

    private String normalizar(String texto) {
        return texto.trim().toLowerCase();
    }

    private String capitalizar(String texto) {
        if (texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
