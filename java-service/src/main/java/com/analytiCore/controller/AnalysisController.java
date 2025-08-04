package com.analytiCore.controller;

import com.analytiCore.model.Job;
import com.analytiCore.service.AnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j 
@RestController
@RequestMapping("/analyze")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/{jobId}")
    public Job procesar(@PathVariable Long jobId) {
        log.info("Recibiendo solicitud para procesar el trabajo con jobId: {}", jobId);

        Job job;
        try {
            job = analysisService.analizarTexto(jobId);
            log.info("El análisis para jobId: {} ha sido completado con éxito.", jobId);
        } catch (Exception e) {
            log.error("Error al procesar el análisis para jobId: {}", jobId, e);
            throw e;  // Vuelve a lanzar la excepción para que Spring maneje el error
        }

        return job;
    }
}
