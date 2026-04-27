package app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import app.model.Transacao;
import app.model.Usuario;
import app.service.TransacaoService;
import dto.DashboardDTO;
import dto.TransacaoRequestDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    @Autowired
    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    // Método auxiliar para não repetir código de pegar o usuário logado
    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity<Transacao> criar(@RequestBody @Valid TransacaoRequestDTO dto) {
        // Passamos o objeto Usuario completo para o service "carimbar" a transação
        Transacao novaTransacao = transacaoService.salvar(dto, getUsuarioLogado());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTransacao);
    }

    @GetMapping
    public ResponseEntity<Page<Transacao>> listarTodas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        // Passamos apenas o ID para a listagem
        return ResponseEntity.ok(transacaoService.listarTodasPaginadas(pagina, tamanho, getUsuarioLogado().getId()));
    }

    @GetMapping("/filtro")
    public ResponseEntity<Page<Transacao>> listarFiltrado(
            @RequestParam int mes, 
            @RequestParam int ano,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(transacaoService.listarPorPeriodo(mes, ano, pagina, tamanho, getUsuarioLogado().getId()));
    }

    @GetMapping("/resumo")
    public ResponseEntity<DashboardDTO> obterResumo() {
        return ResponseEntity.ok(transacaoService.buscarResumo(getUsuarioLogado().getId()));
    }

    @GetMapping("/resumo/filtro")
    public ResponseEntity<DashboardDTO> buscarResumoFiltrado(
            @RequestParam int mes, 
            @RequestParam int ano) {
        return ResponseEntity.ok(transacaoService.buscarResumoPorPeriodo(mes, ano, getUsuarioLogado().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transacao> atualizar(@PathVariable Long id, @RequestBody @Valid TransacaoRequestDTO dto) {
        Transacao transacaoAtualizada = transacaoService.atualizar(id, dto, getUsuarioLogado().getId());
        return ResponseEntity.ok(transacaoAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        transacaoService.deletar(id, getUsuarioLogado().getId());
        return ResponseEntity.noContent().build();
    }
}