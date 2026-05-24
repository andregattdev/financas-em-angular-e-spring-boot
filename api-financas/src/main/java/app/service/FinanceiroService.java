package app.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.repository.TransacaoRepository;
import dto.CategoriaSomaDTO;
import dto.HistoricoMensalDTO;

@Service
public class FinanceiroService {
	
	@Autowired
    private TransacaoRepository repository;

    // Adicionamos o parâmetro usuarioId aqui
	public List<HistoricoMensalDTO> getHistoricoAnual(Long usuarioId) {
	    List<Object[]> resultados = repository.buscarHistorico12MesesNative(usuarioId);
	    
        // Vamos calcular o saldo acumulado inicial (tudo que veio antes do 1º mês retornado)
        BigDecimal saldoAcumulado = BigDecimal.ZERO;
        if (!resultados.isEmpty()) {
            String primeiroMesAno = resultados.get(0)[0].toString(); // MM/YYYY
            int mes = Integer.parseInt(primeiroMesAno.split("/")[0]);
            int ano = Integer.parseInt(primeiroMesAno.split("/")[1]);
            java.time.LocalDate dataAntesDoGrafico = java.time.LocalDate.of(ano, mes, 1).minusDays(1);
            
            BigDecimal recAnteriores = repository.sumByTipoUpToDate(app.model.TipoTransacao.RECEITA, usuarioId, dataAntesDoGrafico);
            BigDecimal despAnteriores = repository.sumByTipoUpToDate(app.model.TipoTransacao.DESPESA, usuarioId, dataAntesDoGrafico);
            recAnteriores = recAnteriores != null ? recAnteriores : BigDecimal.ZERO;
            despAnteriores = despAnteriores != null ? despAnteriores : BigDecimal.ZERO;
            saldoAcumulado = recAnteriores.subtract(despAnteriores);
        }
        
        List<HistoricoMensalDTO> dtos = new java.util.ArrayList<>();
        
        for (Object[] obj : resultados) {
            String mesAno = obj[0].toString();
            BigDecimal receitas = new BigDecimal(obj[1].toString());
            BigDecimal despesas = new BigDecimal(obj[2].toString());
            
            BigDecimal saldoMes = receitas.subtract(despesas);
            saldoAcumulado = saldoAcumulado.add(saldoMes);
            
            dtos.add(new HistoricoMensalDTO(mesAno, receitas, despesas, saldoMes, saldoAcumulado));
        }
        
        return dtos;
	}
	
    // Adicionamos o parâmetro usuarioId, mes e ano aqui também
	public List<CategoriaSomaDTO> getGastosPorCategoria(Long usuarioId, int mes, int ano) {
        // Passamos o usuarioId, mes e ano para a query nativa do repository
	    List<Object[]> resultados = repository.buscarGastosPorCategoriaNative(usuarioId, mes, ano);
	    return resultados.stream()
	        .map(obj -> new CategoriaSomaDTO(
	            obj[0].toString(),
	            new BigDecimal(obj[1].toString())
	        ))
	        .collect(Collectors.toList());
	}

    public List<dto.FluxoDiarioDTO> getFluxoDiario(Long usuarioId, int mes, int ano) {
        List<Object[]> resultados = repository.buscarFluxoDiarioNative(usuarioId, mes, ano);
        return resultados.stream()
            .map(obj -> new dto.FluxoDiarioDTO(
                obj[0].toString(),
                new BigDecimal(obj[1].toString()),
                new BigDecimal(obj[2].toString())
            ))
            .collect(Collectors.toList());
    }
}