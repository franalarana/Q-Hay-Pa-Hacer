package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.ComparacionIngredienteDTO;
import cl.quehaypahacer.backend.dto.EstadoIngrediente;
import cl.quehaypahacer.backend.dto.EstadoReceta;
import cl.quehaypahacer.backend.dto.RecetaComparadaDTO;
import cl.quehaypahacer.backend.model.IngredienteReceta;
import cl.quehaypahacer.backend.model.IngredienteUsuario;
import cl.quehaypahacer.backend.model.Receta;
import cl.quehaypahacer.backend.model.UnidadMedida;
import cl.quehaypahacer.backend.model.Usuario;
import cl.quehaypahacer.backend.repository.IngredienteUsuarioRepository;
import cl.quehaypahacer.backend.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparadorRecetasService {

    private final RecetaRepository recetaRepository;
    private final IngredienteUsuarioRepository ingredienteUsuarioRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<RecetaComparadaDTO> obtenerRecetasSugeridas(Jwt jwt) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        List<IngredienteUsuario> despensa = ingredienteUsuarioRepository.findByUsuarioOrderByIngredienteNombreAsc(usuario);
        
        // Mapear despensa por ID del ingrediente
        Map<Long, IngredienteUsuario> despensaMap = despensa.stream()
                .collect(Collectors.toMap(
                        item -> item.getIngrediente().getId(),
                        item -> item,
                        (item1, item2) -> item1
                ));

        List<Receta> todasLasRecetas = recetaRepository.findAllWithIngredientes();

        return todasLasRecetas.stream()
                .map(receta -> compararRecetaConDespensa(receta, despensaMap))
                .sorted(Comparator
                        .comparing(RecetaComparadaDTO::getPorcentajeCoincidencia).reversed()
                        .thenComparing(RecetaComparadaDTO::getIngredientesVerdes, Comparator.reverseOrder())
                        .thenComparing(RecetaComparadaDTO::getTitulo))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecetaComparadaDTO> obtenerMisRecetas(Jwt jwt) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        List<IngredienteUsuario> despensa = ingredienteUsuarioRepository.findByUsuarioOrderByIngredienteNombreAsc(usuario);
        
        Map<Long, IngredienteUsuario> despensaMap = despensa.stream()
                .collect(Collectors.toMap(
                        item -> item.getIngrediente().getId(),
                        item -> item,
                        (item1, item2) -> item1
                ));

        List<Receta> misRecetas = recetaRepository.findByCreadorOrderByTituloAsc(usuario);

        return misRecetas.stream()
                .map(receta -> compararRecetaConDespensa(receta, despensaMap))
                .collect(Collectors.toList());
    }

    @Transactional
    public RecetaComparadaDTO crearRecetaPropia(Jwt jwt, cl.quehaypahacer.backend.dto.CrearRecetaRequest req, cl.quehaypahacer.backend.repository.IngredienteRepository ingredienteRepository) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);

        Receta nueva = Receta.builder()
                .titulo(req.getTitulo())
                .descripcion(req.getDescripcion())
                .instrucciones(req.getInstrucciones())
                .tiempoMinutos(req.getTiempoMinutos())
                .porciones(req.getPorciones())
                .dificultad(req.getDificultad() != null ? req.getDificultad() : "Fácil")
                .imagenUrl(req.getImagenUrl() != null && !req.getImagenUrl().isBlank() 
                        ? req.getImagenUrl() 
                        : "https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600&auto=format&fit=crop&q=60")
                .creador(usuario)
                .build();

        Receta guardada = recetaRepository.save(nueva);

        List<IngredienteReceta> ingredientesList = new ArrayList<>();
        for (cl.quehaypahacer.backend.dto.IngredienteRecetaRequest irr : req.getIngredientes()) {
            cl.quehaypahacer.backend.model.Ingrediente ing = ingredienteRepository.findById(irr.getIngredienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado con id: " + irr.getIngredienteId()));

            IngredienteReceta ir = IngredienteReceta.builder()
                    .receta(guardada)
                    .ingrediente(ing)
                    .cantidadRequerida(irr.getCantidadRequerida())
                    .unidad(irr.getUnidad())
                    .opcional(irr.getOpcional() != null ? irr.getOpcional() : false)
                    .build();

            ingredientesList.add(ir);
        }

        guardada.setIngredientes(ingredientesList);
        guardada = recetaRepository.save(guardada);

        return obtenerDetalleReceta(jwt, guardada.getId());
    }

    @Transactional
    public RecetaComparadaDTO actualizarRecetaPropia(Jwt jwt, Long recetaId, cl.quehaypahacer.backend.dto.CrearRecetaRequest req, cl.quehaypahacer.backend.repository.IngredienteRepository ingredienteRepository) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        Receta receta = obtenerRecetaPropiaOrThrow(recetaId, usuario);

        receta.setTitulo(req.getTitulo());
        receta.setDescripcion(req.getDescripcion());
        receta.setInstrucciones(req.getInstrucciones());
        receta.setTiempoMinutos(req.getTiempoMinutos());
        receta.setPorciones(req.getPorciones());
        receta.setDificultad(req.getDificultad() != null ? req.getDificultad() : "Fácil");
        if (req.getImagenUrl() != null && !req.getImagenUrl().isBlank()) {
            receta.setImagenUrl(req.getImagenUrl());
        }

        // Reemplaza los ingredientes en la misma colección para que el orphanRemoval borre los anteriores
        receta.getIngredientes().clear();
        for (cl.quehaypahacer.backend.dto.IngredienteRecetaRequest irr : req.getIngredientes()) {
            cl.quehaypahacer.backend.model.Ingrediente ing = ingredienteRepository.findById(irr.getIngredienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado con id: " + irr.getIngredienteId()));

            receta.getIngredientes().add(IngredienteReceta.builder()
                    .receta(receta)
                    .ingrediente(ing)
                    .cantidadRequerida(irr.getCantidadRequerida())
                    .unidad(irr.getUnidad())
                    .opcional(irr.getOpcional() != null ? irr.getOpcional() : false)
                    .build());
        }

        recetaRepository.save(receta);
        return obtenerDetalleReceta(jwt, receta.getId());
    }

    @Transactional
    public void eliminarRecetaPropia(Jwt jwt, Long recetaId) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        Receta receta = obtenerRecetaPropiaOrThrow(recetaId, usuario);
        recetaRepository.delete(receta);
    }

    private Receta obtenerRecetaPropiaOrThrow(Long recetaId, Usuario usuario) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receta no encontrada con id: " + recetaId));
        if (receta.getCreador() == null || !receta.getCreador().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes editar o eliminar recetas creadas por ti");
        }
        return receta;
    }

    @Transactional(readOnly = true)
    public RecetaComparadaDTO obtenerDetalleReceta(Jwt jwt, Long recetaId) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        List<IngredienteUsuario> despensa = ingredienteUsuarioRepository.findByUsuarioOrderByIngredienteNombreAsc(usuario);
        
        Map<Long, IngredienteUsuario> despensaMap = despensa.stream()
                .collect(Collectors.toMap(
                        item -> item.getIngrediente().getId(),
                        item -> item,
                        (item1, item2) -> item1
                ));

        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada con id: " + recetaId));

        return compararRecetaConDespensa(receta, despensaMap);
    }


    private RecetaComparadaDTO compararRecetaConDespensa(Receta receta, Map<Long, IngredienteUsuario> despensaMap) {
        List<ComparacionIngredienteDTO> comparaciones = new ArrayList<>();
        int verdes = 0;
        int amarillos = 0;
        int rojos = 0;

        List<IngredienteReceta> ingredientesReceta = receta.getIngredientes();
        int totalIngredientes = ingredientesReceta.size();

        for (IngredienteReceta ir : ingredientesReceta) {
            Long ingId = ir.getIngrediente().getId();
            Double req = ir.getCantidadRequerida();
            UnidadMedida uReq = ir.getUnidad();

            IngredienteUsuario itemDespensa = despensaMap.get(ingId);
            Double disp = (itemDespensa != null) ? itemDespensa.getCantidad() : 0.0;
            UnidadMedida uDisp = (itemDespensa != null) ? itemDespensa.getUnidad() : uReq;

            // Normalización básica de unidades (ej: kg <-> g, L <-> ml)
            Double dispNormalizado = normalizarCantidad(disp, uDisp, uReq);

            EstadoIngrediente estado;
            String mensaje;

            if (itemDespensa == null || disp <= 0.0) {
                estado = EstadoIngrediente.ROJO;
                rojos++;
                mensaje = "No lo tienes en tu despensa";
            } else if (dispNormalizado >= req) {
                estado = EstadoIngrediente.VERDE;
                verdes++;
                mensaje = "Cantidad suficiente (" + disp + " " + uDisp.name().toLowerCase() + ")";
            } else {
                estado = EstadoIngrediente.AMARILLO;
                amarillos++;
                double falta = req - dispNormalizado;
                mensaje = "Tienes " + disp + " " + uDisp.name().toLowerCase() + " (te faltan " + String.format("%.1f", falta) + " " + uReq.name().toLowerCase() + ")";
            }

            comparaciones.add(ComparacionIngredienteDTO.builder()
                    .ingredienteId(ingId)
                    .nombreIngrediente(ir.getIngrediente().getNombre())
                    .categoria(ir.getIngrediente().getCategoria())
                    .cantidadRequerida(req)
                    .unidadRequerida(uReq)
                    .cantidadDisponible(disp)
                    .unidadDisponible(uDisp)
                    .estado(estado)
                    .opcional(ir.getOpcional())
                    .mensajeEstado(mensaje)
                    .build());
        }

        // Determinar estado general de la receta
        EstadoReceta estadoGeneral;
        if (totalIngredientes > 0 && verdes == totalIngredientes) {
            estadoGeneral = EstadoReceta.VERDE;
        } else if (verdes > 0 || amarillos > 0) {
            estadoGeneral = EstadoReceta.AMARILLO;
        } else {
            estadoGeneral = EstadoReceta.ROJO;
        }

        // Cálculo de coincidencia porcentual
        double score = 0.0;
        if (totalIngredientes > 0) {
            score = ((verdes * 1.0) + (amarillos * 0.5)) / totalIngredientes * 100.0;
            score = Math.round(score * 10.0) / 10.0;
        }

        return RecetaComparadaDTO.builder()
                .id(receta.getId())
                .titulo(receta.getTitulo())
                .descripcion(receta.getDescripcion())
                .instrucciones(receta.getInstrucciones())
                .tiempoMinutos(receta.getTiempoMinutos())
                .porciones(receta.getPorciones())
                .dificultad(receta.getDificultad())
                .imagenUrl(receta.getImagenUrl())
                .estadoGeneral(estadoGeneral)
                .porcentajeCoincidencia(score)
                .ingredientesVerdes(verdes)
                .ingredientesAmarillos(amarillos)
                .ingredientesRojos(rojos)
                .totalIngredientes(totalIngredientes)
                .ingredientes(comparaciones)
                .build();
    }

    private Double normalizarCantidad(Double cantidad, UnidadMedida origen, UnidadMedida destino) {
        if (origen == destino) {
            return cantidad;
        }
        // Kilos a Gramos
        if (origen == UnidadMedida.KILOGRAMOS && destino == UnidadMedida.GRAMOS) {
            return cantidad * 1000.0;
        }
        // Gramos a Kilos
        if (origen == UnidadMedida.GRAMOS && destino == UnidadMedida.KILOGRAMOS) {
            return cantidad / 1000.0;
        }
        // Litros a Mililitros
        if (origen == UnidadMedida.LITROS && destino == UnidadMedida.MILILITROS) {
            return cantidad * 1000.0;
        }
        // Mililitros a Litros
        if (origen == UnidadMedida.MILILITROS && destino == UnidadMedida.LITROS) {
            return cantidad / 1000.0;
        }
        return cantidad;
    }
}
