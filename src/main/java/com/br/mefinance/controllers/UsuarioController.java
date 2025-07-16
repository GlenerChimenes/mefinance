package com.br.mefinance.controllers;

import com.br.mefinance.dto.UsuarioDTO;
import com.br.mefinance.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioServico;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioId(@PathVariable Long id) {
        UsuarioDTO dto = usuarioServico.buscarUsuarioId(id);
        return ResponseEntity.ok().body(dto);
    }



}
