package com.br.mefinance.repositorys;

import com.br.mefinance.entities.Gasto;
import com.br.mefinance.projections.GastoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {


    @Query("""
    SELECT g
    FROM Gasto g
    WHERE g.user.id = :userId
      AND g.periodo = :periodo
    ORDER BY g.dataVencimento ASC
""")
    List<Gasto> findByUserIdAndPeriodo(
            @Param("userId") Long userId,
            @Param("periodo") Integer periodo
    );

    @Query("""
                SELECT 
                    g.id AS id,
                    g.descricao AS descricao,
                    g.valor AS valor,
                    g.periodo AS periodo,
                    g.dataVencimento AS dataVencimento,
                    g.dataPagamento AS dataPagamento,
                    g.situacao AS situacao,
                    c.id AS idCategoria,
                    c.nome AS nomeCategoria
                FROM Gasto g
                LEFT JOIN g.categoria c
                WHERE g.user.id = :user_id
                AND   g.periodo = :periodo            
                ORDER BY g.periodo DESC
            """)
    Page<GastoProjection> findByUserId(@Param("user_id") Long userId, @Param("periodo") Integer periodo, Pageable pageable);

    @Query("""
    SELECT 
        g.id AS id,
        g.descricao AS descricao,
        g.valor AS valor,
        g.periodo AS periodo,
        g.dataVencimento AS dataVencimento,
        g.dataPagamento AS dataPagamento,
        g.situacao AS situacao,
        c.id AS idCategoria,
        c.nome AS nomeCategoria
    FROM Gasto g
    LEFT JOIN g.categoria c
    WHERE g.user.id = :user_id
      AND LOWER(g.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))
    ORDER BY g.periodo DESC
""")
    Page<GastoProjection> buscarGastosFiltrados(@Param("user_id") Long userId, @Param("descricao") String descricao, Pageable pageable);

    @Modifying
    @Query(value =
            "insert into TB_GASTO                   " +
                    "(descricao,                        " +
                    " valor,            " +
                    " periodo,                  " +
                    " data_vencimento,                        " +
                    " situacao,                     " +
                    " data_pagamento,                      " +
                    " user_id)                      " +
                    "SELECT g.descricao,         " +
					"       g.valor,    " +
                    "       :periodoReplicar,    " +
                    "       :dataVencimento,         " +
                    "       'PENDENTE',           " +
                    "       NULL,               " +
                    "       :user_id               " +
                    "FROM TB_GASTO g                " +
                    " where g.user_id = :user_id           " +
                    " and   g.periodo = :periodoAtual      " ,
            nativeQuery = true)
    @Transactional
    void replicarGastos(@Param("user_id") Long userId,
                        @Param("periodoAtual") Integer periodoAtual,
                        @Param("periodoReplicar") Integer periodoReplicar,
                        @Param("dataVencimento") LocalDate dataVencimento);
}
