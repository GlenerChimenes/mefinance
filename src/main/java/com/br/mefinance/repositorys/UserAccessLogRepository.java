package com.br.mefinance.repositorys;

import com.br.mefinance.entities.UserAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccessLogRepository extends JpaRepository<UserAccessLog, Long> {

}
