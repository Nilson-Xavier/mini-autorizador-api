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
        if (repository.existsById(numero)) {
            throw new CartaoExistenteException();
        }
        Cartao cartao = new Cartao(numero, senha);
        return repository.save(cartao);
    }

    public BigDecimal obterSaldo(String numero) {
        return repository.findById(numero)
                .orElseThrow(CartaoInexistenteException::new)
                .getSaldo();
    }
}
