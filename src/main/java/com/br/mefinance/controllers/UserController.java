package com.br.mefinance.controllers;

import com.br.mefinance.dto.UserDTO;
import com.br.mefinance.dto.UserInsertDTO;
import com.br.mefinance.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> cadastrar(@Valid @RequestBody UserInsertDTO dto) {
        UserDTO userDTO = userService.cadastrarUsuario(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(userDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> buscarUsuarioId(@PathVariable Long id) {
        UserDTO dto = userService.buscarUsuarioId(id);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> findMe() {
        UserDTO dto = userService.getMe();
        return ResponseEntity.ok().body(dto);
    }

}
