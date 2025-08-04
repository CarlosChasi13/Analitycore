package com.analytiCore.service;

import com.analytiCore.model.Job;
import com.analytiCore.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final JobRepository jobRepository;

    public AnalysisService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job analizarTexto(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setEstado("PROCESANDO");

        String texto = job.getTexto().toLowerCase();
        String sentimiento = texto.contains("bueno") ? "POSITIVO" : "NEGATIVO";
        String palabrasClave = Arrays.stream(texto.split("\\s+"))
                .distinct()
                .limit(5)
                .collect(Collectors.joining(", "));

        job.setSentimiento(sentimiento);
        job.setPalabrasClave(palabrasClave);
        job.setEstado("COMPLETADO");

        return jobRepository.save(job);
    }
}
