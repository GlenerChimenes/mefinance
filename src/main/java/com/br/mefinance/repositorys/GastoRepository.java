package com.br.mefinance.repositorys;

import com.br.mefinance.entities.Gasto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GastoRepository extends CrudRepository<Gasto, Long> {


    List<Gasto> findByUserIdAndPeriodo(@Param("user_id") Long userId, @Param("periodo") Integer periodo);
}
