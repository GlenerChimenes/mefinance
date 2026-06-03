package com.br.mefinance.services;

import com.br.mefinance.dto.UserDTO;
import com.br.mefinance.dto.UserInsertDTO;
import com.br.mefinance.entities.Role;
import com.br.mefinance.entities.User;
import com.br.mefinance.projections.UserDetailsProjection;
import com.br.mefinance.repositorys.UserRepository;
import com.br.mefinance.services.exceptions.BusinessException;
import com.br.mefinance.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(email);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User não encontrado");
        }

        User user = new User();
        user.setId(result.getFirst().getUserId());
        user.setEmail(email);
        user.setPassword(result.getFirst().getPassword());
        for (UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }

    public UserDTO buscarUsuarioId(Long id) {
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Erro ao buscar usuraio"));
        return new UserDTO(entity);
    }

    protected User authenticated(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");

           return repository.findByEmail(username).get();
        }catch (Exception e){
            throw new UsernameNotFoundException("Email not found");
        }

    }

    @Transactional(readOnly = true)
    public UserDTO getMe() {
        User user = authenticated();
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO cadastrarUsuario(UserInsertDTO dto) {
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("E-mail já cadastrado");
        }

        User entity = new User();
        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setRendaMensal(dto.getRendaMensal());

        Role roleClient = new Role();
        roleClient.setId(3L);
        roleClient.setAuthority("ROLE_CLIENT");

        entity.addRole(roleClient);

        entity = repository.save(entity);

        return new UserDTO(entity);
    }
}
