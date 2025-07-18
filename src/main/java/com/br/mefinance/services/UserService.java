package com.br.mefinance.services;

import com.br.mefinance.dto.UserDTO;
import com.br.mefinance.entities.Role;
import com.br.mefinance.entities.User;
import com.br.mefinance.projections.UserDetailsProjection;
import com.br.mefinance.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(email);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User nao encontrado");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(result.getFirst().getPassword());
        for (UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }

    public UserDTO buscarUsuarioId(Long id) {
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(() -> new RuntimeException("Erro ao buscar usuraio"));
        return new UserDTO(entity);
    }

}
