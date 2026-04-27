package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria,Long > {
	
	List<Categoria> findByUsuarioId(Long usuarioId);

}
