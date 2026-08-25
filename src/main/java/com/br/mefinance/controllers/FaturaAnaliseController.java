package com.br.mefinance.controllers;

import com.br.mefinance.dto.FaturaAnaliseDTO;
import com.br.mefinance.services.FaturaAnaliseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;

@RestController
@RequestMapping("/faturas")
public class FaturaAnaliseController {

    private final FaturaAnaliseService service;

    public FaturaAnaliseController(FaturaAnaliseService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_OPERATOR')")
    @PostMapping(value = "/analisar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FaturaAnaliseDTO> analisar(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "mesReferencia", required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesReferencia,
            @RequestParam(value = "termo", required = false, defaultValue = "") String termo) {
        return ResponseEntity.ok(service.analisar(arquivo, mesReferencia, termo));
    }
}
