package com.br.mefinance.repositorys;

import com.br.mefinance.entities.Usuario;
import com.br.mefinance.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    @Query(nativeQuery = true, value = """
				SELECT tb_usuario.email AS username, tb_usuario.senha, tb_role.id AS roleId, tb_role.authority
				FROM tb_usuario
				INNER JOIN tb_usuario_role ON tb_usuario.id = tb_usuario_role.usuario_id
				INNER JOIN tb_role ON tb_role.id = tb_usuario_role.role_id
				WHERE tb_usuario.email = :email
			""")
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);



}
