package app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import app.model.Usuario;
import app.repository.UsuarioRepository;
import dto.RegistroRequest;

@Service
public class UsuarioService implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuário no banco pelo e-mail
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
    }

	public Usuario registrar(RegistroRequest dados) {
	    // Primeiro, verificamos se o e-mail já existe buscando pelo e-mail que veio no DTO
	    Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(dados.email());

	    if (usuarioExistente.isPresent()) {
	        Usuario user = usuarioExistente.get();

	        // Caso 1: E-mail existe mas está inativo
	        if (!user.isAtivo()) {
	            user.setSenha(passwordEncoder.encode(dados.senha()));
	            user.setAtivo(false); 
	            user.setTokenAtivacao(UUID.randomUUID().toString());
	            // emailService.enviarEmailConfirmacao(user.getEmail(), user.getTokenAtivacao());
	            return usuarioRepository.save(user);
	        }
	        // Caso 2: E-mail já está ativo
	        else {
	            throw new RuntimeException("Erro: O e-mail '" + dados.email() + "' já está cadastrado e ativo.");
	        }
	    }

	    // Caso 3: E-mail novo - Criamos um novo objeto Usuario e preenchemos com os dados do DTO
	    Usuario novoUsuario = new Usuario();
	    novoUsuario.setNome(dados.nome());
	    novoUsuario.setEmail(dados.email());
	    
	    // Criptografia e Tokens
	    String senhaCriptografada = passwordEncoder.encode(dados.senha());
	    novoUsuario.setSenha(senhaCriptografada);
	    
	    String token = UUID.randomUUID().toString();
	    novoUsuario.setTokenAtivacao(token);
	    novoUsuario.setAtivo(false); // Mantemos false conforme sua regra de negócio

	    // emailService.enviarEmailConfirmacao(novoUsuario.getEmail(), token);

	    return usuarioRepository.save(novoUsuario);
	}
	
	public Optional<Usuario> autenticar(String email, String senhaDigitada) {
	    return usuarioRepository.findByEmail(email)
	        .filter(usuario -> usuario.isAtivo()) // Só loga se estiver ativado
	        .filter(usuario -> passwordEncoder.matches(senhaDigitada, usuario.getSenha()));
	}

	public boolean ativar(String token) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenAtivacao(token);
		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();
			usuario.setAtivo(true);
			usuario.setTokenAtivacao(null); // Limpa o token usado
			usuarioRepository.save(usuario);
			return true;
		}
		return false;
	}

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	// 2. Ver um usuário específico por ID
	public Optional<Usuario> buscarPorId(Long id) {
		return usuarioRepository.findById(id);
	}

	// 3. Soft Delete (Inativa o usuário em vez de apagar do Postgres)
	public void softDelete(Long id) {
		usuarioRepository.findById(id).ifPresent(usuario -> {
			usuario.setAtivo(false);
			// Você pode até adicionar um campo usuario.setExcluido(true) no futuro
			usuarioRepository.save(usuario);
		});
	}

}
