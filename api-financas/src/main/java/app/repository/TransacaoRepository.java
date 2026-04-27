package app.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.model.TipoTransacao;
import app.model.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    // 1. Somar valores por tipo filtrando pelo Usuário
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo AND t.usuario.id = :usuarioId")
    BigDecimal sumByTipo(@Param("tipo") TipoTransacao tipo, @Param("usuarioId") Long usuarioId);

    // 2. Soma por tipo, mês, ano e usuário (Dashboard)
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo AND MONTH(t.data) = :mes AND YEAR(t.data) = :ano AND t.usuario.id = :usuarioId")
    BigDecimal sumByTipoAndMesEAno(
        @Param("tipo") TipoTransacao tipo, 
        @Param("mes") int mes, 
        @Param("ano") int ano, 
        @Param("usuarioId") Long usuarioId
    );

    // 3. Busca transações por mês, ano e usuário (Lista filtrada)
    @Query("SELECT t FROM Transacao t WHERE MONTH(t.data) = :mes AND YEAR(t.data) = :ano AND t.usuario.id = :usuarioId")
    Page<Transacao> findByMesEAno(
        @Param("mes") int mes, 
        @Param("ano") int ano, 
        @Param("usuarioId") Long usuarioId, 
        Pageable pageable
    );

    // 4. Listagem geral paginada por usuário
    Page<Transacao> findByUsuarioId(Long usuarioId, Pageable pageable);

    // 5. Histórico de 12 meses (Native Query para Postgres) - Agora com filtro de Usuário
    @Query(value = """
            SELECT
                TO_CHAR(t.data, 'MM/YYYY') as mesAno,
                SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE 0 END) as receitas,
                SUM(CASE WHEN t.tipo = 'DESPESA' THEN t.valor ELSE 0 END) as despesas
            FROM tb_transacao t
            WHERE t.usuario_id = :usuarioId
            AND t.data >= CURRENT_DATE - INTERVAL '12 months'
            GROUP BY TO_CHAR(t.data, 'MM/YYYY'), TO_CHAR(t.data, 'YYYYMM')
            ORDER BY TO_CHAR(t.data, 'YYYYMM') ASC
            """, nativeQuery = true)
    List<Object[]> buscarHistorico12MesesNative(@Param("usuarioId") Long usuarioId);

    // 6. Gastos por categoria (Native Query) - Agora com filtro de Usuário, Mês e Ano
    @Query(value = """
            SELECT c.nome_categoria as categoria, SUM(t.valor) as total
            FROM tb_transacao t
            JOIN tb_categoria c ON t.categoria_id = c.id
            WHERE t.usuario_id = :usuarioId 
            AND t.tipo = 'DESPESA'
            AND EXTRACT(MONTH FROM t.data) = :mes
            AND EXTRACT(YEAR FROM t.data) = :ano
            GROUP BY c.nome_categoria
            """, nativeQuery = true)
    List<Object[]> buscarGastosPorCategoriaNative(@Param("usuarioId") Long usuarioId, @Param("mes") int mes, @Param("ano") int ano);

    // 7. Fluxo Diário (Native Query) - Agrupado por dia do mês selecionado
    @Query(value = """
            SELECT
                TO_CHAR(t.data, 'DD/MM') as dia,
                SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE 0 END) as receitas,
                SUM(CASE WHEN t.tipo = 'DESPESA' THEN t.valor ELSE 0 END) as despesas
            FROM tb_transacao t
            WHERE t.usuario_id = :usuarioId
            AND EXTRACT(MONTH FROM t.data) = :mes
            AND EXTRACT(YEAR FROM t.data) = :ano
            GROUP BY t.data, TO_CHAR(t.data, 'DD/MM')
            ORDER BY t.data ASC
            """, nativeQuery = true)
    List<Object[]> buscarFluxoDiarioNative(@Param("usuarioId") Long usuarioId, @Param("mes") int mes, @Param("ano") int ano);

    // Método auxiliar simples
    List<Transacao> findByUsuarioId(Long usuarioId);
}