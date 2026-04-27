package app.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import app.exceptions.CategoriaNotFoundException;
import app.exceptions.TransacaoNotFoundException;
import app.model.Categoria;
import app.model.TipoTransacao;
import app.model.Transacao;
import app.model.Usuario; // Importante
import app.repository.CategoriaRepository;
import app.repository.TransacaoRepository;
import dto.DashboardDTO;
import dto.TransacaoRequestDTO;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public TransacaoService(TransacaoRepository transacaoRepository, CategoriaRepository categoriaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Transacao salvar(TransacaoRequestDTO dto, Usuario usuarioLogado) {
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new CategoriaNotFoundException(dto.categoriaId()));

        Transacao transacao = new Transacao();
        transacao.setDescricao(dto.descricao());
        transacao.setValor(dto.valor());
        transacao.setData(dto.data());
        transacao.setTipo(dto.tipo());
        transacao.setCategoria(categoria);
        
        // AQUI ESTÁ O SEGREDO: Vinculamos o dono da transação
        transacao.setUsuario(usuarioLogado); 

        return transacaoRepository.save(transacao);
    }

    @Transactional(readOnly = true)
    public List<Transacao> listarTodas(Long usuarioId) {
        return transacaoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Transacao atualizar(Long id, TransacaoRequestDTO dto, Long usuarioId) {
        // Busca a transação e garante que ela pertence ao usuário logado
        Transacao transacaoExistente = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNotFoundException(id));
        
        // Proteção extra: verifica se o usuário não está tentando editar a transação de outro
        if (!transacaoExistente.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado: esta transação não pertence a você.");
        }

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new CategoriaNotFoundException(dto.categoriaId()));

        transacaoExistente.setDescricao(dto.descricao());
        transacaoExistente.setValor(dto.valor());
        transacaoExistente.setData(dto.data());
        transacaoExistente.setTipo(dto.tipo());
        transacaoExistente.setCategoria(categoria);

        return transacaoRepository.save(transacaoExistente);
    }

    @Transactional
    public void deletar(Long id, Long usuarioId) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNotFoundException(id));
        
        if (!transacao.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado.");
        }
        
        transacaoRepository.delete(transacao);
    }

    @Transactional(readOnly = true)
    public DashboardDTO buscarResumo(Long usuarioId) {
        BigDecimal receitas = transacaoRepository.sumByTipo(TipoTransacao.RECEITA, usuarioId);
        BigDecimal despesas = transacaoRepository.sumByTipo(TipoTransacao.DESPESA, usuarioId);

        receitas = (receitas != null) ? receitas : BigDecimal.ZERO;
        despesas = (despesas != null) ? despesas : BigDecimal.ZERO;

        return new DashboardDTO(receitas, despesas, receitas.subtract(despesas));
    }
    
    @Transactional(readOnly = true)
    public DashboardDTO buscarResumoPorPeriodo(int mes, int ano, Long usuarioId) {
        BigDecimal receitas = transacaoRepository.sumByTipoAndMesEAno(TipoTransacao.RECEITA, mes, ano, usuarioId);
        BigDecimal despesas = transacaoRepository.sumByTipoAndMesEAno(TipoTransacao.DESPESA, mes, ano, usuarioId);

        receitas = (receitas != null) ? receitas : BigDecimal.ZERO;
        despesas = (despesas != null) ? despesas : BigDecimal.ZERO;

        return new DashboardDTO(receitas, despesas, receitas.subtract(despesas));
    }
    
    @Transactional(readOnly = true)
    public Page<Transacao> listarPorPeriodo(int mes, int ano, int pagina, int tamanho, Long usuarioId) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("data").descending());
        return transacaoRepository.findByMesEAno(mes, ano, usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transacao> listarTodasPaginadas(int pagina, int tamanho, Long usuarioId) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("data").descending());
        return transacaoRepository.findByUsuarioId(usuarioId, pageable);
    }
}