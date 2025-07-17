package com.br.mefinance.services;

import com.br.mefinance.dto.UsuarioDTO;
import com.br.mefinance.entities.Role;
import com.br.mefinance.entities.Usuario;
import com.br.mefinance.projections.UserDetailsProjection;
import com.br.mefinance.repositorys.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(email);
        if (result.size() == 0) {
            throw new UsernameNotFoundException("Usuario nao encontrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(result.get(0).getPassword());
        for (UserDetailsProjection projection : result) {
            usuario.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return usuario;
    }

    public UsuarioDTO buscarUsuarioId(Long id) {
        Optional<Usuario> obj = repository.findById(id);
        Usuario entity = obj.orElseThrow(() -> new RuntimeException("Erro ao buscar usuraio"));
        return new UsuarioDTO(entity);
    }

}
