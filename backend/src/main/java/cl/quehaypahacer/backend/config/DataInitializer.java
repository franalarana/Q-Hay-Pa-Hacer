package cl.quehaypahacer.backend.config;

import cl.quehaypahacer.backend.model.Ingrediente;
import cl.quehaypahacer.backend.model.IngredienteReceta;
import cl.quehaypahacer.backend.model.Receta;
import cl.quehaypahacer.backend.model.UnidadMedida;
import cl.quehaypahacer.backend.repository.IngredienteRecetaRepository;
import cl.quehaypahacer.backend.repository.IngredienteRepository;
import cl.quehaypahacer.backend.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final IngredienteRepository ingredienteRepository;
    private final RecetaRepository recetaRepository;
    private final IngredienteRecetaRepository ingredienteRecetaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (ingredienteRepository.count() == 0) {
            log.info("Cargando catálogo inicial de ingredientes y recetas de prueba...");
            Map<String, Ingrediente> ings = inicializarIngredientes();
            inicializarRecetas(ings);
            log.info("Datos iniciales cargados con éxito ({} ingredientes, {} recetas).",
                    ingredienteRepository.count(), recetaRepository.count());
        }
    }

    private Map<String, Ingrediente> inicializarIngredientes() {
        Map<String, Ingrediente> map = new HashMap<>();

        List<Ingrediente> lista = List.of(
                Ingrediente.builder().nombre("Huevos").categoria("Huevos y Lácteos").unidadBase(UnidadMedida.UNIDAD).build(),
                Ingrediente.builder().nombre("Leche").categoria("Huevos y Lácteos").unidadBase(UnidadMedida.MILILITROS).build(),
                Ingrediente.builder().nombre("Queso rallado").categoria("Huevos y Lácteos").unidadBase(UnidadMedida.GRAMOS).build(),
                Ingrediente.builder().nombre("Mantequilla").categoria("Huevos y Lácteos").unidadBase(UnidadMedida.GRAMOS).build(),
                
                Ingrediente.builder().nombre("Arroz").categoria("Abarrotes").unidadBase(UnidadMedida.GRAMOS).build(),
                Ingrediente.builder().nombre("Harina de trigo").categoria("Abarrotes").unidadBase(UnidadMedida.GRAMOS).build(),
                Ingrediente.builder().nombre("Fideos / Pasta").categoria("Abarrotes").unidadBase(UnidadMedida.GRAMOS).build(),
                Ingrediente.builder().nombre("Salsa de tomate").categoria("Abarrotes").unidadBase(UnidadMedida.GRAMOS).build(),
                Ingrediente.builder().nombre("Azúcar").categoria("Abarrotes").unidadBase(UnidadMedida.GRAMOS).build(),

                Ingrediente.builder().nombre("Aceite").categoria("Aceites y Condimentos").unidadBase(UnidadMedida.MILILITROS).build(),
                Ingrediente.builder().nombre("Sal").categoria("Aceites y Condimentos").unidadBase(UnidadMedida.GRAMOS).build(),
                Ingrediente.builder().nombre("Pimienta").categoria("Aceites y Condimentos").unidadBase(UnidadMedida.GRAMOS).build(),

                Ingrediente.builder().nombre("Cebolla").categoria("Verduras").unidadBase(UnidadMedida.UNIDAD).build(),
                Ingrediente.builder().nombre("Tomate").categoria("Verduras").unidadBase(UnidadMedida.UNIDAD).build(),
                Ingrediente.builder().nombre("Papas").categoria("Verduras").unidadBase(UnidadMedida.UNIDAD).build(),
                Ingrediente.builder().nombre("Ajo").categoria("Verduras").unidadBase(UnidadMedida.UNIDAD).build(),

                Ingrediente.builder().nombre("Carne molida").categoria("Carnes").unidadBase(UnidadMedida.GRAMOS).build(),
                Ingrediente.builder().nombre("Pechuga de pollo").categoria("Carnes").unidadBase(UnidadMedida.GRAMOS).build()
        );

        for (Ingrediente ing : lista) {
            Ingrediente guardado = ingredienteRepository.save(ing);
            map.put(guardado.getNombre(), guardado);
        }
        return map;
    }

    private void inicializarRecetas(Map<String, Ingrediente> ings) {
        // Receta 1: Arroz con Huevo Frito (Caso Verde típico para pocos ingredientes)
        Receta r1 = Receta.builder()
                .titulo("Arroz con Huevo Frito")
                .descripcion("El clásico salvador chileno: rápido, delicioso y reconfortante.")
                .instrucciones("1. Cocina el arroz en agua hirviendo con sal durante 20 minutos.\n2. Fríe dos huevos en un sartén con aceite caliente.\n3. Sirve el arroz caliente y monta los huevos fritos encima.")
                .tiempoMinutos(15)
                .porciones(1)
                .dificultad("Fácil")
                .imagenUrl("https://images.unsplash.com/photo-1598515214211-89d3c73ae83b?w=600&auto=format&fit=crop&q=60")
                .build();
        r1 = recetaRepository.save(r1);

        agregarIngrediente(r1, ings.get("Arroz"), 150.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r1, ings.get("Huevos"), 2.0, UnidadMedida.UNIDAD);
        agregarIngrediente(r1, ings.get("Aceite"), 15.0, UnidadMedida.MILILITROS);
        agregarIngrediente(r1, ings.get("Sal"), 5.0, UnidadMedida.GRAMOS);

        // Receta 2: Panqueques Caseros (Caso Amarillo típico si falta leche o harina)
        Receta r2 = Receta.builder()
                .titulo("Panqueques Caseros")
                .descripcion("Panqueques esponjosos ideales para el desayuno o la once.")
                .instrucciones("1. Bate los huevos con la leche y el azúcar.\n2. Agrega la harina tamizada y mezcla hasta no tener grumos.\n3. Vierte un cucharón en un sartén enmantequillado y dora por ambos lados.")
                .tiempoMinutos(20)
                .porciones(4)
                .dificultad("Fácil")
                .imagenUrl("https://images.unsplash.com/photo-1528207776546-365bb710ee93?w=600&auto=format&fit=crop&q=60")
                .build();
        r2 = recetaRepository.save(r2);

        agregarIngrediente(r2, ings.get("Harina de trigo"), 200.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r2, ings.get("Leche"), 300.0, UnidadMedida.MILILITROS);
        agregarIngrediente(r2, ings.get("Huevos"), 2.0, UnidadMedida.UNIDAD);
        agregarIngrediente(r2, ings.get("Mantequilla"), 20.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r2, ings.get("Azúcar"), 15.0, UnidadMedida.GRAMOS);

        // Receta 3: Pasta con Salsa de Tomate y Queso
        Receta r3 = Receta.builder()
                .titulo("Pasta con Salsa de Tomate y Queso")
                .descripcion("Fideos al dente con salsa de tomate casera y queso rallado fundido.")
                .instrucciones("1. Cocina la pasta en agua hirviendo con sal hasta que esté al dente.\n2. Sofríe la cebolla picada en aceite y añade la salsa de tomate.\n3. Mezcla los fideos con la salsa y corona con queso rallado.")
                .tiempoMinutos(25)
                .porciones(2)
                .dificultad("Fácil")
                .imagenUrl("https://images.unsplash.com/photo-1621996346565-e3d5d6281691?w=600&auto=format&fit=crop&q=60")
                .build();
        r3 = recetaRepository.save(r3);

        agregarIngrediente(r3, ings.get("Fideos / Pasta"), 200.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r3, ings.get("Salsa de tomate"), 180.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r3, ings.get("Queso rallado"), 40.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r3, ings.get("Cebolla"), 1.0, UnidadMedida.UNIDAD);
        agregarIngrediente(r3, ings.get("Aceite"), 10.0, UnidadMedida.MILILITROS);

        // Receta 4: Tortilla de Papas y Cebolla
        Receta r4 = Receta.builder()
                .titulo("Tortilla de Papas y Cebolla")
                .descripcion("La infalible tortilla española suave por dentro y dorada por fuera.")
                .instrucciones("1. Corta las papas y cebollas en cubos y sofríelas a fuego medio hasta que estén tiernas.\n2. Bate los huevos con sal e integra las papas escurridas.\n3. Cocina la mezcla en sartén caliente dando vuelta con un plato.")
                .tiempoMinutos(30)
                .porciones(3)
                .dificultad("Media")
                .imagenUrl("https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&auto=format&fit=crop&q=60")
                .build();
        r4 = recetaRepository.save(r4);

        agregarIngrediente(r4, ings.get("Papas"), 3.0, UnidadMedida.UNIDAD);
        agregarIngrediente(r4, ings.get("Huevos"), 4.0, UnidadMedida.UNIDAD);
        agregarIngrediente(r4, ings.get("Cebolla"), 1.0, UnidadMedida.UNIDAD);
        agregarIngrediente(r4, ings.get("Aceite"), 20.0, UnidadMedida.MILILITROS);
        agregarIngrediente(r4, ings.get("Sal"), 5.0, UnidadMedida.GRAMOS);

        // Receta 5: Lasaña de Carne Bolognesa (Caso Rojo típico si el usuario tiene despensa básica)
        Receta r5 = Receta.builder()
                .titulo("Lasaña de Carne Bolognesa")
                .descripcion("Capas de pasta rellenas de carne molida en salsa bolognesa y queso gratinado.")
                .instrucciones("1. Prepara la salsa bolognesa sofriendo ajo, cebolla, carne molida y salsa de tomate.\n2. Arma las capas intercalando pasta, bolognesa y abundante queso.\n3. Hornea a 180°C durante 30 minutos hasta gratinar.")
                .tiempoMinutos(60)
                .porciones(4)
                .dificultad("Difícil")
                .imagenUrl("https://images.unsplash.com/photo-1574894709920-11b28e7367e3?w=600&auto=format&fit=crop&q=60")
                .build();
        r5 = recetaRepository.save(r5);

        agregarIngrediente(r5, ings.get("Carne molida"), 400.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r5, ings.get("Fideos / Pasta"), 250.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r5, ings.get("Salsa de tomate"), 300.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r5, ings.get("Queso rallado"), 150.0, UnidadMedida.GRAMOS);
        agregarIngrediente(r5, ings.get("Cebolla"), 2.0, UnidadMedida.UNIDAD);
        agregarIngrediente(r5, ings.get("Ajo"), 2.0, UnidadMedida.UNIDAD);
        agregarIngrediente(r5, ings.get("Aceite"), 20.0, UnidadMedida.MILILITROS);
    }

    private void agregarIngrediente(Receta receta, Ingrediente ingrediente, Double cantidad, UnidadMedida unidad) {
        if (ingrediente == null) return;
        IngredienteReceta ir = IngredienteReceta.builder()
                .receta(receta)
                .ingrediente(ingrediente)
                .cantidadRequerida(cantidad)
                .unidad(unidad)
                .opcional(false)
                .build();
        ingredienteRecetaRepository.save(ir);
    }
}
