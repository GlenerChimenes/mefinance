package com.br.mefinance.controllers;

import com.br.mefinance.dto.GastoDTO;
import com.br.mefinance.projections.GastoProjection;
import com.br.mefinance.services.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private GastoService gastoService;

    @GetMapping
    public ResponseEntity<List<GastoDTO>> buscarGastosUsuario(@RequestParam Long usuarioId, @RequestParam Integer periodo) {
        List<GastoDTO> listDto = gastoService.buscarGastosUsuario(usuarioId, periodo);
        return ResponseEntity.ok().body(listDto);
    }

    // Buscar gastos filtrado
    @GetMapping(value = "/filtrados")
    public ResponseEntity<Page<GastoProjection>> buscarGastosFiltrados(@RequestParam(value = "usuarioId", defaultValue = "0") Long usuarioId,
                                                                       @RequestParam(value = "descricao", defaultValue = "0") String descricao,
                                                                      Pageable pageable){
        Page<GastoProjection> page = gastoService.buscarGastosFiltrados(usuarioId, descricao, pageable);
        return ResponseEntity.ok().body(page);
    }

    // Buscar todos os gastos
    @GetMapping(value = "/todos")
    public ResponseEntity<Page<GastoProjection>> buscarTodosGastos(@RequestParam(value = "usuarioId", defaultValue = "0") Long usuarioId,
                                                                   Pageable pageable){
        Page<GastoProjection> page = gastoService.buscarTodosGastos(usuarioId, pageable);
        return ResponseEntity.ok().body(page);
    }

    // Inserir gasto
    @PostMapping
    public ResponseEntity<GastoDTO> inserir(@Validated @RequestBody GastoDTO dto) {
          dto =  gastoService.inserir(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    // Alterar gasto


    // Deletar gasto

    // Replicar gasto

    // Pagar gasto
    @GetMapping(value = "/pagarGasto/{id}")
    public ResponseEntity<GastoDTO> pagar(@PathVariable Long id){
        GastoDTO dto = gastoService.pagarGasto(id);
        return ResponseEntity.ok(dto);
    }

}
