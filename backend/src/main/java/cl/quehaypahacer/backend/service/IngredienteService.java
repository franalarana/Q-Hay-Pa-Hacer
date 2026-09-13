package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.CrearIngredienteRequest;
import cl.quehaypahacer.backend.model.Ingrediente;
import cl.quehaypahacer.backend.repository.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    @Transactional(readOnly = true)
    public List<Ingrediente> listarTodos() {
        return ingredienteRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Ingrediente obtenerPorId(Long id) {
        return ingredienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado con id: " + id));
    }

    @Transactional
    public Ingrediente crearPersonalizado(CrearIngredienteRequest req) {
        return ingredienteRepository.findByNombreIgnoreCase(req.getNombre().trim())
                .orElseGet(() -> ingredienteRepository.save(
                        Ingrediente.builder()
                                .nombre(req.getNombre().trim())
                                .categoria(req.getCategoria().trim())
                                .unidadBase(req.getUnidadBase())
                                .build()
                ));
    }
}
