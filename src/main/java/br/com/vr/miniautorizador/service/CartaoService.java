package br.com.vr.miniautorizador.service;

import br.com.vr.miniautorizador.entity.Cartao;
import br.com.vr.miniautorizador.exception.CartaoExistenteException;
import br.com.vr.miniautorizador.exception.CartaoInexistenteException;
import br.com.vr.miniautorizador.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository repository;

    public Cartao criarCartao(String numero, String senha) {
        return (Cartao) repository.findById(numero)
                .map(existing -> { throw new CartaoExistenteException(); })
                .orElseGet(() -> repository.save(new Cartao(numero, senha)));
    }

    public BigDecimal obterSaldo(String numero) {
        return repository.findById(numero)
                .orElseThrow(CartaoInexistenteException::new)
                .getSaldo();
    }
}
