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

        int numParcelas = (dto.parcelas() != null && dto.parcelas() > 1) ? dto.parcelas() : 1;
        BigDecimal valorTotal = dto.valor();
        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(numParcelas), 2, java.math.RoundingMode.HALF_UP);
        
        Transacao primeiraTransacao = null;
        String codigoParcelamento = (numParcelas > 1) ? java.util.UUID.randomUUID().toString() : null;

        java.time.LocalDate dataBase = dto.data();
        if (dto.diaVencimento() != null && dto.diaVencimento() >= 1 && dto.diaVencimento() <= 31) {
            int year = dataBase.getYear();
            int month = dataBase.getMonthValue();
            int maxDays = java.time.YearMonth.of(year, month).lengthOfMonth();
            int day = Math.min(dto.diaVencimento(), maxDays);
            dataBase = java.time.LocalDate.of(year, month, day);
        }

        for (int i = 0; i < numParcelas; i++) {
            Transacao transacao = new Transacao();
            
            if (numParcelas > 1) {
                transacao.setDescricao(dto.descricao() + " (" + (i + 1) + "/" + numParcelas + ")");
                transacao.setValor(valorParcela);
            } else {
                transacao.setDescricao(dto.descricao());
                transacao.setValor(valorTotal);
            }
            
            java.time.LocalDate dataParcela = dataBase.plusMonths(i);
            if (dto.diaVencimento() != null && dto.diaVencimento() >= 1 && dto.diaVencimento() <= 31) {
                int maxDays = java.time.YearMonth.of(dataParcela.getYear(), dataParcela.getMonthValue()).lengthOfMonth();
                int day = Math.min(dto.diaVencimento(), maxDays);
                dataParcela = java.time.LocalDate.of(dataParcela.getYear(), dataParcela.getMonthValue(), day);
            }
            
            transacao.setData(dataParcela);
            transacao.setTipo(dto.tipo());
            transacao.setCategoria(categoria);
            transacao.setUsuario(usuarioLogado); 
            
            if (i == 0) {
                transacao.setPago(dto.pago() != null ? dto.pago() : false);
            } else {
                transacao.setPago(false);
            }
            
            transacao.setCodigoParcelamento(codigoParcelamento);
            
            transacao = transacaoRepository.save(transacao);
            
            if (i == 0) {
                primeiraTransacao = transacao;
            }
        }

        return primeiraTransacao;
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
        transacaoExistente.setPago(dto.pago() != null ? dto.pago() : false);

        return transacaoRepository.save(transacaoExistente);
    }

    @Transactional
    public Transacao atualizarStatus(Long id, boolean pago, Long usuarioId) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNotFoundException(id));
        
        if (!transacao.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado: esta transação não pertence a você.");
        }

        transacao.setPago(pago);
        return transacaoRepository.save(transacao);
    }

    @Transactional
    public void deletar(Long id, Long usuarioId) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNotFoundException(id));
        
        if (!transacao.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado.");
        }
        
        if (transacao.getCodigoParcelamento() != null) {
            List<Transacao> parcelasFuturas = transacaoRepository
                .findByCodigoParcelamentoAndDataGreaterThanEqual(transacao.getCodigoParcelamento(), transacao.getData());
            
            for (Transacao p : parcelasFuturas) {
                if (p.getUsuario().getId().equals(usuarioId)) {
                    transacaoRepository.delete(p);
                }
            }
        } else {
            transacaoRepository.delete(transacao);
        }
    }

    @Transactional(readOnly = true)
    public DashboardDTO buscarResumo(Long usuarioId) {
        BigDecimal receitas = transacaoRepository.sumByTipo(TipoTransacao.RECEITA, usuarioId);
        BigDecimal despesas = transacaoRepository.sumByTipo(TipoTransacao.DESPESA, usuarioId);

        receitas = (receitas != null) ? receitas : BigDecimal.ZERO;
        despesas = (despesas != null) ? despesas : BigDecimal.ZERO;

        BigDecimal saldo = receitas.subtract(despesas);
        return new DashboardDTO(receitas, despesas, saldo, saldo);
    }
    
    @Transactional(readOnly = true)
    public DashboardDTO buscarResumoPorPeriodo(int mes, int ano, Long usuarioId) {
        BigDecimal receitas = transacaoRepository.sumByTipoAndMesEAno(TipoTransacao.RECEITA, mes, ano, usuarioId);
        BigDecimal despesas = transacaoRepository.sumByTipoAndMesEAno(TipoTransacao.DESPESA, mes, ano, usuarioId);

        receitas = (receitas != null) ? receitas : BigDecimal.ZERO;
        despesas = (despesas != null) ? despesas : BigDecimal.ZERO;
        
        // Calcula saldo acumulado até o final do mês selecionado
        java.time.YearMonth yearMonth = java.time.YearMonth.of(ano, mes);
        java.time.LocalDate dataLimite = yearMonth.atEndOfMonth();
        
        BigDecimal recAcumuladas = transacaoRepository.sumByTipoUpToDate(TipoTransacao.RECEITA, usuarioId, dataLimite);
        BigDecimal despAcumuladas = transacaoRepository.sumByTipoUpToDate(TipoTransacao.DESPESA, usuarioId, dataLimite);
        
        recAcumuladas = (recAcumuladas != null) ? recAcumuladas : BigDecimal.ZERO;
        despAcumuladas = (despAcumuladas != null) ? despAcumuladas : BigDecimal.ZERO;
        
        BigDecimal saldoAcumulado = recAcumuladas.subtract(despAcumuladas);

        return new DashboardDTO(receitas, despesas, receitas.subtract(despesas), saldoAcumulado);
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