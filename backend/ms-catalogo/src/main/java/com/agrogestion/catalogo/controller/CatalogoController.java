package com.agrogestion.catalogo.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
public class CatalogoController {

    @GetMapping("/predios")
    public List<Map<String, Object>> listarPredios() {
        return List.of(
            Map.of("id", "san_isidro", "nombre", "Fundo San Isidro", "ubicacion", "Quillota, Región de Valparaíso", "cultivo", "Palto Hass", "superficieHa", 12.5),
            Map.of("id", "las_palmas", "nombre", "Fundo Las Palmas", "ubicacion", "La Cruz, Región de Valparaíso", "cultivo", "Cítricos (Mandarino W. Murcott)", "superficieHa", 8.4),
            Map.of("id", "el_boldo", "nombre", "Agrícola El Boldo", "ubicacion", "Hijuelas, Región de Valparaíso", "cultivo", "Tomate Indeterminado", "superficieHa", 4.2)
        );
    }

    @GetMapping("/requerimientos")
    public Map<String, Object> obtenerRequerimiento(
            @RequestParam String cultivo,
            @RequestParam String etapa) {
        if (cultivo.toLowerCase().contains("tomate")) {
            return Map.of("cultivo", cultivo, "etapa", etapa, "n", 140.0, "p", 55.0, "k", 220.0, "phOptimo", 5.8);
        }
        if (cultivo.toLowerCase().contains("cítricos")) {
            return Map.of("cultivo", cultivo, "etapa", etapa, "n", 110.0, "p", 50.0, "k", 160.0, "phOptimo", 6.1);
        }
        return Map.of("cultivo", cultivo, "etapa", etapa, "n", 120.0, "p", 45.0, "k", 180.0, "phOptimo", 5.9);
    }
}