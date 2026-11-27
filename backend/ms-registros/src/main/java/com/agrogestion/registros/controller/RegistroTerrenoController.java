package com.agrogestion.registros.controller;

import com.agrogestion.registros.model.RegistroTerreno;
import com.agrogestion.registros.repository.RegistroTerrenoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registros")
@CrossOrigin(origins = "*")
public class RegistroTerrenoController {

    private final RegistroTerrenoRepository repository;

    public RegistroTerrenoController(RegistroTerrenoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<RegistroTerreno> listarTodos() {
        return repository.findAllByOrderByFechaMillisDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroTerreno> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RegistroTerreno> crear(@RequestBody RegistroTerreno registro) {
        registro.setId(null);
        if (registro.getFechaMillis() == null) {
            registro.setFechaMillis(System.currentTimeMillis());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(registro));
    }

    @PostMapping("/sync")
    public ResponseEntity<List<RegistroTerreno>> sincronizarLote(@RequestBody List<RegistroTerreno> pendientes) {
        pendientes.forEach(r -> r.setId(null));
        return ResponseEntity.ok(repository.saveAll(pendientes));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroTerreno> actualizar(@PathVariable Long id, @RequestBody RegistroTerreno datos) {
        return repository.findById(id).map(existente -> {
            existente.setObservacionesFoliares(datos.getObservacionesFoliares());
            if (datos.getRecomendacion() != null) existente.setRecomendacion(datos.getRecomendacion());
            if (datos.getPh() != null) existente.setPh(datos.getPh());
            if (datos.getEc() != null) existente.setEc(datos.getEc());
            return ResponseEntity.ok(repository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}