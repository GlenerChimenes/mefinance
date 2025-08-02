package com.br.mefinance.controllers;

import com.br.mefinance.dto.GastoDTO;
import com.br.mefinance.projections.GastoProjection;
import com.br.mefinance.services.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ROLE_CLIENT')")
    @GetMapping
    public ResponseEntity<List<GastoDTO>> buscarGastosUsuario(@RequestParam Long usuarioId, @RequestParam Integer periodo) {
        List<GastoDTO> listDto = gastoService.buscarGastosUsuario(usuarioId, periodo);
        return ResponseEntity.ok().body(listDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT')")
    @GetMapping(value = "/filtrados")
    public ResponseEntity<Page<GastoProjection>> buscarGastosFiltrados(@RequestParam(value = "usuarioId", defaultValue = "0") Long usuarioId,
                                                                       @RequestParam(value = "descricao", defaultValue = "0") String descricao,
                                                                      Pageable pageable){
        Page<GastoProjection> page = gastoService.buscarGastosFiltrados(usuarioId, descricao, pageable);
        return ResponseEntity.ok().body(page);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT')")
    @GetMapping(value = "/todos")
    public ResponseEntity<Page<GastoProjection>> buscarTodosGastos(@RequestParam(value = "usuarioId", defaultValue = "0") Long usuarioId,
                                                                   Pageable pageable){
        Page<GastoProjection> page = gastoService.buscarTodosGastos(usuarioId, pageable);
        return ResponseEntity.ok().body(page);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT')")
    @PostMapping
    public ResponseEntity<GastoDTO> inserirGasto(@Validated @RequestBody GastoDTO dto) {
          dto =  gastoService.inserir(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    // Alterar gasto
    @PreAuthorize("hasAnyRole('ROLE_CLIENT')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<GastoDTO> alterarGasto(@PathVariable Long id, @Validated @RequestBody GastoDTO dto) {
        GastoDTO newDto = gastoService.update(id, dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }


    // Deletar gasto
    @PreAuthorize("hasAnyRole('ROLE_CLIENT')")
    @GetMapping(value = "/replicar")
    public ResponseEntity<Void> replicarGasto(@RequestParam(value = "userId") Long userId,
                                              @RequestParam(value = "periodoAtual") Integer periodoAtual,
                                              @RequestParam(value = "periodoReplicar") Integer periodoReplicar ){
        gastoService.replicarGastos(userId, periodoAtual, periodoReplicar);
        return ResponseEntity.noContent().build();

    }
    @PreAuthorize("hasAnyRole('ROLE_CLIENT')")
    @GetMapping(value = "/pagarGasto/{id}")
    public ResponseEntity<GastoDTO> pagar(@PathVariable Long id, @RequestParam(value = "userId") Long userId){
        GastoDTO dto = gastoService.pagarGasto(id, userId);
        return ResponseEntity.ok(dto);
    }

}
