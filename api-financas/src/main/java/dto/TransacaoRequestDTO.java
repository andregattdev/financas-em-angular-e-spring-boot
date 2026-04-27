package dto;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import app.model.TipoTransacao;

public record TransacaoRequestDTO(
	    @NotBlank String descricao,
	    @NotNull @Positive BigDecimal valor,
	    @NotNull LocalDate data,
	    @NotNull TipoTransacao tipo,
	    @NotNull Long categoriaId
	) {}
