package com.br.mefinance.controllers;

import com.br.mefinance.dto.GastoDTO;
import com.br.mefinance.services.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    // Inserir gasto

    // Alterar gasto

    // Deletar gasto

    // Replicar gasto

    // Pagar gasto
}
