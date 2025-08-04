package com.analytiCore.repository;

import com.analytiCore.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Buscar trabajos por estado (PENDIENTE, PROCESANDO, COMPLETADO, ERROR)
    List<Job> findByEstado(String estado);

    // Buscar trabajos por sentimiento (POSITIVO, NEGATIVO, etc.)
    List<Job> findBySentimiento(String sentimiento);

    // Buscar trabajos que contengan ciertas palabras clave (por ejemplo, "análisis", "texto")
    List<Job> findByPalabrasClaveContaining(String palabraClave);

    // Buscar trabajos que contengan un fragmento de texto específico
    List<Job> findByTextoContaining(String texto);

    // Método para actualizar el estado de un trabajo por jobId
    @Modifying
    @Transactional
    @Query("UPDATE Job j SET j.estado = :estado WHERE j.id = :jobId")
    void updateEstadoByJobId(String estado, Long jobId);

    // Método para buscar trabajos por un conjunto de estados
    List<Job> findByEstadoIn(List<String> estados);
}
