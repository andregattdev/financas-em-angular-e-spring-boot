package app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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