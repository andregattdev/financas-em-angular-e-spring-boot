package app.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_transacao")
@Getter
@Setter
@NoArgsConstructor
public class Transacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String descricao;

	@Column(nullable = false)
	private BigDecimal valor;

	@Column(nullable = false)
	private LocalDate data;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoTransacao tipo;

	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@Column(columnDefinition = "boolean default false")
	private Boolean pago = false;

	@Column(name = "codigo_parcelamento")
	private String codigoParcelamento;

	public Transacao(Long id, String descricao, BigDecimal valor, LocalDate data, TipoTransacao tipo,
			Categoria categoria, Usuario usuario, Boolean pago) {
		this.id = id;
		this.descricao = descricao;
		this.valor = valor;
		this.data = data;
		this.tipo = tipo;
		this.categoria = categoria;
		this.usuario = usuario;
		this.pago = pago;
	}
}
