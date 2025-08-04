package com.analytiCore.service;

import com.analytiCore.model.Job;
import com.analytiCore.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);
    private final JobRepository jobRepository;

    public AnalysisService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job analizarTexto(Long jobId) {
        logger.info("Iniciando análisis para el Job con ID: {}", jobId);

        Job job = jobRepository.findById(jobId).orElseThrow(() -> {
            logger.error("Job no encontrado con ID: {}", jobId);
            return new RuntimeException("Job no encontrado");
        });

        // Validación del texto
        if (job.getTexto() == null || job.getTexto().trim().isEmpty()) {
            logger.error("El texto para el Job con ID: {} está vacío o es nulo", jobId);
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }

        // Cambiar el estado a 'PENDIENTE' al inicio
        job.setEstado("PENDIENTE");
        logger.info("Estado del Job con ID: {} cambiado a 'PENDIENTE'", jobId);
        jobRepository.save(job);

        try {
            // Actualizar a 'PROCESANDO' al empezar el análisis
            job.setEstado("PROCESANDO");
            logger.info("Estado del Job con ID: {} cambiado a 'PROCESANDO'", jobId);
            jobRepository.save(job);

            String texto = job.getTexto().toLowerCase();
            String sentimiento = texto.contains("bueno") ? "POSITIVO" : "NEGATIVO";
            String palabrasClave = Arrays.stream(texto.split("\\s+"))
                    .distinct()
                    .limit(5)
                    .collect(Collectors.joining(", "));

            job.setSentimiento(sentimiento);
            job.setPalabrasClave(palabrasClave);

            // Actualizar a 'COMPLETADO' cuando el análisis se ha realizado con éxito
            job.setEstado("COMPLETADO");
            logger.info("Estado del Job con ID: {} cambiado a 'COMPLETADO'", jobId);
            return jobRepository.save(job);

        } catch (Exception e) {
            // Si hay un error, actualizar el estado a 'ERROR'
            job.setEstado("ERROR");
            logger.error("Error al procesar el Job con ID: {}", jobId, e);
            jobRepository.save(job);
            throw new RuntimeException("Error al procesar el análisis", e);
        }
    }
}
