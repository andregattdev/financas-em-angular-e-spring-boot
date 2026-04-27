package app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import app.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	// O Spring vai gerar o SQL: SELECT * FROM tb_usuario WHERE token_ativacao = ?
    Optional<Usuario> findByTokenAtivacao(String token);
    
    // Aproveite e já deixe este pronto para o futuro Login
    Optional<Usuario> findByEmail(String email);

}
