package com.br.mefinance.services;

import com.br.mefinance.dto.GastoDTO;
import com.br.mefinance.entities.Gasto;
import com.br.mefinance.enuns.SituacaoGasto;
import com.br.mefinance.projections.GastoProjection;
import com.br.mefinance.repositorys.GastoRepository;
import com.br.mefinance.services.exceptions.BusinessException;
import com.br.mefinance.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GastoService {

    @Autowired
    private GastoRepository repository;

    @Transactional(readOnly = true)
    public List<GastoDTO> buscarGastosUsuario(Long userId, Integer periodo) {
        List<Gasto> entity = repository.findByUserIdAndPeriodo(userId, periodo);
       return entity.stream()
                .map(o -> new GastoDTO(o)).collect(Collectors.toList());
    }

    @Transactional
    public GastoDTO inserir(GastoDTO dto) {
        Gasto entity = new Gasto();
        copiaDtoParaEntidade(dto, entity);
        entity = repository.save(entity);
        return new GastoDTO(entity);
    }

    private void copiaDtoParaEntidade(GastoDTO dto, Gasto entity) {
        entity.setDescricao(dto.getDescricao());
        entity.setDataVencimento(dto.getDataVencimento());
        entity.setValor(dto.getValor());
        entity.setPeriodo(dto.getPeriodo());
        entity.setSituacao(SituacaoGasto.PENDENTE);
        entity.setUser(dto.getUser());
    }

    @Transactional(readOnly = true)
    public Page<GastoProjection> buscarTodosGastos(Long usuarioId, Pageable pageable) {
            return repository.findByUserId(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<GastoProjection> buscarGastosFiltrados(Long usuarioId, String descricao, Pageable pageable) {
        Page<GastoProjection> page = repository.buscarGastosFiltrados(usuarioId, descricao, pageable);
        return page;
    }

    public GastoDTO pagarGasto(Long id, Long userId) {
        Gasto entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado"));

        if(!entity.getUser().getId().equals(userId)){
            throw new BusinessException("Este gasto não pertence ao usuário informado");
        }
        if(entity.getSituacao() == SituacaoGasto.PAGO){
            throw new BusinessException("Esse gasto já está pago");
        }
        entity.setSituacao(SituacaoGasto.PAGO);
        entity.setDataPagamento(LocalDateTime.now());
        entity = repository.save(entity);

        return new GastoDTO(entity);
    }

    @Transactional
    public void replicarGastos(Long userId, Integer periodoAtual, Integer periodoReplicar) {
        List<Gasto> entity = repository.findByUserIdAndPeriodo(userId, periodoReplicar);
        if(!entity.isEmpty()){
            throw new BusinessException("Já existe gastos para o mês escolhido para replicacao");
        }

        LocalDate dataReplicar = tranformaPeriodoEmData(periodoReplicar);
        repository.replicarGastos(userId, periodoAtual, periodoReplicar, dataReplicar);
    }

    private LocalDate tranformaPeriodoEmData(Integer periodoReplicar) {
        String periodoStr = String.format("%06d", periodoReplicar);
        int mes = Integer.parseInt(periodoStr.substring(0, 2));
        int ano = Integer.parseInt(periodoStr.substring(2, 6));
        return LocalDate.of(ano, mes, 10);
    }

    @Transactional
    public GastoDTO update(Long id, GastoDTO dto) {
        try {
            Gasto entity = repository.getReferenceById(id);
            copiaDtoParaEntidadeUpdate(dto, entity);
            entity = repository.save(entity);
            return new GastoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encotrado " + id);
        }
    }

    private void copiaDtoParaEntidadeUpdate(GastoDTO dto, Gasto entity) {
        entity.setDescricao(dto.getDescricao());
        entity.setDataVencimento(dto.getDataVencimento());
        entity.setValor(dto.getValor());
        entity.setPeriodo(dto.getPeriodo());
        if(dto.getSituacao().equals(entity.getSituacao())){
            entity.setDataPagamento(dto.getDataPagamento());
        }else if (dto.getSituacao().equals(SituacaoGasto.PAGO)) {
            entity.setDataPagamento(LocalDateTime.now());
        } else {
            entity.setDataPagamento(null);
        }
        entity.setSituacao(dto.getSituacao());
        entity.setUser(dto.getUser());
    }
}
