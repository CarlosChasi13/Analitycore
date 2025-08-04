package com.analytiCore.controller;

import com.analytiCore.model.Job;
import com.analytiCore.service.AnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analyze")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public Job procesar(@RequestParam Long jobId) {
        return analysisService.analizarTexto(jobId);
    }
}
