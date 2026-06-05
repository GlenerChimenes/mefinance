package com.br.mefinance.controllers;

import com.br.mefinance.dto.UserAccessLogDTO;
import com.br.mefinance.services.UserAccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios/acessos")
public class UserAccessLogController {

    @Autowired
    private UserAccessLogService service;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserAccessLogDTO>> listar(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserAccessLogDTO> result = service.listarUltimosAcessos(pageable);

        return ResponseEntity.ok(result);
    }
}