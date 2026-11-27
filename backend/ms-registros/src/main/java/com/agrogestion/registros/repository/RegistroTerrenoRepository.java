package com.agrogestion.registros.repository;

import com.agrogestion.registros.model.RegistroTerreno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistroTerrenoRepository extends JpaRepository<RegistroTerreno, Long> {
    List<RegistroTerreno> findAllByOrderByFechaMillisDesc();
}