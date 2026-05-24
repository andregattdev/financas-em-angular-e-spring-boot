package app.exceptions;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException() {
        super("Acesso negado: este recurso não pertence a você.");
    }

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
