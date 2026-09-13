package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.dto.CrearIngredienteRequest;
import cl.quehaypahacer.backend.model.Ingrediente;
import cl.quehaypahacer.backend.service.IngredienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredientes")
@RequiredArgsConstructor
public class IngredienteController {

    private final IngredienteService ingredienteService;

    @GetMapping
    public List<Ingrediente> listarTodos() {
        return ingredienteService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<Ingrediente> crearPersonalizado(@Valid @RequestBody CrearIngredienteRequest request) {
        Ingrediente creado = ingredienteService.crearPersonalizado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
