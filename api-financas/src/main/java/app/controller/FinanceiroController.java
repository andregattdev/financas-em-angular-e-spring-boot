package app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.model.Usuario;
import app.service.FinanceiroService;
import dto.CategoriaSomaDTO;
import dto.HistoricoMensalDTO;

@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService service;

    // Método auxiliar para pegar o ID do usuário autenticado pelo JWT
    private Long getUsuarioIdLogado() {
        Usuario logado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return logado.getId();
    }

    @GetMapping("/historico")
    public ResponseEntity<List<HistoricoMensalDTO>> getHistorico() {
        // Passamos o ID para o service, que agora exige esse parâmetro
        List<HistoricoMensalDTO> historico = service.getHistoricoAnual(getUsuarioIdLogado());
        return ResponseEntity.ok(historico);
    }
    
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaSomaDTO>> getCategorias(
            @org.springframework.web.bind.annotation.RequestParam int mes,
            @org.springframework.web.bind.annotation.RequestParam int ano) {
        // O mesmo aqui: filtro por usuário logado, mês e ano
        return ResponseEntity.ok(service.getGastosPorCategoria(getUsuarioIdLogado(), mes, ano));
    }

    @GetMapping("/fluxo-diario")
    public ResponseEntity<List<dto.FluxoDiarioDTO>> getFluxoDiario(
            @org.springframework.web.bind.annotation.RequestParam int mes,
            @org.springframework.web.bind.annotation.RequestParam int ano) {
        return ResponseEntity.ok(service.getFluxoDiario(getUsuarioIdLogado(), mes, ano));
    }
}