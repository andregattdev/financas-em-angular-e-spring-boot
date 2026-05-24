package dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de resposta para Transacao — evita expor a entidade diretamente
 * e previne a serialização de dados sensíveis do Usuário (senha, tokenAtivacao, etc.)
 */
public record TransacaoResponseDTO(
    Long id,
    String descricao,
    BigDecimal valor,
    LocalDate data,
    String tipo,
    Long categoriaId,
    String categoriaNome,
    Boolean pago,
    String codigoParcelamento
) {}
