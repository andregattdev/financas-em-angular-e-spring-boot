package app.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_usuario")
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres!")
	private String nome;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String senha;

	@Column(nullable = false)
	private boolean ativo = false;

	private String tokenAtivacao;

	public Usuario() {

	}

	public Usuario(Long id, String nome, String email, String senha, boolean ativo, String tokenAtivacao) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.ativo = ativo;
		this.tokenAtivacao = tokenAtivacao;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Por enquanto, retorna uma lista vazia ou um perfil padrão (ROLE_USER)
		return List.of();
	}

	@Override
	public String getPassword() {
		return this.senha; // O Spring precisa saber qual campo é a senha
	}

	@Override
	public String getUsername() {
		return this.email; // O Spring usa o e-mail como "username"
	}

	// Deixe todos como true para facilitar o teste inicial
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.ativo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getTokenAtivacao() {
		return tokenAtivacao;
	}

	public void setTokenAtivacao(String tokenAtivacao) {
		this.tokenAtivacao = tokenAtivacao;
	}

}
