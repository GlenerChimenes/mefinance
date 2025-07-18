package com.br.mefinance.services;

import com.br.mefinance.dto.GastoDTO;
import com.br.mefinance.entities.Gasto;
import com.br.mefinance.repositorys.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GastoService {

    @Autowired
    private GastoRepository repository;

    public List<GastoDTO> buscarGastosUsuario(Long userId, Integer periodo) {
        List<Gasto> entity = repository.findByUserIdAndPeriodo(userId, periodo);
       return entity.stream()
                .map(o -> new GastoDTO(o)).collect(Collectors.toList());
    }

    @Transactional
    public GastoDTO inserir(GastoDTO dto) {
        Gasto entity = new Gasto();
        copiaDtpParaEntidade(dto, entity);
        entity = repository.save(entity);
        return new GastoDTO(entity);
    }

    private void copiaDtpParaEntidade(GastoDTO dto, Gasto entity) {
        entity.setDescricao(dto.getDescricao());
        entity.setDataVencimento(dto.getDataVencimento());
        entity.setValor(dto.getValor());
        entity.setPeriodo(dto.getPeriodo());
        entity.setUser(dto.getUsuario());
    }
}
