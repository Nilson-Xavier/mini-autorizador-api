package br.com.vr.miniautorizador.exception;

public class CartaoInexistenteException extends RuntimeException {
    public CartaoInexistenteException() {
        super("Cartão inexistente");
    }
}

