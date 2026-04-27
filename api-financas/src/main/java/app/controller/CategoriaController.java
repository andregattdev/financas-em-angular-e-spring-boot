package app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.context.SecurityContextHolder;
import app.model.Usuario;

import app.model.Categoria;
import app.model.Transacao;
import app.service.CategoriaService;
import dto.CategoriaRequestDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	@PostMapping
	public ResponseEntity<Categoria> criar(@RequestBody @Valid CategoriaRequestDTO dto) {
		// 1. Pega o usuário logado do contexto de segurança
		Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Categoria categoria = new Categoria();
		categoria.setNomeCategoria(dto.nomeCategoria());

		// 2. VINCULA O USUÁRIO À CATEGORIA (Isso evita o erro de coluna vazia)
		categoria.setUsuario(usuarioLogado);

		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.salvar(categoria));
	}

	@GetMapping
	public ResponseEntity<List<Categoria>> listar() {
		// 1. Pega o usuário logado (André - ID 7)
		Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// 2. Chama o Service passando o ID dele para filtrar
		List<Categoria> lista = categoriaService.listarPorUsuario(usuarioLogado.getId());

		return ResponseEntity.ok(lista);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Categoria> atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaRequestDTO dto) {
		Categoria categoriaAtualizada = categoriaService.atualizar(id, dto);
		return ResponseEntity.ok(categoriaAtualizada);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		categoriaService.deletar(id);
		return ResponseEntity.noContent().build();
	}

}
