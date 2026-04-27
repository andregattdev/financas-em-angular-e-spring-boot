package dto;

import java.math.BigDecimal;

public record HistoricoMensalDTO(
		String mesAno, 
		BigDecimal receitas, 
		BigDecimal despesas) {

}
