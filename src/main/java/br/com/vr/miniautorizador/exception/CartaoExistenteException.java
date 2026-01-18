package br.com.vr.miniautorizador.exception;

public class CartaoExistenteException extends RuntimeException {
    public CartaoExistenteException() {
        super("Cartão já existente");
    }
}

