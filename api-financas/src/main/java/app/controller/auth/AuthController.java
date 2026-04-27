package app.controller.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Importa todos os métodos (Get, Post, Delete, etc)

import app.model.Usuario;
import app.service.TokenService;
import app.service.UsuarioService;
import dto.LoginRequest;
import dto.LoginResponse;
import dto.RegistroRequest;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private TokenService tokenService;

	// 1. Registrar Usuário
	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest dados) {
	    // Aqui você converte o DTO para a Entidade dentro do Service
	    Usuario novoUsuario = usuarioService.registrar(dados); 
	    return ResponseEntity.ok(novoUsuario);
	}

	// 2. Confirmar Cadastro (via E-mail)
	@GetMapping("/confirmar")
	public ResponseEntity<String> confirmar(@RequestParam("token") String token) {
		boolean ativado = usuarioService.ativar(token);
		if (ativado) {
			return ResponseEntity.ok("Conta ativada com sucesso!");
		}
		return ResponseEntity.badRequest().body("Token inválido ou expirado.");
	}

	// 3. Listar todos os usuários
	@GetMapping("/usuarios")
	public ResponseEntity<List<Usuario>> listarTodos() {
		return ResponseEntity.ok(usuarioService.listarTodos());
	}

	// 4. Buscar um usuário específico por ID
	@GetMapping("/usuarios/{id}")
	public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
		return usuarioService.buscarPorId(id).map(ResponseEntity::ok) // Se achar, retorna 200 OK
				.orElse(ResponseEntity.notFound().build()); // Se não achar, retorna 404 Not Found
	}

	// 5. Soft Delete (Inativar Usuário)
	@DeleteMapping("/usuarios/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		usuarioService.softDelete(id);
		return ResponseEntity.noContent().build(); // Retorna 204 No Content (sucesso sem corpo)
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		Optional<Usuario> usuarioOpt = usuarioService.autenticar(loginRequest.email(), loginRequest.senha());

		if (usuarioOpt.isPresent()) {
			Usuario user = usuarioOpt.get();
			String token = tokenService.gerarToken(user);
			LoginResponse response = new LoginResponse(token, user.getNome(), user.getEmail());
			return ResponseEntity.ok(response);
		}

		return ResponseEntity.status(401).body("E-mail ou senha inválidos.");
	}
}