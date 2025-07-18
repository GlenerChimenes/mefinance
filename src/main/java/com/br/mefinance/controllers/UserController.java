package com.br.mefinance.controllers;

import com.br.mefinance.dto.UserDTO;
import com.br.mefinance.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> buscarUsuarioId(@PathVariable Long id) {
        UserDTO dto = userService.buscarUsuarioId(id);
        return ResponseEntity.ok().body(dto);
    }



}
