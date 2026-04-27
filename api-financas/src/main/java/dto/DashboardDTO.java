package dto;

import java.math.BigDecimal;

public record DashboardDTO(
		BigDecimal totalReceitas,
	    BigDecimal totalDespesas,
	    BigDecimal saldoGeral) {

}
