package com.br.mefinance.controllers;

import com.br.mefinance.dto.GastoDTO;
import com.br.mefinance.dto.ResumoGastosDTO;
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

import java.net.URI;

@RestController
@RequestMapping("/gastos")
public class GastoController {

    @Autowired
    private GastoService gastoService;

    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_CLIENT')")
    @GetMapping
    public ResponseEntity<ResumoGastosDTO> buscarGastosUsuario(@RequestParam Long usuarioId, @RequestParam Integer periodo) {
        ResumoGastosDTO dto = gastoService.buscarGastosUsuario(usuarioId, periodo);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_CLIENT')")
    @GetMapping(value = "/filtrados")
    public ResponseEntity<Page<GastoProjection>> buscarGastosFiltrados(@RequestParam(value = "usuarioId", defaultValue = "0") Long usuarioId,
                                                                       @RequestParam(value = "descricao", defaultValue = "0") String descricao,
                                                                      Pageable pageable){
        Page<GastoProjection> page = gastoService.buscarGastosFiltrados(usuarioId, descricao, pageable);
        return ResponseEntity.ok().body(page);
    }

    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_CLIENT')")
    @GetMapping(value = "/todos")
    public ResponseEntity<Page<GastoProjection>> buscarTodosGastos(@RequestParam(value = "usuarioId", defaultValue = "0") Long usuarioId,
                                                                   @RequestParam(value = "periodo", required = false) Integer periodo,
                                                                   Pageable pageable){
        Page<GastoProjection> page = gastoService.buscarTodosGastos(usuarioId, periodo, pageable);
        return ResponseEntity.ok().body(page);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_OPERATOR')")
    @PostMapping
    public ResponseEntity<GastoDTO> inserirGasto(@Validated @RequestBody GastoDTO dto) {
          dto =  gastoService.inserir(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_OPERATOR')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<GastoDTO> alterarGasto(@PathVariable Long id, @Validated @RequestBody GastoDTO dto) {
        GastoDTO newDto = gastoService.update(id, dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }


    // Deletar gasto
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_OPERATOR')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gastoService.deleteGasto(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_OPERATOR')")
    @GetMapping(value = "/replicar")
    public ResponseEntity<Void> replicarGasto(@RequestParam(value = "userId") Long userId,
                                              @RequestParam(value = "periodoAtual") Integer periodoAtual,
                                              @RequestParam(value = "periodoReplicar") Integer periodoReplicar ){
        gastoService.replicarGastos(userId, periodoAtual, periodoReplicar);
        return ResponseEntity.noContent().build();

    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_OPERATOR')")
    @GetMapping(value = "/pagarGasto/{id}")
    public ResponseEntity<GastoDTO> pagar(@PathVariable Long id, @RequestParam(value = "userId") Long userId){
        GastoDTO dto = gastoService.pagarGasto(id, userId);
        return ResponseEntity.ok(dto);
    }

}
