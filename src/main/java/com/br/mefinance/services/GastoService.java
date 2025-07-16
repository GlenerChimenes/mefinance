package com.br.mefinance.services;

import com.br.mefinance.dto.GastoDTO;
import com.br.mefinance.entities.Gasto;
import com.br.mefinance.repositorys.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GastoService {

    @Autowired
    private GastoRepository repository;

    public List<GastoDTO> buscarGastosUsuario(Long usuarioId, Integer periodo) {
        List<Gasto> entity = repository.findByUsuarioIdAndPeriodo(usuarioId, periodo);
       return entity.stream()
                .map(o -> new GastoDTO(o)).collect(Collectors.toList());
    }
}
