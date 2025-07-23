package com.br.mefinance.services;

import com.br.mefinance.dto.GastoDTO;
import com.br.mefinance.entities.Gasto;
import com.br.mefinance.enuns.SituacaoGasto;
import com.br.mefinance.projections.GastoProjection;
import com.br.mefinance.repositorys.GastoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        entity.setSituacao(SituacaoGasto.PENDENTE);
        entity.setUser(dto.getUser());
    }

    public Page<GastoProjection> buscarTodosGastos(Long usuarioId, Pageable pageable) {
        Page<GastoProjection> page = repository.findByUserId(usuarioId, pageable);
        return page;
    }

    public Page<GastoProjection> buscarGastosFiltrados(Long usuarioId, String descricao, Pageable pageable) {
        Page<GastoProjection> page = repository.buscarGastosFiltrados(usuarioId, descricao, pageable);
        return page;
    }

    public GastoDTO pagarGasto(Long id) {
        Gasto entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gasto não encontrado"));

        if(entity.getSituacao() == SituacaoGasto.PAGO){
            throw new IllegalArgumentException("Gasto já está pago");
        }
        entity.setSituacao(SituacaoGasto.PAGO);
        entity.setDataPagamento(LocalDateTime.now());
        entity = repository.save(entity);

        return new GastoDTO(entity);
    }
}
