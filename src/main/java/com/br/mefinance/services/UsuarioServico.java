package com.br.mefinance.services;

import com.br.mefinance.dto.UsuarioDTO;
import com.br.mefinance.entities.Usuario;
import com.br.mefinance.repositorys.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServico {

    @Autowired
    private UsuarioRepository repository;


    public UsuarioDTO buscarUsuarioId(Long id) {
        Optional<Usuario> obj = repository.findById(id);
        Usuario entity = obj.orElseThrow(() -> new RuntimeException("Erro ao buscar usuraio"));
        return new UsuarioDTO(entity);
    }
}
