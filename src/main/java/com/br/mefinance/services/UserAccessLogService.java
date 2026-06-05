package com.br.mefinance.services;

import com.br.mefinance.dto.UserAccessLogDTO;
import com.br.mefinance.entities.User;
import com.br.mefinance.entities.UserAccessLog;
import com.br.mefinance.repositorys.UserAccessLogRepository;
import com.br.mefinance.repositorys.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UserAccessLogService {

    @Autowired
    private UserAccessLogRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void registrarAcesso(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para registrar acesso"));

        HttpServletRequest request = getCurrentRequest();

        String ip = request != null ? getClientIp(request) : null;
        String userAgent = request != null ? request.getHeader("User-Agent") : null;

        UserAccessLog log = new UserAccessLog(user, ip, userAgent);

        repository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<UserAccessLogDTO> listarUltimosAcessos(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "dataAcesso")
        );

        return repository.findAll(sorted).map(UserAccessLogDTO::new);
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}