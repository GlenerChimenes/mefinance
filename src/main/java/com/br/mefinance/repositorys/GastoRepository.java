package com.br.mefinance.repositorys;

import com.br.mefinance.entities.Gasto;
import com.br.mefinance.projections.GastoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GastoRepository extends CrudRepository<Gasto, Long> {


    List<Gasto> findByUserIdAndPeriodo(@Param("user_id") Long userId, @Param("periodo") Integer periodo);

    @Query("SELECT g.id AS id, g.descricao AS descricao, g.valor AS valor, g.periodo AS periodo, g.dataVencimento AS dataVencimento " +
            " FROM Gasto g " +
            " WHERE g.user.id =:user_id " +
            " ORDER BY g.periodo DESC"
    )
    Page<GastoProjection> findByUserId(@Param("user_id") Long userId, Pageable pageable);

    @Query("SELECT g.id AS id, g.descricao AS descricao, g.valor AS valor, g.periodo AS periodo, g.dataVencimento AS dataVencimento " +
            "FROM Gasto g " +
            "WHERE g.user.id = :user_id AND LOWER(g.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')) " +
            "ORDER BY g.periodo DESC")
    Page<GastoProjection> buscarGastosFiltrados(@Param("user_id") Long usuarioId, @Param("descricao") String descricao, Pageable pageable);
}
