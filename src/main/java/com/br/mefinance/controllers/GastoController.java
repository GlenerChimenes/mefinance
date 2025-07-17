package com.br.mefinance.controllers;

import com.br.mefinance.dto.GastoDTO;
import com.br.mefinance.services.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/gastos")
public class GastoController {

    BigDecimal rendaBruta = new BigDecimal("12500.00");

    @Autowired
    private GastoService gastorService;

    @GetMapping
    public ResponseEntity<List<GastoDTO>> buscarGastosUsuario(@RequestParam Long usuarioId, @RequestParam Integer periodo) {
        List<GastoDTO> listDto = gastorService.buscarGastosUsuario(usuarioId, periodo);
        return ResponseEntity.ok().body(listDto);
    }

    // Buscar gastos ordenado

    // Buscar todos os gastos

    // Inserir gasto

    @PostMapping
    public ResponseEntity<GastoDTO> inserir(@Validated @RequestBody GastoDTO dto) {
          dto =  gastorService.inserir(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    // Alterar gasto

    // Deletar gasto

    // Replicar gasto

    // Pagar gasto
}
