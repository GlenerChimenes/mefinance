package com.br.mefinance.controllers;

import com.br.mefinance.dto.FaturaAnaliseDTO;
import com.br.mefinance.dto.FaturaArquivoDTO;
import com.br.mefinance.services.FaturaArquivoStorageService;
import com.br.mefinance.services.FaturaAnaliseService;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/faturas")
public class FaturaAnaliseController {

    private final FaturaAnaliseService service;
    private final FaturaArquivoStorageService storageService;

    public FaturaAnaliseController(
            FaturaAnaliseService service,
            FaturaArquivoStorageService storageService) {
        this.service = service;
        this.storageService = storageService;
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_OPERATOR')")
    @PostMapping(value = "/analisar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FaturaAnaliseDTO> analisar(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "mesReferencia", required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesReferencia,
            @RequestParam(value = "termo", required = false, defaultValue = "") String termo,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = usuarioId(jwt);
        YearMonth referencia = mesReferencia == null ? YearMonth.now() : mesReferencia;

        storageService.validarPeriodoDisponivel(arquivo, usuarioId, referencia);
        FaturaAnaliseDTO analise = service.analisar(arquivo, referencia, termo);
        storageService.salvar(arquivo, usuarioId, referencia);
        return ResponseEntity.ok(analise);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_OPERATOR')")
    @GetMapping("/arquivos")
    public ResponseEntity<List<FaturaArquivoDTO>> listarArquivos(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(storageService.listar(usuarioId(jwt)));
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_OPERATOR')")
    @GetMapping("/arquivos/{id}")
    public ResponseEntity<Resource> baixarArquivo(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {
        FaturaArquivoStorageService.FaturaDownload download =
                storageService.carregar(id, usuarioId(jwt));
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(download.nomeArquivo(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.tipoConteudo()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(download.recurso());
    }

    private Long usuarioId(Jwt jwt) {
        Object claim = jwt == null ? null : jwt.getClaim("userId");
        if (claim instanceof Number numero) {
            return numero.longValue();
        }
        if (claim instanceof String texto) {
            try {
                return Long.valueOf(texto);
            } catch (NumberFormatException ignored) {
                // Tratado abaixo.
            }
        }
        throw new IllegalStateException("O token autenticado não contém um usuário válido.");
    }
}
