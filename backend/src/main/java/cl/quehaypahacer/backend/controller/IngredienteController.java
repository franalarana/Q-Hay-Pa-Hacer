package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.model.Ingrediente;
import cl.quehaypahacer.backend.service.IngredienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
