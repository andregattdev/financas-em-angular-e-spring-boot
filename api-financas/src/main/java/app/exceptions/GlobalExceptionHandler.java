package app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<Object> handleCategoriaNotFound(CategoriaNotFoundException ex) {
        return montarRespostaErro(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransacaoNotFoundException.class)
    public ResponseEntity<Object> handleTransacaoNotFound(TransacaoNotFoundException ex) {
        return montarRespostaErro(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Object> handleAcessoNegado(AcessoNegadoException ex) {
        return montarRespostaErro(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidacao(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> corpo = new HashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", HttpStatus.BAD_REQUEST.value());
        corpo.put("erro", "Validação falhou");
        corpo.put("campos", erros);

        return new ResponseEntity<>(corpo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return montarRespostaErro(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Método auxiliar para padronizar a resposta de erro
    private ResponseEntity<Object> montarRespostaErro(String mensagem, HttpStatus status) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem);
        
        return new ResponseEntity<>(corpo, status);
    }
}