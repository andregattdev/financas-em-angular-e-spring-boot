package dto;

import java.math.BigDecimal;

public record FluxoDiarioDTO(
    String dia,
    BigDecimal receitas,
    BigDecimal despesas
) {}
