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
                        inicializarRecetasExtra(ings);
                        inicializarRecetasJovenes(ings);
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
                                                .unidadBase(UnidadMedida.GRAMOS).build(),

                                // cambios aqui: ingredientes agregados para las 50 recetas nuevas (chilenas y comunes)
                                Ingrediente.builder().nombre("Zapallo").categoria("Verduras")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Zanahoria").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Choclo").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Apio").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Pimentón").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Palta").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Limón").categoria("Verduras")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Cilantro").categoria("Verduras")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Perejil").categoria("Verduras")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Manzana").categoria("Frutas")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),

                                Ingrediente.builder().nombre("Porotos").categoria("Legumbres")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Garbanzos").categoria("Legumbres")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Lentejas").categoria("Legumbres")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),

                                Ingrediente.builder().nombre("Trutro de pollo").categoria("Carnes")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Posta de vacuno").categoria("Carnes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Costillar de cerdo").categoria("Carnes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Carne de cordero").categoria("Carnes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Chorizo").categoria("Carnes")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Longaniza").categoria("Carnes")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Jamón").categoria("Carnes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),

                                Ingrediente.builder().nombre("Congrio").categoria("Pescados y Mariscos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Machas").categoria("Pescados y Mariscos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Atún en lata").categoria("Pescados y Mariscos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Mariscos surtidos").categoria("Pescados y Mariscos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),

                                Ingrediente.builder().nombre("Vino blanco").categoria("Aceites y Condimentos")
                                                .unidadBase(UnidadMedida.MILILITROS).build(),
                                Ingrediente.builder().nombre("Vino tinto").categoria("Aceites y Condimentos")
                                                .unidadBase(UnidadMedida.MILILITROS).build(),
                                Ingrediente.builder().nombre("Comino").categoria("Aceites y Condimentos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Orégano").categoria("Aceites y Condimentos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Canela").categoria("Aceites y Condimentos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Mayonesa").categoria("Aceites y Condimentos")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),

                                Ingrediente.builder().nombre("Caldo de verduras").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Pan de molde").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Marraqueta").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Levadura").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Manjar").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Maicena").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Aceitunas").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Pasas").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),

                                Ingrediente.builder().nombre("Crema de leche").categoria("Huevos y Lácteos")
                                                .unidadBase(UnidadMedida.MILILITROS).build(),

                                // cambios aqui: ingredientes agregados para las 30 recetas de vida independiente/estudiante
                                Ingrediente.builder().nombre("Vienesas").categoria("Carnes")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Mermelada").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build(),
                                Ingrediente.builder().nombre("Yogurt").categoria("Huevos y Lácteos")
                                                .unidadBase(UnidadMedida.MILILITROS).build(),
                                Ingrediente.builder().nombre("Plátano").categoria("Frutas")
                                                .unidadBase(UnidadMedida.UNIDAD).build(),
                                Ingrediente.builder().nombre("Avena").categoria("Abarrotes")
                                                .unidadBase(UnidadMedida.GRAMOS).build());
                                // hasta aqui
                                // hasta aqui

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

        // cambios aqui: helper compacto para no repetir Receta.builder()+save() en cada una de las 50 recetas nuevas
        private record Ing(String nombre, double cantidad, UnidadMedida unidad) {
        }

        private static Ing ing(String nombre, double cantidad, UnidadMedida unidad) {
                return new Ing(nombre, cantidad, unidad);
        }

        private void receta(Map<String, Ingrediente> ings, String titulo, String descripcion, String instrucciones,
                        int tiempoMinutos, int porciones, String dificultad, Ing... usos) {
                Receta r = Receta.builder()
                                .titulo(titulo)
                                .descripcion(descripcion)
                                .instrucciones(instrucciones)
                                .tiempoMinutos(tiempoMinutos)
                                .porciones(porciones)
                                .dificultad(dificultad)
                                .build();
                r = recetaRepository.save(r);
                for (Ing u : usos) {
                        agregarIngrediente(r, ings.get(u.nombre()), u.cantidad(), u.unidad());
                }
        }

        /**
         * 50 recetas chilenas y de uso común, con variedad de complejidad
         * (Fácil/Media/Difícil), agregadas a pedido para ampliar el catálogo base.
         */
        private void inicializarRecetasExtra(Map<String, Ingrediente> ings) {

                // ---------- FÁCIL (18) ----------

                receta(ings, "Ensalada Chilena",
                                "La clásica ensalada de tomate y cebolla, infaltable en cualquier almuerzo.",
                                "1. Corta los tomates en cubos o gajos según prefieras.\n2. Corta la cebolla en pluma bien fina.\n3. Si la cebolla queda muy fuerte, sumérgela en agua fría con sal 5 minutos y escúrrela.\n4. Mezcla el tomate con la cebolla en un bowl.\n5. Condimenta con aceite y sal a gusto justo antes de servir.",
                                10, 4, "Fácil",
                                ing("Tomate", 3, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 20, UnidadMedida.MILILITROS),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Pan con Palta",
                                "Marraqueta crujiente con palta molida, ideal para el desayuno o la once.",
                                "1. Corta la marraqueta por la mitad y tuéstala levemente si quieres.\n2. Pela la palta y macháchala con un tenedor en un bowl.\n3. Agrega sal y unas gotas de limón a la palta y mezcla bien.\n4. Unta generosamente la palta sobre el pan.\n5. Sirve de inmediato para que la palta no se oxide.",
                                5, 2, "Fácil",
                                ing("Marraqueta", 2, UnidadMedida.UNIDAD),
                                ing("Palta", 1, UnidadMedida.UNIDAD),
                                ing("Sal", 2, UnidadMedida.GRAMOS),
                                ing("Limón", 1, UnidadMedida.UNIDAD));

                receta(ings, "Choripán",
                                "Longaniza a la plancha en marraqueta, con tomate y cebolla.",
                                "1. Cocina la longaniza en un sartén o parrilla a fuego medio, dando vueltas hasta que esté dorada por fuera y cocida por dentro (10-12 minutos).\n2. Mientras se cocina, pica el tomate y la cebolla en cubos pequeños.\n3. Corta la marraqueta por la mitad.\n4. Coloca la longaniza dentro del pan.\n5. Agrega el tomate y la cebolla picados por encima.\n6. Sirve caliente, con salsas a gusto.",
                                15, 2, "Fácil",
                                ing("Longaniza", 2, UnidadMedida.UNIDAD),
                                ing("Marraqueta", 2, UnidadMedida.UNIDAD),
                                ing("Tomate", 1, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD));

                receta(ings, "Huevos a la Copa",
                                "Huevos pasados por agua, suaves y cremosos, servidos en su cáscara.",
                                "1. Pon agua a hervir en una olla pequeña.\n2. Con cuidado, sumerge los huevos (con cáscara) en el agua hirviendo.\n3. Cocina exactamente 3-4 minutos para que la clara cuaje y la yema quede líquida.\n4. Retira los huevos con una cuchara y colócalos en huevera o taza.\n5. Corta la punta superior de la cáscara y sazona con sal antes de comer con cucharita.",
                                8, 2, "Fácil",
                                ing("Huevos", 4, UnidadMedida.UNIDAD),
                                ing("Sal", 2, UnidadMedida.GRAMOS));

                receta(ings, "Arroz Graneado",
                                "Arroz blanco suelto y esponjoso, base de muchos platos chilenos.",
                                "1. Calienta el aceite en una olla a fuego medio y sofríe el ajo picado 1 minuto.\n2. Agrega el arroz y revuelve 1-2 minutos para que se impregne del aceite.\n3. Añade el doble de agua que de arroz y sal a gusto.\n4. Cocina tapado a fuego bajo 15-18 minutos sin destapar.\n5. Retira del fuego y deja reposar 5 minutos antes de destapar y separar los granos con un tenedor.",
                                20, 4, "Fácil",
                                ing("Arroz", 300, UnidadMedida.GRAMOS),
                                ing("Aceite", 15, UnidadMedida.MILILITROS),
                                ing("Ajo", 1, UnidadMedida.UNIDAD),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Revuelto de Verduras con Huevo",
                                "Huevos revueltos con cebolla, tomate y pimentón salteados.",
                                "1. Pica la cebolla, el tomate y el pimentón en cubos pequeños.\n2. Calienta el aceite en un sartén y sofríe la cebolla y el pimentón 3-4 minutos.\n3. Agrega el tomate y cocina 2 minutos más.\n4. Bate los huevos aparte con una pizca de sal y viértelos sobre las verduras.\n5. Revuelve constantemente a fuego medio-bajo hasta que el huevo cuaje.\n6. Sirve de inmediato, recién hecho.",
                                15, 2, "Fácil",
                                ing("Huevos", 4, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Tomate", 1, UnidadMedida.UNIDAD),
                                ing("Pimentón", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 10, UnidadMedida.MILILITROS));

                receta(ings, "Sopaipillas",
                                "Las clásicas tortillas fritas de zapallo, perfectas para un día lluvioso.",
                                "1. Cocina el zapallo al vapor o hervido hasta que esté blando, luego hazlo puré.\n2. Mezcla el puré de zapallo tibio con la harina y una pizca de sal hasta formar una masa suave.\n3. Amasa brevemente sobre una superficie enharinada y estira con uslero hasta dejarla fina.\n4. Corta círculos con un vaso o molde y pincha cada uno con un tenedor al centro.\n5. Calienta abundante aceite en una olla a fuego medio-alto.\n6. Fríe las sopaipillas de a pocas, dándolas vuelta, hasta que estén doradas por ambos lados (2-3 minutos).\n7. Retira y escurre sobre papel absorbente antes de servir.",
                                30, 6, "Fácil",
                                ing("Zapallo", 300, UnidadMedida.GRAMOS),
                                ing("Harina de trigo", 400, UnidadMedida.GRAMOS),
                                ing("Aceite", 500, UnidadMedida.MILILITROS),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Ensalada de Papas con Mayonesa",
                                "Papas cocidas en cubos con mayonesa y cebolla, ideal como acompañamiento.",
                                "1. Pela las papas y córtalas en cubos parejos.\n2. Cocínalas en agua con sal 15-18 minutos hasta que estén blandas pero firmes.\n3. Escurre y deja enfriar completamente.\n4. Pica la cebolla bien fina y mézclala con las papas frías.\n5. Agrega la mayonesa y mezcla con cuidado para no deshacer las papas.\n6. Sazona con sal a gusto y refrigera antes de servir.",
                                25, 4, "Fácil",
                                ing("Papas", 4, UnidadMedida.UNIDAD),
                                ing("Mayonesa", 150, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Tostadas con Palta y Huevo",
                                "Pan de molde tostado con palta y huevo frito encima.",
                                "1. Tuesta las rebanadas de pan de molde hasta que estén doradas.\n2. Macháchala palta con un tenedor y sazona con sal.\n3. Unta la palta sobre las tostadas.\n4. Fríe los huevos en un sartén con un poco de aceite hasta que la clara esté firme.\n5. Coloca un huevo frito sobre cada tostada y sirve de inmediato.",
                                10, 2, "Fácil",
                                ing("Pan de molde", 4, UnidadMedida.UNIDAD),
                                ing("Palta", 1, UnidadMedida.UNIDAD),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Sal", 2, UnidadMedida.GRAMOS));

                receta(ings, "Fideos con Mantequilla y Queso",
                                "La receta más simple y rápida para un antojo de pasta.",
                                "1. Cocina los fideos en agua hirviendo con sal según el tiempo indicado en el envase.\n2. Escurre reservando un poco del agua de cocción.\n3. Mezcla los fideos calientes con la mantequilla hasta que se derrita por completo.\n4. Agrega el queso rallado y mezcla nuevamente, sumando un poco del agua reservada si queda muy seco.\n5. Sirve de inmediato bien caliente.",
                                15, 3, "Fácil",
                                ing("Fideos / Pasta", 250, UnidadMedida.GRAMOS),
                                ing("Mantequilla", 30, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 60, UnidadMedida.GRAMOS),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Sándwich de Jamón y Queso Caliente",
                                "Pan de molde tostado con jamón y queso derretido.",
                                "1. Unta ligeramente mantequilla en un lado de cada rebanada de pan.\n2. Arma el sándwich con jamón y queso rallado entre dos rebanadas, con la mantequilla hacia afuera.\n3. Cocina en un sartén a fuego medio-bajo 2-3 minutos por lado.\n4. Presiona levemente con una espátula para que el queso se derrita bien.\n5. Corta por la mitad y sirve caliente.",
                                10, 2, "Fácil",
                                ing("Pan de molde", 4, UnidadMedida.UNIDAD),
                                ing("Jamón", 80, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 60, UnidadMedida.GRAMOS),
                                ing("Mantequilla", 15, UnidadMedida.GRAMOS));

                receta(ings, "Ensalada Rusa",
                                "Papas y zanahorias cocidas en cubos, con huevo y mayonesa.",
                                "1. Pela y corta en cubos pequeños las papas y zanahorias.\n2. Cocínalas juntas en agua con sal 12-15 minutos hasta que estén blandas.\n3. Cocina los huevos duros aparte 10 minutos, enfríalos y pícalos en cubos.\n4. Escurre y enfría las papas y zanahorias.\n5. Mezcla todo con la mayonesa hasta integrar bien.\n6. Sazona con sal y refrigera antes de servir.",
                                30, 4, "Fácil",
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Zanahoria", 2, UnidadMedida.UNIDAD),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Mayonesa", 120, UnidadMedida.GRAMOS));

                receta(ings, "Puré de Papas",
                                "Puré cremoso, el acompañamiento infaltable de todo almuerzo.",
                                "1. Pela las papas y córtalas en trozos parejos.\n2. Cocínalas en agua con sal 15-18 minutos hasta que estén muy blandas.\n3. Escurre bien y machácalas con un pisapapas.\n4. Calienta la leche con la mantequilla hasta que esta se derrita.\n5. Incorpora la mezcla de leche caliente al puré poco a poco, batiendo hasta lograr una textura cremosa.\n6. Ajusta la sal a gusto y sirve caliente.",
                                25, 4, "Fácil",
                                ing("Papas", 4, UnidadMedida.UNIDAD),
                                ing("Leche", 150, UnidadMedida.MILILITROS),
                                ing("Mantequilla", 30, UnidadMedida.GRAMOS),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Huevos Revueltos con Tomate",
                                "Versión simple del revuelto, solo con tomate salteado.",
                                "1. Pica el tomate y la cebolla en cubos pequeños.\n2. Calienta el aceite en un sartén y sofríe la cebolla 2-3 minutos.\n3. Agrega el tomate y cocina otros 3 minutos hasta que suelte su jugo.\n4. Bate los huevos con sal y viértelos sobre el sofrito.\n5. Revuelve a fuego bajo hasta que los huevos cuajen y sirve de inmediato.",
                                12, 2, "Fácil",
                                ing("Huevos", 3, UnidadMedida.UNIDAD),
                                ing("Tomate", 1, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 10, UnidadMedida.MILILITROS),
                                ing("Sal", 3, UnidadMedida.GRAMOS));

                receta(ings, "Arroz con Atún",
                                "Arroz frío mezclado con atún y mayonesa, rápido y rendidor.",
                                "1. Cocina el arroz como de costumbre y deja enfriar.\n2. Escurre bien el atún en lata.\n3. Pica la cebolla bien fina.\n4. Mezcla el arroz frío con el atún, la cebolla y la mayonesa.\n5. Sazona con sal a gusto y sirve frío o a temperatura ambiente.",
                                20, 3, "Fácil",
                                ing("Arroz", 200, UnidadMedida.GRAMOS),
                                ing("Atún en lata", 160, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Mayonesa", 60, UnidadMedida.GRAMOS));

                receta(ings, "Sopa de Fideos",
                                "Sopa liviana y reconfortante, ideal para los días fríos.",
                                "1. Pica la cebolla y la zanahoria en cubos pequeños.\n2. En una olla, disuelve el caldo de verduras en agua hirviendo siguiendo la proporción del envase.\n3. Agrega la cebolla y zanahoria y cocina 8-10 minutos hasta que estén blandas.\n4. Incorpora los fideos y cocina 8-10 minutos más hasta que estén al dente.\n5. Ajusta la sal a gusto y sirve bien caliente.",
                                20, 4, "Fácil",
                                ing("Fideos / Pasta", 100, UnidadMedida.GRAMOS),
                                ing("Caldo de verduras", 2, UnidadMedida.UNIDAD),
                                ing("Zanahoria", 1, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD));

                receta(ings, "Tostadas Francesas",
                                "Pan de molde bañado en huevo y leche, dorado y espolvoreado con canela.",
                                "1. Bate los huevos junto con la leche, el azúcar y la canela en un bowl amplio.\n2. Sumerge cada rebanada de pan en la mezcla por ambos lados, dejando que se empape bien.\n3. Calienta mantequilla en un sartén a fuego medio.\n4. Cocina cada rebanada 2-3 minutos por lado hasta dorar.\n5. Sirve caliente, espolvoreada con azúcar o acompañada de miel.",
                                15, 3, "Fácil",
                                ing("Pan de molde", 6, UnidadMedida.UNIDAD),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Leche", 100, UnidadMedida.MILILITROS),
                                ing("Azúcar", 20, UnidadMedida.GRAMOS),
                                ing("Canela", 3, UnidadMedida.GRAMOS));

                receta(ings, "Huevos con Jamón y Queso",
                                "Huevos revueltos con trozos de jamón y queso derretido.",
                                "1. Corta el jamón en cubos pequeños.\n2. Calienta el aceite en un sartén y saltea el jamón 1-2 minutos.\n3. Bate los huevos y viértelos sobre el jamón.\n4. Revuelve a fuego bajo hasta que empiecen a cuajar.\n5. Agrega el queso rallado y sigue revolviendo hasta que se derrita.\n6. Sirve de inmediato.",
                                12, 2, "Fácil",
                                ing("Huevos", 4, UnidadMedida.UNIDAD),
                                ing("Jamón", 60, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 50, UnidadMedida.GRAMOS),
                                ing("Aceite", 10, UnidadMedida.MILILITROS));

                // ---------- MEDIA (21) ----------

                receta(ings, "Cazuela de Pollo",
                                "La cazuela chilena clásica, con pollo y verduras de la estación.",
                                "1. En una olla grande, dora los trutros de pollo con un poco de aceite por ambos lados.\n2. Agrega la cebolla picada y sofríe 3-4 minutos.\n3. Cubre con agua caliente y deja hervir 20 minutos.\n4. Incorpora las papas peladas enteras, el zapallo en trozos, la zanahoria y el choclo.\n5. Cocina a fuego medio 30-40 minutos hasta que las papas y el pollo estén bien cocidos.\n6. Sazona con sal a gusto y sirve caliente en plato hondo.",
                                70, 4, "Media",
                                ing("Trutro de pollo", 4, UnidadMedida.UNIDAD),
                                ing("Papas", 4, UnidadMedida.UNIDAD),
                                ing("Zapallo", 300, UnidadMedida.GRAMOS),
                                ing("Zanahoria", 2, UnidadMedida.UNIDAD),
                                ing("Choclo", 2, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Sal", 8, UnidadMedida.GRAMOS));

                receta(ings, "Cazuela de Vacuno",
                                "Versión con posta de vacuno, un caldo sustancioso y reconfortante.",
                                "1. Dora la posta de vacuno en trozos grandes en una olla con aceite.\n2. Agrega la cebolla picada y sofríe 3-4 minutos.\n3. Cubre con agua caliente y cocina a fuego medio-bajo 40 minutos para que la carne se ablande.\n4. Incorpora las papas, el zapallo, la zanahoria y el choclo.\n5. Cocina 25-30 minutos más hasta que todas las verduras estén tiernas.\n6. Sazona con sal y sirve bien caliente.",
                                80, 4, "Media",
                                ing("Posta de vacuno", 500, UnidadMedida.GRAMOS),
                                ing("Papas", 4, UnidadMedida.UNIDAD),
                                ing("Zapallo", 300, UnidadMedida.GRAMOS),
                                ing("Zanahoria", 2, UnidadMedida.UNIDAD),
                                ing("Choclo", 2, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Sal", 8, UnidadMedida.GRAMOS));

                receta(ings, "Pastel de Choclo",
                                "El clásico pastel de choclo con pino de carne y pollo, dulce por encima.",
                                "1. Prepara el pino: sofríe la cebolla picada en aceite hasta transparentar, agrega la carne molida y el pollo desmenuzado y cocina 8-10 minutos.\n2. Distribuye el pino en el fondo de una fuente para horno, agregando encima huevo duro en rodajas, aceitunas y pasas.\n3. Licua o muele el choclo junto con un poco de leche y azúcar hasta formar una pasta.\n4. Cocina esa pasta de choclo en una olla a fuego medio, revolviendo constantemente, hasta que espese (10-12 minutos).\n5. Vierte la pasta de choclo caliente sobre el pino, cubriendo toda la fuente.\n6. Espolvorea azúcar por encima y hornea a 200°C durante 25-30 minutos hasta dorar la superficie.\n7. Deja reposar 5 minutos antes de servir.",
                                75, 6, "Media",
                                ing("Choclo", 6, UnidadMedida.UNIDAD),
                                ing("Carne molida", 300, UnidadMedida.GRAMOS),
                                ing("Pechuga de pollo", 200, UnidadMedida.GRAMOS),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Aceitunas", 50, UnidadMedida.GRAMOS),
                                ing("Pasas", 30, UnidadMedida.GRAMOS),
                                ing("Azúcar", 20, UnidadMedida.GRAMOS),
                                ing("Leche", 50, UnidadMedida.MILILITROS));

                receta(ings, "Charquicán",
                                "Guiso espeso de carne molida y verduras machacadas, muy tradicional.",
                                "1. Sofríe la cebolla y el ajo picados en aceite hasta transparentar.\n2. Agrega la carne molida y cocina 6-8 minutos hasta dorar.\n3. Incorpora las papas y el zapallo en cubos, cubre con agua y cocina 20-25 minutos hasta que estén muy blandos.\n4. Agrega el choclo y cocina 5 minutos más.\n5. Machaca parcialmente las verduras con un tenedor dentro de la misma olla, dejando una textura espesa.\n6. Sazona con sal y sirve caliente, tradicionalmente con un huevo frito encima.",
                                45, 4, "Media",
                                ing("Carne molida", 300, UnidadMedida.GRAMOS),
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Zapallo", 250, UnidadMedida.GRAMOS),
                                ing("Choclo", 1, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Ajo", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 15, UnidadMedida.MILILITROS));

                receta(ings, "Porotos Granados",
                                "Guiso veraniego de porotos con choclo y zapallo, típico de Chile.",
                                "1. Sofríe la cebolla y el ajo picados en aceite hasta transparentar.\n2. Agrega los porotos (previamente remojados o ya cocidos) junto con un poco de agua.\n3. Incorpora el choclo desgranado y el zapallo en cubos.\n4. Cocina a fuego medio-bajo 35-40 minutos, revolviendo de vez en cuando, hasta que espese.\n5. Si queda muy líquido, machaca un poco de choclo contra la olla para espesar naturalmente.\n6. Sazona con sal y sirve caliente, acompañado de ensalada chilena o pebre.",
                                60, 4, "Media",
                                ing("Porotos", 300, UnidadMedida.GRAMOS),
                                ing("Choclo", 3, UnidadMedida.UNIDAD),
                                ing("Zapallo", 250, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Ajo", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 15, UnidadMedida.MILILITROS));

                receta(ings, "Arroz con Pollo",
                                "Arroz cocido junto al pollo y verduras, todo en una sola olla.",
                                "1. Dora la pechuga de pollo troceada en aceite hasta dorar por fuera.\n2. Agrega la cebolla y el pimentón picados y sofríe 3-4 minutos.\n3. Incorpora el arroz y revuelve 1-2 minutos para que se impregne.\n4. Añade la zanahoria en cubos y el doble de agua que de arroz, con sal a gusto.\n5. Cocina tapado a fuego bajo 18-20 minutos sin destapar.\n6. Deja reposar 5 minutos y revuelve suavemente antes de servir.",
                                45, 4, "Media",
                                ing("Arroz", 300, UnidadMedida.GRAMOS),
                                ing("Pechuga de pollo", 300, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Pimentón", 1, UnidadMedida.UNIDAD),
                                ing("Zanahoria", 1, UnidadMedida.UNIDAD),
                                ing("Sal", 8, UnidadMedida.GRAMOS));

                receta(ings, "Bistec a lo Pobre",
                                "Bistec de vacuno con papas fritas, cebolla y huevo frito encima.",
                                "1. Corta las papas en bastones y fríelas en aceite caliente hasta dorar. Escurre y reserva.\n2. Sazona la posta de vacuno cortada en bistecs con sal.\n3. Cocina los bistecs en un sartén con un poco de aceite, 2-3 minutos por lado.\n4. En el mismo sartén, saltea la cebolla en pluma hasta que esté suave y transparente.\n5. Fríe los huevos aparte hasta que la clara esté firme y la yema líquida.\n6. Sirve el bistec sobre las papas fritas, corona con la cebolla salteada y el huevo frito.",
                                35, 2, "Media",
                                ing("Posta de vacuno", 300, UnidadMedida.GRAMOS),
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Aceite", 40, UnidadMedida.MILILITROS));

                receta(ings, "Milanesas de Pollo con Puré",
                                "Pechugas apanadas y doradas, servidas con puré de papas.",
                                "1. Corta la pechuga de pollo en láminas delgadas y sazona con sal.\n2. Pasa cada lámina por harina, luego por huevo batido.\n3. Fríe las milanesas en aceite caliente 3-4 minutos por lado hasta dorar y cocinar por dentro.\n4. Escurre sobre papel absorbente.\n5. Prepara el puré: cocina las papas, hazlas puré e incorpora la leche caliente hasta lograr una textura cremosa.\n6. Sirve las milanesas calientes junto al puré.",
                                40, 4, "Media",
                                ing("Pechuga de pollo", 400, UnidadMedida.GRAMOS),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Harina de trigo", 100, UnidadMedida.GRAMOS),
                                ing("Papas", 4, UnidadMedida.UNIDAD),
                                ing("Leche", 100, UnidadMedida.MILILITROS));

                receta(ings, "Guiso de Lentejas",
                                "Guiso espeso y nutritivo, uno de los infaltables de la semana.",
                                "1. Sofríe la cebolla, la zanahoria y el ajo picados en aceite hasta que estén suaves.\n2. Agrega las lentejas junto con agua suficiente para cubrir bien.\n3. Cocina a fuego medio-bajo 25-30 minutos, revolviendo de vez en cuando.\n4. Incorpora las papas en cubos y cocina 15 minutos más hasta que todo esté blando.\n5. Ajusta la sal y deja espesar unos minutos más si es necesario.\n6. Sirve caliente.",
                                50, 4, "Media",
                                ing("Lentejas", 300, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Zanahoria", 2, UnidadMedida.UNIDAD),
                                ing("Ajo", 1, UnidadMedida.UNIDAD),
                                ing("Papas", 2, UnidadMedida.UNIDAD));

                receta(ings, "Guiso de Garbanzos con Chorizo",
                                "Garbanzos guisados con chorizo, cebolla y pimentón.",
                                "1. Corta el chorizo en rodajas y dóralo en una olla con un poco de aceite.\n2. Retira el chorizo y en la misma olla sofríe la cebolla, el pimentón y el ajo picados.\n3. Agrega los garbanzos y el chorizo dorado nuevamente a la olla.\n4. Cubre con agua y cocina a fuego medio 30-35 minutos hasta que los garbanzos estén blandos.\n5. Sazona con sal a gusto y deja reducir un poco el líquido antes de servir.",
                                50, 4, "Media",
                                ing("Garbanzos", 300, UnidadMedida.GRAMOS),
                                ing("Chorizo", 2, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Pimentón", 1, UnidadMedida.UNIDAD),
                                ing("Ajo", 1, UnidadMedida.UNIDAD));

                receta(ings, "Chorrillana",
                                "Papas fritas cubiertas con carne, cebolla salteada y huevo.",
                                "1. Corta las papas en bastones y fríelas en abundante aceite hasta dorar. Escurre.\n2. Corta la posta de vacuno en tiras finas y cocínala en un sartén con aceite a fuego alto hasta dorar.\n3. En el mismo sartén, saltea la cebolla en pluma 3-4 minutos.\n4. Arma una fuente con las papas fritas como base.\n5. Distribuye encima la carne y la cebolla salteada.\n6. Espolvorea queso rallado por encima y agrega huevos fritos al final.\n7. Sirve de inmediato, bien caliente.",
                                40, 4, "Media",
                                ing("Papas", 4, UnidadMedida.UNIDAD),
                                ing("Posta de vacuno", 300, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Queso rallado", 80, UnidadMedida.GRAMOS));

                receta(ings, "Pantrucas",
                                "Sopa con trozos de masa casera, un clásico calentito de invierno.",
                                "1. Prepara la masa mezclando harina, un huevo y una pizca de sal hasta lograr una masa firme. Amasa y deja reposar 10 minutos.\n2. Estira la masa fina y córtala en cuadrados o rombos irregulares.\n3. Disuelve el caldo de verduras en agua hirviendo en una olla grande.\n4. Agrega la zanahoria y las papas en cubos y cocina 10 minutos.\n5. Incorpora los trozos de masa (pantrucas) al caldo hirviendo y cocina 8-10 minutos más hasta que floten y estén cocidas.\n6. Ajusta la sal y sirve bien caliente.",
                                40, 4, "Media",
                                ing("Harina de trigo", 200, UnidadMedida.GRAMOS),
                                ing("Huevos", 1, UnidadMedida.UNIDAD),
                                ing("Caldo de verduras", 2, UnidadMedida.UNIDAD),
                                ing("Zanahoria", 1, UnidadMedida.UNIDAD),
                                ing("Papas", 2, UnidadMedida.UNIDAD));

                receta(ings, "Tallarines con Salsa Boloñesa",
                                "Fideos largos con una salsa de carne molida y tomate.",
                                "1. Sofríe la cebolla y el ajo picados en aceite hasta transparentar.\n2. Agrega la carne molida y cocina 6-8 minutos hasta que pierda el color rosado.\n3. Incorpora la salsa de tomate y cocina a fuego bajo 15 minutos, revolviendo de vez en cuando.\n4. Mientras tanto, cocina los tallarines en agua con sal según el tiempo del envase.\n5. Escurre la pasta y mézclala con la salsa boloñesa.\n6. Sirve caliente, con queso rallado por encima.",
                                35, 4, "Media",
                                ing("Fideos / Pasta", 300, UnidadMedida.GRAMOS),
                                ing("Carne molida", 300, UnidadMedida.GRAMOS),
                                ing("Salsa de tomate", 250, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Ajo", 1, UnidadMedida.UNIDAD));

                receta(ings, "Tallarines con Pollo a la Crema",
                                "Pasta cremosa con trozos de pollo y queso fundido.",
                                "1. Corta la pechuga de pollo en cubos y sazona con sal.\n2. Cocina el pollo en un sartén con un poco de aceite hasta dorar por completo.\n3. Agrega la cebolla picada y sofríe 3-4 minutos.\n4. Incorpora la crema de leche y cocina a fuego bajo 5 minutos, revolviendo.\n5. Agrega el queso rallado y mezcla hasta que se derrita y la salsa espese.\n6. Cocina los tallarines aparte, escúrrelos y mézclalos con la salsa antes de servir.",
                                35, 4, "Media",
                                ing("Fideos / Pasta", 300, UnidadMedida.GRAMOS),
                                ing("Pechuga de pollo", 300, UnidadMedida.GRAMOS),
                                ing("Crema de leche", 200, UnidadMedida.MILILITROS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Queso rallado", 60, UnidadMedida.GRAMOS));

                receta(ings, "Pollo Asado al Horno con Papas",
                                "Trutros de pollo horneados junto a papas, dorados y jugosos.",
                                "1. Precalienta el horno a 200°C.\n2. Sazona los trutros de pollo con sal, orégano y ajo picado.\n3. Coloca el pollo en una fuente para horno junto con las papas cortadas en trozos grandes.\n4. Rocía todo con aceite y mezcla bien para que se impregne.\n5. Hornea 45-50 minutos, dando vuelta a mitad de cocción, hasta que el pollo esté dorado y las papas blandas.\n6. Deja reposar 5 minutos antes de servir.",
                                60, 4, "Media",
                                ing("Trutro de pollo", 4, UnidadMedida.UNIDAD),
                                ing("Papas", 5, UnidadMedida.UNIDAD),
                                ing("Ajo", 2, UnidadMedida.UNIDAD),
                                ing("Aceite", 30, UnidadMedida.MILILITROS),
                                ing("Orégano", 5, UnidadMedida.GRAMOS));

                receta(ings, "Costillar de Cerdo al Horno",
                                "Costillar dorado y jugoso, horneado lentamente con papas.",
                                "1. Precalienta el horno a 190°C.\n2. Sazona el costillar de cerdo con sal, orégano y ajo picado.\n3. Colócalo en una fuente para horno rociado con aceite.\n4. Hornea 40 minutos, luego agrega las papas alrededor y continúa horneando.\n5. Cocina 30 minutos más, dando vuelta el costillar a mitad de camino, hasta que esté bien dorado.\n6. Deja reposar 5-10 minutos antes de cortar y servir.",
                                70, 4, "Media",
                                ing("Costillar de cerdo", 800, UnidadMedida.GRAMOS),
                                ing("Papas", 4, UnidadMedida.UNIDAD),
                                ing("Ajo", 2, UnidadMedida.UNIDAD),
                                ing("Orégano", 5, UnidadMedida.GRAMOS),
                                ing("Aceite", 20, UnidadMedida.MILILITROS));

                receta(ings, "Empanadas Fritas de Queso",
                                "Empanadas crujientes rellenas de queso derretido.",
                                "1. Mezcla la harina con sal, un huevo y agua tibia hasta formar una masa suave. Amasa y deja reposar 15 minutos.\n2. Divide la masa en bolitas y estíralas en discos delgados.\n3. Coloca queso rallado al centro de cada disco.\n4. Dobla por la mitad formando una media luna y sella bien los bordes presionando con un tenedor.\n5. Calienta abundante aceite en una olla a fuego medio-alto.\n6. Fríe las empanadas de a pocas hasta que estén doradas por ambos lados (3-4 minutos).\n7. Escurre sobre papel absorbente antes de servir calientes.",
                                45, 6, "Media",
                                ing("Harina de trigo", 400, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 250, UnidadMedida.GRAMOS),
                                ing("Huevos", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 500, UnidadMedida.MILILITROS),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Estofado de Pollo a la Jardinera",
                                "Pollo guisado lentamente con varias verduras picadas.",
                                "1. Dora la pechuga de pollo troceada en aceite hasta sellar por todos lados.\n2. Agrega la cebolla picada y sofríe 3-4 minutos.\n3. Incorpora la zanahoria, el zapallo y las papas en cubos.\n4. Cubre con agua caliente y cocina a fuego medio-bajo 30-35 minutos hasta que las verduras y el pollo estén tiernos.\n5. Sazona con sal y deja reducir un poco el líquido antes de servir.",
                                50, 4, "Media",
                                ing("Pechuga de pollo", 400, UnidadMedida.GRAMOS),
                                ing("Zanahoria", 2, UnidadMedida.UNIDAD),
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Zapallo", 200, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD));

                receta(ings, "Chupe de Marisco Sencillo",
                                "Chupe cremoso con mariscos surtidos y pan remojado en leche.",
                                "1. Remoja el pan de molde en leche hasta que se ablande completamente.\n2. Sofríe la cebolla picada en un poco de aceite hasta transparentar.\n3. Agrega los mariscos surtidos y cocina 5 minutos.\n4. Incorpora el pan remojado (con la leche) y mezcla hasta integrar, formando una crema espesa.\n5. Cocina a fuego bajo 10 minutos, revolviendo para que no se pegue.\n6. Agrega el queso rallado, mezcla hasta derretir y sazona con sal.\n7. Sirve caliente, gratinado si se desea unos minutos al horno.",
                                45, 4, "Media",
                                ing("Mariscos surtidos", 400, UnidadMedida.GRAMOS),
                                ing("Pan de molde", 4, UnidadMedida.UNIDAD),
                                ing("Leche", 300, UnidadMedida.MILILITROS),
                                ing("Queso rallado", 100, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD));

                receta(ings, "Humitas",
                                "Choclo molido envuelto y cocido en sus propias hojas, dulce y suave.",
                                "1. Muele o licua el choclo desgranado junto con la cebolla picada hasta formar una pasta gruesa.\n2. Cocina la pasta en una olla a fuego medio con un poco de aceite, revolviendo 8-10 minutos.\n3. Agrega leche, azúcar y sal a gusto, y sigue cocinando hasta que espese.\n4. Deja enfriar un poco y arma porciones en hojas de choclo (o papel aluminio) formando paquetes bien cerrados.\n5. Cocina los paquetes al vapor o hervidos 40-45 minutos.\n6. Deja reposar antes de abrir y servir.",
                                60, 6, "Media",
                                ing("Choclo", 8, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Leche", 100, UnidadMedida.MILILITROS),
                                ing("Azúcar", 20, UnidadMedida.GRAMOS),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Carbonada",
                                "Guiso de carne con muchas verduras picadas y fideos, caldoso y sustancioso.",
                                "1. Dora la posta de vacuno en trozos pequeños en una olla con aceite.\n2. Agrega la cebolla picada y sofríe 3-4 minutos.\n3. Cubre con agua caliente y cocina a fuego medio-bajo 30 minutos hasta que la carne se ablande.\n4. Incorpora las papas, el zapallo, la zanahoria y el choclo, todo en cubos.\n5. Cocina 15-20 minutos más hasta que las verduras estén tiernas.\n6. Agrega los fideos y cocina 8-10 minutos finales.\n7. Sazona con sal y sirve bien caliente.",
                                55, 4, "Media",
                                ing("Posta de vacuno", 400, UnidadMedida.GRAMOS),
                                ing("Papas", 2, UnidadMedida.UNIDAD),
                                ing("Zapallo", 200, UnidadMedida.GRAMOS),
                                ing("Zanahoria", 2, UnidadMedida.UNIDAD),
                                ing("Choclo", 1, UnidadMedida.UNIDAD),
                                ing("Fideos / Pasta", 100, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD));

                // ---------- DIFÍCIL (11) ----------

                receta(ings, "Empanadas de Pino al Horno",
                                "Las empanadas chilenas por excelencia, con pino de carne, huevo y aceitunas.",
                                "1. Prepara el pino: sofríe la cebolla picada en bastante aceite hasta transparentar, agrega la carne molida, comino y sal, y cocina 10 minutos. Deja enfriar.\n2. Prepara la masa mezclando harina, sal, aceite tibio y agua hasta lograr una masa lisa y elástica. Amasa 5-10 minutos y deja reposar 20 minutos.\n3. Divide la masa en bolas y estíralas en discos de unos 15 cm.\n4. Rellena cada disco con pino frío, un trozo de huevo duro, una aceituna y unas pasas.\n5. Dobla y sella los bordes con los dedos, formando los repulgues típicos.\n6. Pinta las empanadas con huevo batido y colócalas en una bandeja para horno.\n7. Hornea a 200°C durante 25-30 minutos hasta que estén doradas.\n8. Deja reposar 5 minutos antes de servir bien calientes.",
                                90, 8, "Difícil",
                                ing("Harina de trigo", 500, UnidadMedida.GRAMOS),
                                ing("Carne molida", 400, UnidadMedida.GRAMOS),
                                ing("Cebolla", 2, UnidadMedida.UNIDAD),
                                ing("Huevos", 3, UnidadMedida.UNIDAD),
                                ing("Aceitunas", 60, UnidadMedida.GRAMOS),
                                ing("Pasas", 40, UnidadMedida.GRAMOS),
                                ing("Comino", 5, UnidadMedida.GRAMOS),
                                ing("Aceite", 80, UnidadMedida.MILILITROS));

                receta(ings, "Caldillo de Congrio",
                                "El famoso caldillo chileno de pescado, inmortalizado por Pablo Neruda.",
                                "1. Corta el congrio en postas y sazona con sal.\n2. Sofríe la cebolla picada en aceite hasta transparentar.\n3. Agrega el tomate picado y cocina 3-4 minutos hasta que se deshaga un poco.\n4. Vierte el vino blanco y deja reducir 2 minutos.\n5. Cubre con agua caliente y agrega las papas en trozos.\n6. Cocina a fuego medio 20 minutos hasta que las papas estén casi listas.\n7. Incorpora las postas de congrio y cocina 10-12 minutos más, sin revolver demasiado para que no se deshagan.\n8. Sirve caliente, espolvoreado con cilantro fresco picado.",
                                60, 4, "Difícil",
                                ing("Congrio", 600, UnidadMedida.GRAMOS),
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Tomate", 2, UnidadMedida.UNIDAD),
                                ing("Vino blanco", 100, UnidadMedida.MILILITROS),
                                ing("Cilantro", 10, UnidadMedida.GRAMOS));

                receta(ings, "Cazuela de Cordero",
                                "Cazuela con carne de cordero, de sabor intenso y muy tradicional en el sur de Chile.",
                                "1. Dora los trozos de carne de cordero en una olla con aceite hasta sellar bien.\n2. Agrega la cebolla picada y sofríe 3-4 minutos.\n3. Cubre con agua caliente y cocina a fuego medio-bajo 45-50 minutos hasta que la carne empiece a ablandarse.\n4. Incorpora las papas enteras, el zapallo en trozos, la zanahoria y el choclo.\n5. Continúa la cocción 25-30 minutos más hasta que todo esté tierno.\n6. Sazona con sal y ajusta el punto de cocción de la carne antes de servir.\n7. Sirve caliente en plato hondo con bastante caldo.",
                                90, 4, "Difícil",
                                ing("Carne de cordero", 600, UnidadMedida.GRAMOS),
                                ing("Papas", 4, UnidadMedida.UNIDAD),
                                ing("Zapallo", 300, UnidadMedida.GRAMOS),
                                ing("Zanahoria", 2, UnidadMedida.UNIDAD),
                                ing("Choclo", 2, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD));

                receta(ings, "Estofado de Carne con Vino",
                                "Carne cocida lentamente en vino tinto hasta quedar muy tierna.",
                                "1. Sazona la posta de vacuno en trozos grandes con sal y dórala en una olla con aceite por todos lados.\n2. Retira la carne y en la misma olla sofríe la cebolla y el ajo picados 3-4 minutos.\n3. Devuelve la carne a la olla y vierte el vino tinto, dejando reducir 3-4 minutos.\n4. Agrega agua hasta cubrir parcialmente y cocina tapado a fuego bajo 50-60 minutos, revolviendo de vez en cuando.\n5. Incorpora las papas y la zanahoria en trozos grandes.\n6. Cocina 25-30 minutos más hasta que la carne esté muy tierna y la salsa haya espesado.\n7. Ajusta la sal y sirve caliente.",
                                100, 4, "Difícil",
                                ing("Posta de vacuno", 600, UnidadMedida.GRAMOS),
                                ing("Vino tinto", 200, UnidadMedida.MILILITROS),
                                ing("Zanahoria", 2, UnidadMedida.UNIDAD),
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Ajo", 2, UnidadMedida.UNIDAD));

                receta(ings, "Machas a la Parmesana",
                                "Machas gratinadas con queso, mantequilla y un toque de vino blanco.",
                                "1. Limpia bien las machas y colócalas en sus conchas sobre una bandeja para horno.\n2. Precalienta el horno a 220°C en modo grill si está disponible.\n3. Derrite la mantequilla y mézclala con un chorrito de jugo de limón y vino blanco.\n4. Baña cada macha con un poco de esta mezcla.\n5. Cubre generosamente con queso rallado.\n6. Hornea 8-10 minutos hasta que el queso esté dorado y burbujeante.\n7. Sirve de inmediato, bien calientes, con más limón al gusto.",
                                30, 4, "Difícil",
                                ing("Machas", 500, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 150, UnidadMedida.GRAMOS),
                                ing("Mantequilla", 40, UnidadMedida.GRAMOS),
                                ing("Limón", 2, UnidadMedida.UNIDAD),
                                ing("Vino blanco", 50, UnidadMedida.MILILITROS));

                receta(ings, "Lomo a lo Pobre con Salsa",
                                "Lomo jugoso con papas fritas, cebolla y una salsa reducida al vino.",
                                "1. Sazona la posta de vacuno cortada en bistecs gruesos con sal.\n2. Cocina los bistecs en un sartén bien caliente con aceite, 2-3 minutos por lado para que queden jugosos. Retira y reserva.\n3. En el mismo sartén, agrega la cebolla en pluma y saltea hasta que esté suave.\n4. Vierte el vino tinto sobre la cebolla y deja reducir 2-3 minutos, raspando el fondo del sartén.\n5. Corta las papas en bastones y fríelas aparte hasta dorar.\n6. Fríe los huevos hasta que la clara esté firme y la yema líquida.\n7. Sirve el lomo sobre las papas fritas, baña con la salsa de cebolla y vino, y corona con el huevo frito.",
                                45, 2, "Difícil",
                                ing("Posta de vacuno", 350, UnidadMedida.GRAMOS),
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Vino tinto", 60, UnidadMedida.MILILITROS));

                receta(ings, "Pastel de Papas con Carne Mechada",
                                "Puré de papas horneado sobre un pino de carne mechada.",
                                "1. Cocina la posta de vacuno en agua con sal hasta que esté muy blanda (puede cocinarse el día anterior). Desmecha con las manos o dos tenedores.\n2. Sofríe la cebolla picada en aceite hasta transparentar y agrega la carne mechada, cocinando 8-10 minutos.\n3. Cocina las papas en agua con sal hasta que estén muy blandas y hazlas puré con leche y mantequilla.\n4. En una fuente para horno, coloca la mitad del puré como base.\n5. Distribuye la carne mechada encima, junto con huevo duro en rodajas.\n6. Cubre con el resto del puré, alisando la superficie.\n7. Espolvorea queso rallado por encima y hornea a 200°C durante 20-25 minutos hasta dorar.\n8. Deja reposar 5-10 minutos antes de servir.",
                                90, 6, "Difícil",
                                ing("Papas", 6, UnidadMedida.UNIDAD),
                                ing("Posta de vacuno", 400, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Queso rallado", 100, UnidadMedida.GRAMOS),
                                ing("Leche", 100, UnidadMedida.MILILITROS));

                receta(ings, "Kuchen de Manzana",
                                "Kuchen tradicional del sur de Chile, con base de masa y manzanas.",
                                "1. Precalienta el horno a 180°C.\n2. Mezcla la harina, la mantequilla fría en cubos y el azúcar con las manos hasta formar un arenado.\n3. Agrega un huevo y amasa brevemente hasta formar una masa homogénea. Presiona la masa en un molde para tarta.\n4. Pela y corta las manzanas en láminas delgadas.\n5. Distribuye las láminas de manzana sobre la masa, en forma de abanico.\n6. Espolvorea azúcar y canela por encima.\n7. Hornea 35-40 minutos hasta que la masa esté dorada y las manzanas tiernas.\n8. Deja enfriar antes de desmoldar y servir.",
                                75, 8, "Difícil",
                                ing("Harina de trigo", 300, UnidadMedida.GRAMOS),
                                ing("Manzana", 4, UnidadMedida.UNIDAD),
                                ing("Mantequilla", 150, UnidadMedida.GRAMOS),
                                ing("Azúcar", 100, UnidadMedida.GRAMOS),
                                ing("Huevos", 1, UnidadMedida.UNIDAD),
                                ing("Canela", 5, UnidadMedida.GRAMOS));

                receta(ings, "Pan Amasado",
                                "El pan casero chileno por excelencia, dorado y esponjoso por dentro.",
                                "1. Disuelve la levadura en un poco de leche tibia y deja reposar 10 minutos hasta que espume.\n2. Mezcla la harina con sal en un bowl grande y haz un hueco al centro.\n3. Agrega la mantequilla derretida y la mezcla de levadura, integrando poco a poco.\n4. Amasa sobre una superficie enharinada 10-12 minutos hasta lograr una masa lisa y elástica.\n5. Deja reposar la masa tapada en un lugar tibio 45-60 minutos hasta que doble su tamaño.\n6. Divide en bollos, dales forma redonda y pínchalos con un tenedor.\n7. Deja reposar 15 minutos más y hornea a 200°C durante 20-25 minutos hasta dorar.\n8. Deja enfriar unos minutos antes de servir.",
                                120, 10, "Difícil",
                                ing("Harina de trigo", 600, UnidadMedida.GRAMOS),
                                ing("Levadura", 15, UnidadMedida.GRAMOS),
                                ing("Mantequilla", 80, UnidadMedida.GRAMOS),
                                ing("Sal", 10, UnidadMedida.GRAMOS),
                                ing("Leche", 200, UnidadMedida.MILILITROS));

                receta(ings, "Alfajores Rellenos con Manjar",
                                "Galletas suaves unidas con manjar, un clásico de la repostería chilena.",
                                "1. Bate la mantequilla con el azúcar hasta lograr una crema suave.\n2. Agrega los huevos uno a uno, batiendo bien después de cada uno.\n3. Incorpora la harina y la maicena tamizadas, mezclando hasta formar una masa suave sin amasar de más.\n4. Deja reposar la masa envuelta 20 minutos en un lugar fresco.\n5. Estira la masa y corta círculos parejos con un molde.\n6. Hornea a 180°C durante 10-12 minutos hasta que estén firmes pero sin dorarse demasiado.\n7. Deja enfriar completamente y une de a dos galletas con una capa generosa de manjar al centro.\n8. Deja reposar antes de servir para que se asienten.",
                                90, 12, "Difícil",
                                ing("Harina de trigo", 250, UnidadMedida.GRAMOS),
                                ing("Maicena", 250, UnidadMedida.GRAMOS),
                                ing("Mantequilla", 150, UnidadMedida.GRAMOS),
                                ing("Manjar", 300, UnidadMedida.GRAMOS),
                                ing("Azúcar", 100, UnidadMedida.GRAMOS),
                                ing("Huevos", 2, UnidadMedida.UNIDAD));

                receta(ings, "Torta Tres Leches",
                                "Bizcocho esponjoso empapado en tres tipos de leche, muy húmedo y dulce.",
                                "1. Precalienta el horno a 180°C y enmantequilla un molde.\n2. Bate los huevos con el azúcar hasta que dupliquen su volumen y estén espumosos.\n3. Incorpora la harina tamizada con movimientos envolventes para no perder el aire.\n4. Vierte la mezcla en el molde y hornea 25-30 minutos hasta que un palillo salga limpio.\n5. Deja enfriar el bizcocho y perfóralo por toda la superficie con un tenedor.\n6. Mezcla la leche con la crema de leche y un poco de azúcar en un bowl.\n7. Vierte esta mezcla lentamente sobre el bizcocho frío, dejando que se empape por completo.\n8. Refrigera al menos 2 horas antes de servir, idealmente decorada con crema batida.",
                                100, 10, "Difícil",
                                ing("Harina de trigo", 200, UnidadMedida.GRAMOS),
                                ing("Huevos", 4, UnidadMedida.UNIDAD),
                                ing("Azúcar", 150, UnidadMedida.GRAMOS),
                                ing("Leche", 300, UnidadMedida.MILILITROS),
                                ing("Crema de leche", 200, UnidadMedida.MILILITROS));
        }

        /**
         * 30 recetas pensadas para jóvenes que recién viven solos: rápidas,
         * baratas, de pocos ingredientes y bajo nivel de técnica en la cocina.
         */
        private void inicializarRecetasJovenes(Map<String, Ingrediente> ings) {

                // ---------- FÁCIL (20) ----------

                receta(ings, "Arroz con Vienesas",
                                "El clásico de emergencia: rápido, barato y siempre rinde.",
                                "1. Cocina el arroz como de costumbre (agua, sal, 15-18 minutos tapado).\n2. Corta las vienesas en rodajas.\n3. Calienta un poco de aceite en un sartén y saltea las vienesas 3-4 minutos hasta que doren.\n4. Mezcla las vienesas con el arroz cocido o sírvelas encima.\n5. Agrega salsa de tomate por encima si quieres.",
                                20, 2, "Fácil",
                                ing("Arroz", 200, UnidadMedida.GRAMOS),
                                ing("Vienesas", 4, UnidadMedida.UNIDAD),
                                ing("Aceite", 10, UnidadMedida.MILILITROS),
                                ing("Salsa de tomate", 60, UnidadMedida.GRAMOS));

                receta(ings, "Completo Simple en Pan",
                                "Vienesa en pan con lo básico, para el hambre de después de clases.",
                                "1. Cocina las vienesas en agua hirviendo 5 minutos, o a la plancha si prefieres.\n2. Corta el pan de molde o marraqueta por la mitad.\n3. Coloca la vienesa dentro del pan.\n4. Agrega salsa de tomate y mayonesa a gusto.\n5. Sirve de inmediato.",
                                10, 2, "Fácil",
                                ing("Vienesas", 2, UnidadMedida.UNIDAD),
                                ing("Marraqueta", 2, UnidadMedida.UNIDAD),
                                ing("Salsa de tomate", 30, UnidadMedida.GRAMOS),
                                ing("Mayonesa", 30, UnidadMedida.GRAMOS));

                receta(ings, "Sándwich de Atún",
                                "Proteína rápida sin encender casi nada.",
                                "1. Escurre bien el atún en lata.\n2. Mezcla el atún con mayonesa en un bowl pequeño.\n3. Unta la mezcla sobre una rebanada de pan de molde.\n4. Cubre con la otra rebanada y corta por la mitad.\n5. Sirve frío, listo en minutos.",
                                8, 1, "Fácil",
                                ing("Atún en lata", 120, UnidadMedida.GRAMOS),
                                ing("Pan de molde", 2, UnidadMedida.UNIDAD),
                                ing("Mayonesa", 30, UnidadMedida.GRAMOS));

                receta(ings, "Tostadas con Mermelada",
                                "El desayuno más simple que existe.",
                                "1. Tuesta las rebanadas de pan de molde hasta que estén doradas.\n2. Unta mantequilla mientras aún están calientes.\n3. Agrega mermelada por encima al gusto.\n4. Sirve de inmediato.",
                                5, 1, "Fácil",
                                ing("Pan de molde", 2, UnidadMedida.UNIDAD),
                                ing("Mermelada", 30, UnidadMedida.GRAMOS),
                                ing("Mantequilla", 10, UnidadMedida.GRAMOS));

                receta(ings, "Porridge de Avena con Plátano",
                                "Desayuno rápido, rendidor y que llena harto.",
                                "1. Calienta la leche en una olla pequeña o en el microondas.\n2. Agrega la avena y cocina a fuego bajo 3-5 minutos, revolviendo, hasta que espese.\n3. Corta el plátano en rodajas.\n4. Sirve la avena en un bowl y agrega el plátano y azúcar a gusto por encima.",
                                10, 1, "Fácil",
                                ing("Avena", 50, UnidadMedida.GRAMOS),
                                ing("Leche", 200, UnidadMedida.MILILITROS),
                                ing("Plátano", 1, UnidadMedida.UNIDAD),
                                ing("Azúcar", 10, UnidadMedida.GRAMOS));

                receta(ings, "Batido de Plátano y Leche",
                                "Para cuando no hay tiempo ni ganas de cocinar nada.",
                                "1. Pela el plátano y córtalo en trozos.\n2. Coloca el plátano, la leche y el azúcar en una licuadora.\n3. Licua 30-40 segundos hasta que quede homogéneo.\n4. Sirve inmediatamente bien frío.",
                                5, 1, "Fácil",
                                ing("Plátano", 1, UnidadMedida.UNIDAD),
                                ing("Leche", 250, UnidadMedida.MILILITROS),
                                ing("Azúcar", 10, UnidadMedida.GRAMOS));

                receta(ings, "Fideos con Atún",
                                "Pasta rápida con lo que casi siempre hay en la despensa.",
                                "1. Cocina los fideos en agua con sal según el tiempo del envase.\n2. Escurre el atún en lata.\n3. Escurre los fideos y vuelve a ponerlos en la olla.\n4. Agrega el atún y un chorrito de aceite, mezclando bien con el calor residual.\n5. Sazona con sal y pimienta a gusto y sirve.",
                                15, 2, "Fácil",
                                ing("Fideos / Pasta", 200, UnidadMedida.GRAMOS),
                                ing("Atún en lata", 120, UnidadMedida.GRAMOS),
                                ing("Aceite", 10, UnidadMedida.MILILITROS),
                                ing("Sal", 3, UnidadMedida.GRAMOS));

                receta(ings, "Ensalada Rápida de Atún y Tomate",
                                "Ligera, fría y sin necesidad de prender la cocina.",
                                "1. Escurre el atún en lata.\n2. Corta el tomate en cubos.\n3. Mezcla el atún con el tomate en un bowl.\n4. Agrega aceite y sal a gusto.\n5. Sirve fría, sola o con pan.",
                                8, 1, "Fácil",
                                ing("Atún en lata", 120, UnidadMedida.GRAMOS),
                                ing("Tomate", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 10, UnidadMedida.MILILITROS),
                                ing("Sal", 2, UnidadMedida.GRAMOS));

                receta(ings, "Arroz con Queso Derretido",
                                "Arroz recién hecho con queso derretido encima, simple y rico.",
                                "1. Cocina el arroz como de costumbre.\n2. Mientras el arroz está aún caliente, agrega el queso rallado por encima.\n3. Tapa la olla 1-2 minutos para que el queso se derrita con el vapor.\n4. Mezcla y sirve de inmediato.",
                                20, 2, "Fácil",
                                ing("Arroz", 200, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 60, UnidadMedida.GRAMOS),
                                ing("Sal", 3, UnidadMedida.GRAMOS));

                receta(ings, "Tostadas con Queso Derretido",
                                "Pan tostado con queso fundido, listo en minutos.",
                                "1. Coloca las rebanadas de pan de molde en un sartén a fuego bajo.\n2. Espolvorea queso rallado generosamente sobre cada una.\n3. Tapa el sartén 2-3 minutos hasta que el queso se derrita.\n4. Retira con cuidado y sirve caliente.",
                                8, 1, "Fácil",
                                ing("Pan de molde", 2, UnidadMedida.UNIDAD),
                                ing("Queso rallado", 50, UnidadMedida.GRAMOS));

                receta(ings, "Panqueques de Plátano",
                                "Panqueques simples endulzados con plátano, sin necesitar mucho azúcar.",
                                "1. Machaca el plátano con un tenedor en un bowl.\n2. Agrega los huevos y bate hasta integrar.\n3. Incorpora la harina poco a poco hasta formar una masa espesa.\n4. Calienta un poco de mantequilla en un sartén a fuego medio-bajo.\n5. Vierte porciones de masa y cocina 2 minutos por lado hasta dorar.\n6. Sirve calientes, solos o con mermelada.",
                                20, 2, "Fácil",
                                ing("Plátano", 2, UnidadMedida.UNIDAD),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Harina de trigo", 80, UnidadMedida.GRAMOS),
                                ing("Mantequilla", 15, UnidadMedida.GRAMOS));

                receta(ings, "Yogurt con Avena y Plátano",
                                "Desayuno o colación sin cocinar nada.",
                                "1. Corta el plátano en rodajas.\n2. Sirve el yogurt en un bowl o vaso.\n3. Agrega la avena por encima.\n4. Corona con las rodajas de plátano y sirve.",
                                5, 1, "Fácil",
                                ing("Yogurt", 200, UnidadMedida.MILILITROS),
                                ing("Avena", 30, UnidadMedida.GRAMOS),
                                ing("Plátano", 1, UnidadMedida.UNIDAD));

                receta(ings, "Papas Cocidas con Mantequilla y Queso",
                                "Papas simples pero reconfortantes, con mínimo esfuerzo.",
                                "1. Pela las papas y córtalas en trozos medianos.\n2. Cocínalas en agua con sal 15-18 minutos hasta que estén blandas.\n3. Escurre bien.\n4. Agrega mantequilla y queso rallado mientras aún están calientes, mezclando hasta que se derritan.\n5. Sirve caliente.",
                                20, 2, "Fácil",
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Mantequilla", 20, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 40, UnidadMedida.GRAMOS),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Huevo Revuelto Express con Queso",
                                "Dos minutos, un sartén, y ya tienes proteína lista.",
                                "1. Bate los huevos con una pizca de sal.\n2. Calienta un poco de aceite en un sartén a fuego medio.\n3. Vierte los huevos y revuelve constantemente.\n4. Cuando estén casi listos, agrega el queso rallado y sigue revolviendo hasta que se derrita.\n5. Sirve de inmediato.",
                                6, 1, "Fácil",
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Queso rallado", 30, UnidadMedida.GRAMOS),
                                ing("Aceite", 5, UnidadMedida.MILILITROS),
                                ing("Sal", 2, UnidadMedida.GRAMOS));

                receta(ings, "Marraqueta con Mermelada",
                                "Pan fresco con mermelada, para cuando hay poco tiempo en la mañana.",
                                "1. Corta la marraqueta por la mitad.\n2. Unta mantequilla en cada mitad.\n3. Agrega mermelada por encima al gusto.\n4. Sirve de inmediato.",
                                5, 1, "Fácil",
                                ing("Marraqueta", 1, UnidadMedida.UNIDAD),
                                ing("Mermelada", 30, UnidadMedida.GRAMOS),
                                ing("Mantequilla", 10, UnidadMedida.GRAMOS));

                receta(ings, "Sopa de Fideos con Huevo",
                                "Sopa rápida con un huevo escalfado adentro para sumar proteína.",
                                "1. Disuelve el caldo de verduras en agua hirviendo según la proporción del envase.\n2. Agrega los fideos y cocina 8-10 minutos.\n3. Con la sopa hirviendo suave, rompe el huevo directo dentro de la olla.\n4. Cocina 2-3 minutos más sin revolver mucho, hasta que el huevo cuaje.\n5. Sirve caliente.",
                                15, 1, "Fácil",
                                ing("Fideos / Pasta", 80, UnidadMedida.GRAMOS),
                                ing("Caldo de verduras", 1, UnidadMedida.UNIDAD),
                                ing("Huevos", 1, UnidadMedida.UNIDAD));

                receta(ings, "Arroz con Salsa de Tomate y Queso",
                                "Arroz simple con salsa y queso, para cuando no hay casi nada más.",
                                "1. Cocina el arroz como de costumbre.\n2. Calienta la salsa de tomate en una olla pequeña o en el microondas.\n3. Sirve el arroz en un plato y baña con la salsa caliente.\n4. Espolvorea queso rallado por encima antes de servir.",
                                20, 2, "Fácil",
                                ing("Arroz", 200, UnidadMedida.GRAMOS),
                                ing("Salsa de tomate", 100, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 40, UnidadMedida.GRAMOS));

                receta(ings, "Sándwich de Palta y Queso",
                                "Combinación cremosa, rápida y sin usar la cocina.",
                                "1. Macháchala palta con un tenedor y sazona con sal.\n2. Unta la palta sobre una rebanada de pan de molde.\n3. Agrega queso rallado por encima.\n4. Cubre con la otra rebanada y sirve.",
                                8, 1, "Fácil",
                                ing("Palta", 1, UnidadMedida.UNIDAD),
                                ing("Pan de molde", 2, UnidadMedida.UNIDAD),
                                ing("Queso rallado", 30, UnidadMedida.GRAMOS),
                                ing("Sal", 2, UnidadMedida.GRAMOS));

                receta(ings, "Tostadas de Huevo Duro con Mayonesa",
                                "Aprovecha huevos duros que sobraron para un sándwich rápido.",
                                "1. Cocina los huevos en agua hirviendo 10 minutos hasta que estén duros.\n2. Enfría, pela y pica los huevos en cubos pequeños.\n3. Mezcla los huevos con mayonesa y sal.\n4. Tuesta el pan y unta la mezcla encima.\n5. Sirve de inmediato.",
                                15, 1, "Fácil",
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Pan de molde", 2, UnidadMedida.UNIDAD),
                                ing("Mayonesa", 30, UnidadMedida.GRAMOS),
                                ing("Sal", 2, UnidadMedida.GRAMOS));

                receta(ings, "Vienesas a la Plancha con Puré",
                                "Combo sencillo y rendidor para el almuerzo o la cena.",
                                "1. Pela las papas, córtalas en trozos y cocínalas en agua con sal 15-18 minutos.\n2. Escurre y haz puré con leche y mantequilla.\n3. Cocina las vienesas en un sartén a fuego medio 5-6 minutos, dándoles vuelta.\n4. Sirve las vienesas sobre el puré.",
                                25, 2, "Fácil",
                                ing("Vienesas", 4, UnidadMedida.UNIDAD),
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Leche", 60, UnidadMedida.MILILITROS),
                                ing("Mantequilla", 15, UnidadMedida.GRAMOS));

                // ---------- MEDIA (8) ----------

                receta(ings, "Arroz Frito con Huevo y Verduras",
                                "Versión casera y rápida del arroz frito, usando arroz del día anterior.",
                                "1. Bate los huevos aparte.\n2. Calienta aceite en un sartén o wok a fuego alto.\n3. Agrega la cebolla, la zanahoria y el pimentón picados en cubos pequeños y saltea 3-4 minutos.\n4. Empuja las verduras a un lado y vierte los huevos batidos, revolviendo hasta que cuajen.\n5. Agrega el arroz (idealmente frío) y mezcla todo bien, salteando 3-4 minutos más.\n6. Sazona con sal y sirve caliente.",
                                25, 2, "Media",
                                ing("Arroz", 250, UnidadMedida.GRAMOS),
                                ing("Huevos", 2, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Zanahoria", 1, UnidadMedida.UNIDAD),
                                ing("Pimentón", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 15, UnidadMedida.MILILITROS));

                receta(ings, "Salchipapas",
                                "Papas fritas con vienesas doradas, ideal para compartir viendo una serie.",
                                "1. Corta las papas en bastones y fríelas en aceite caliente hasta dorar. Escurre.\n2. Corta las vienesas en rodajas y fríelas en el mismo aceite 2-3 minutos hasta dorar.\n3. Mezcla las papas fritas con las vienesas en una fuente.\n4. Agrega salsa de tomate y mayonesa por encima.\n5. Sirve de inmediato bien caliente.",
                                25, 2, "Media",
                                ing("Papas", 4, UnidadMedida.UNIDAD),
                                ing("Vienesas", 4, UnidadMedida.UNIDAD),
                                ing("Aceite", 300, UnidadMedida.MILILITROS),
                                ing("Salsa de tomate", 40, UnidadMedida.GRAMOS),
                                ing("Mayonesa", 40, UnidadMedida.GRAMOS));

                receta(ings, "Tortilla de Atún",
                                "Como la tortilla de papas, pero con atún en vez de papas.",
                                "1. Escurre el atún en lata.\n2. Pica la cebolla en cubos pequeños y sofríela en aceite 3-4 minutos hasta transparentar.\n3. Bate los huevos con sal en un bowl y agrega el atún y la cebolla salteada.\n4. Vierte la mezcla en el sartén con un poco de aceite a fuego medio-bajo.\n5. Cocina 3-4 minutos hasta que los bordes cuajen, luego dale vuelta con ayuda de un plato.\n6. Cocina 2-3 minutos más por el otro lado y sirve.",
                                20, 2, "Media",
                                ing("Atún en lata", 160, UnidadMedida.GRAMOS),
                                ing("Huevos", 4, UnidadMedida.UNIDAD),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 15, UnidadMedida.MILILITROS),
                                ing("Sal", 3, UnidadMedida.GRAMOS));

                receta(ings, "Arroz con Pollo Exprés en Una Olla",
                                "Todo en la misma olla, para lavar lo mínimo posible.",
                                "1. Corta la pechuga de pollo en cubos y sazona con sal.\n2. Dora el pollo en una olla con aceite 4-5 minutos.\n3. Agrega la cebolla picada y sofríe 2-3 minutos más.\n4. Incorpora el arroz y el doble de agua que de arroz.\n5. Cocina tapado a fuego bajo 18-20 minutos sin destapar.\n6. Deja reposar 5 minutos y sirve.",
                                35, 2, "Media",
                                ing("Arroz", 200, UnidadMedida.GRAMOS),
                                ing("Pechuga de pollo", 200, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Aceite", 15, UnidadMedida.MILILITROS),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Sándwich Caliente de Vienesa y Queso",
                                "Vienesa y queso fundido entre pan tostado, para una cena rápida.",
                                "1. Corta las vienesas por la mitad a lo largo.\n2. Cocínalas en un sartén 3-4 minutos hasta dorar.\n3. Arma el sándwich con las vienesas y queso rallado entre dos rebanadas de pan.\n4. Cocina el sándwich armado en el sartén con un poco de mantequilla, 2 minutos por lado, hasta que el queso se derrita.\n5. Corta por la mitad y sirve caliente.",
                                20, 2, "Media",
                                ing("Vienesas", 3, UnidadMedida.UNIDAD),
                                ing("Pan de molde", 4, UnidadMedida.UNIDAD),
                                ing("Queso rallado", 60, UnidadMedida.GRAMOS),
                                ing("Mantequilla", 15, UnidadMedida.GRAMOS));

                receta(ings, "Sopa Cremosa de Zapallo Rápida",
                                "Cremosa y liviana, buena opción para las noches frías.",
                                "1. Corta el zapallo en cubos y cocínalo en agua con sal 15-18 minutos hasta que esté muy blando.\n2. Sofríe la cebolla en aceite en una olla aparte hasta transparentar.\n3. Escurre el zapallo (reservando un poco del agua) y agrégalo a la olla con la cebolla.\n4. Licua o machaca todo junto con un poco de leche hasta lograr una crema suave.\n5. Vuelve a calentar a fuego bajo, ajusta la sal y sirve caliente.",
                                30, 2, "Media",
                                ing("Zapallo", 400, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Leche", 100, UnidadMedida.MILILITROS),
                                ing("Aceite", 10, UnidadMedida.MILILITROS),
                                ing("Sal", 5, UnidadMedida.GRAMOS));

                receta(ings, "Arroz con Atún Gratinado al Horno",
                                "Arroz con atún cubierto de queso, gratinado en el horno.",
                                "1. Cocina el arroz como de costumbre.\n2. Escurre el atún y mézclalo con el arroz cocido y la salsa de tomate.\n3. Coloca la mezcla en una fuente para horno.\n4. Cubre con queso rallado por encima.\n5. Hornea a 200°C durante 10-12 minutos hasta que el queso esté dorado.\n6. Sirve caliente.",
                                35, 2, "Media",
                                ing("Arroz", 200, UnidadMedida.GRAMOS),
                                ing("Atún en lata", 160, UnidadMedida.GRAMOS),
                                ing("Salsa de tomate", 60, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 80, UnidadMedida.GRAMOS));

                receta(ings, "Fideos con Salchichas y Salsa de Tomate",
                                "Pasta con vienesas en salsa, rinde harto y gusta a todos.",
                                "1. Cocina los fideos en agua con sal según el tiempo del envase.\n2. Corta las vienesas en rodajas y saltéalas en aceite 3-4 minutos.\n3. Agrega la salsa de tomate y cocina 5 minutos más a fuego bajo.\n4. Escurre los fideos y mézclalos con la salsa y las vienesas.\n5. Sirve caliente, con queso rallado por encima si quieres.",
                                25, 2, "Media",
                                ing("Fideos / Pasta", 250, UnidadMedida.GRAMOS),
                                ing("Vienesas", 4, UnidadMedida.UNIDAD),
                                ing("Salsa de tomate", 150, UnidadMedida.GRAMOS),
                                ing("Aceite", 10, UnidadMedida.MILILITROS));

                // ---------- DIFÍCIL (2) ----------

                receta(ings, "Lasaña Exprés en Sartén (Individual)",
                                "Una lasaña sin horno, hecha en capas dentro de un sartén con tapa.",
                                "1. Sofríe la cebolla y el ajo en aceite hasta transparentar.\n2. Agrega la carne molida y cocina 6-8 minutos hasta dorar.\n3. Incorpora la salsa de tomate y cocina 10 minutos a fuego bajo.\n4. En el mismo sartén, arma capas alternando salsa, láminas de pasta (previamente hidratadas en agua caliente) y queso rallado.\n5. Tapa el sartén y cocina a fuego muy bajo 15-18 minutos hasta que la pasta esté blanda y el queso derretido.\n6. Deja reposar 5 minutos tapado antes de servir.",
                                45, 2, "Difícil",
                                ing("Fideos / Pasta", 120, UnidadMedida.GRAMOS),
                                ing("Carne molida", 200, UnidadMedida.GRAMOS),
                                ing("Salsa de tomate", 200, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 100, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Ajo", 1, UnidadMedida.UNIDAD));

                receta(ings, "Pastel de Papas Individual al Horno",
                                "Versión chica del pastel de papas, para una sola porción generosa.",
                                "1. Pela y cocina las papas en agua con sal 15-18 minutos hasta que estén blandas.\n2. Haz puré con las papas, la leche y la mantequilla.\n3. Sofríe la cebolla en aceite y agrega la carne molida, cocinando 6-8 minutos.\n4. En un molde pequeño para horno, coloca la mitad del puré, luego la carne, y cubre con el resto del puré.\n5. Espolvorea queso rallado por encima.\n6. Hornea a 200°C durante 15-18 minutos hasta dorar la superficie.\n7. Deja reposar unos minutos antes de servir.",
                                45, 1, "Difícil",
                                ing("Papas", 3, UnidadMedida.UNIDAD),
                                ing("Carne molida", 100, UnidadMedida.GRAMOS),
                                ing("Cebolla", 1, UnidadMedida.UNIDAD),
                                ing("Leche", 40, UnidadMedida.MILILITROS),
                                ing("Mantequilla", 10, UnidadMedida.GRAMOS),
                                ing("Queso rallado", 40, UnidadMedida.GRAMOS));
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
