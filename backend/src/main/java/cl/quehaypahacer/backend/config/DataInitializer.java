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
                                Ingrediente.builder().nombre("Huevos").categoria("Huevos y Lácteos")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Leche").categoria("Huevos y Lácteos")
                                                .unidadBase(UnidadMedida.MILILITROS).build(),
                                Ingrediente.builder().nombre("Queso rallado").categoria("Huevos y Lácteos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Mantequilla").categoria("Huevos y Lácteos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),

                                Ingrediente.builder().nombre("Arroz").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Harina de trigo").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Fideos / Pasta").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Salsa de tomate").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Azúcar").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),

                                Ingrediente.builder().nombre("Aceite").categoria("Aceites y Condimentos")
                                                .unidadBase(UnidadMedida.MILILITROS).build(),
                                Ingrediente.builder().nombre("Sal").categoria("Aceites y Condimentos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Pimienta").categoria("Aceites y Condimentos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),

                                Ingrediente.builder().nombre("Cebolla").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Tomate").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Papas").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Ajo").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),

                                Ingrediente.builder().nombre("Carne molida").categoria("Carnes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Pechuga de pollo").categoria("Carnes")
                                                .unidadBase(UnidadMedida.GRAMOS).build());

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
                                .instrucciones("1. Pon a hervir agua en una olla con una pizca generosa de sal.\n2. Añade el arroz, revuelve una vez y cocina a fuego medio-bajo con tapa durante 15-18 minutos, sin destapar antes de tiempo.\n3. Retira del fuego y deja reposar 5 minutos con la tapa puesta para que termine de absorber el vapor.\n4. Calienta el aceite en un sartén a fuego medio-alto hasta que humee levemente.\n5. Rompe los huevos directo en el sartén, evitando que se peguen entre sí, y cocina 2-3 minutos hasta que la clara esté firme y la yema siga líquida.\n6. Sirve el arroz esponjado en un plato hondo y corona con los huevos fritos recién hechos.\n7. Sazona a gusto con más sal o un chorrito de salsa de tu preferencia antes de servir.")
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
                                .instrucciones("1. En un bowl, bate los huevos junto con la leche y el azúcar hasta que la mezcla esté homogénea.\n2. Tamiza la harina para evitar grumos y agrégala en dos tandas, mezclando suavemente con espátula entre cada una.\n3. Deja reposar la masa 5-10 minutos a temperatura ambiente para que se hidrate bien.\n4. Derrite un poco de mantequilla en un sartén antiadherente a fuego medio-bajo.\n5. Vierte un cucharón de masa por panqueque y cocina 1-2 minutos hasta que se formen burbujas y los bordes se despeguen.\n6. Da vuelta con una espátula y cocina 1 minuto más por el otro lado, hasta dorar.\n7. Repite con el resto de la masa, engrasando el sartén levemente entre tandas.\n8. Apila los panqueques y sirve con miel, mermelada o fruta al gusto.")
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
                                .instrucciones("1. Pon a hervir abundante agua con sal en una olla grande.\n2. Agrega la pasta y cocina según el tiempo indicado en el envase, removiendo de vez en cuando para que no se pegue.\n3. Mientras la pasta cocina, pica finamente la cebolla.\n4. En un sartén aparte, calienta el aceite a fuego medio y sofríe la cebolla 3-4 minutos hasta que esté transparente y suave.\n5. Incorpora la salsa de tomate al sartén y cocina 5 minutos más a fuego bajo, revolviendo para que no se pegue.\n6. Escurre la pasta reservando un poco del agua de cocción, y mézclala directamente en el sartén con la salsa.\n7. Si la salsa queda muy espesa, agrega un poco del agua reservada hasta lograr la consistencia deseada.\n8. Sirve caliente y espolvorea generosamente con queso rallado antes de comer.")
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
                                .instrucciones("1. Pela las papas y córtalas en cubos pequeños o láminas finas; pica la cebolla en tiras delgadas.\n2. Calienta el aceite en un sartén a fuego medio y sofríe las papas y la cebolla juntas 12-15 minutos, revolviendo ocasionalmente, hasta que las papas estén tiernas pero no doradas.\n3. Retira del sartén y escurre el exceso de aceite en un colador.\n4. En un bowl grande, bate los huevos con una pizca de sal hasta integrar bien.\n5. Incorpora las papas y cebolla ya tibias a los huevos batidos y mezcla con cuidado para no romperlas demasiado.\n6. Vierte la mezcla en el mismo sartén con un poco de aceite a fuego medio-bajo, presiona suavemente y cocina 4-5 minutos hasta que los bordes cuajen.\n7. Cubre el sartén con un plato grande, dale vuelta con firmeza para voltear la tortilla y devuélvela al sartén por el otro lado.\n8. Cocina 3-4 minutos más hasta que esté dorada por fuera y jugosa por dentro. Deja reposar 2 minutos antes de cortar.")
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

                // Receta 5: Lasaña de Carne Bolognesa (Caso Rojo típico si el usuario tiene
                // despensa básica)
                Receta r5 = Receta.builder()
                                .titulo("Lasaña de Carne Bolognesa")
                                .descripcion("Capas de pasta rellenas de carne molida en salsa bolognesa y queso gratinado.")
                                .instrucciones("1. Pica finamente la cebolla y el ajo. Calienta el aceite en una olla amplia a fuego medio y sofríelos 3-4 minutos hasta que estén fragrantes.\n2. Agrega la carne molida y cocina 6-8 minutos, desmenuzando con una cuchara, hasta que pierda el color rosado.\n3. Incorpora la salsa de tomate, sazona con sal y pimienta, y cocina a fuego bajo 15-20 minutos, revolviendo de vez en cuando, hasta que la salsa espese.\n4. Si usas láminas de pasta secas, hidrátalas unos minutos en agua caliente según las instrucciones del envase.\n5. Precalienta el horno a 180°C.\n6. En una fuente para horno, coloca una capa fina de bolognesa en el fondo, luego una capa de pasta, más bolognesa y una capa de queso rallado.\n7. Repite el proceso de capas hasta terminar los ingredientes, dejando queso abundante en la superficie final.\n8. Cubre con papel aluminio y hornea 20 minutos; luego retira el papel y hornea 10 minutos más hasta que el queso esté dorado y gratinado.\n9. Deja reposar 5-10 minutos antes de cortar y servir, para que las capas se asienten.")
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
                if (ingrediente == null)
                        return;
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
