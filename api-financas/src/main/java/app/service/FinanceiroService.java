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
        // Passamos o usuarioId para a query nativa do repository
	    List<Object[]> resultados = repository.buscarHistorico12MesesNative(usuarioId);
	    
	    return resultados.stream()
	        .map(obj -> {
	            // Log para debug (útil para ver se o filtro está funcionando)
	            System.out.println("Usuário: " + usuarioId + " | Mes: " + obj[0] + " | Rec: " + obj[1] + " | Desp: " + obj[2]);
	            
	            return new HistoricoMensalDTO(
	                obj[0].toString(), 
	                new BigDecimal(obj[1].toString()), 
	                new BigDecimal(obj[2].toString())  
	            );
	        })
	        .collect(Collectors.toList());
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