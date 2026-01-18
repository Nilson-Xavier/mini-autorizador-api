package br.com.vr.miniautorizador.service;

import br.com.vr.miniautorizador.entity.Cartao;
import br.com.vr.miniautorizador.exception.CartaoInexistenteException;
import br.com.vr.miniautorizador.repository.CartaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransacaoService {

    private final CartaoRepository repository;

    public TransacaoService(CartaoRepository repository) {
        this.repository = repository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void autorizar(String numero, String senha, BigDecimal valor) {
        Cartao cartao = repository.findByNumeroForUpdate(numero)
                .orElseThrow(CartaoInexistenteException::new);

        cartao.validarSenha(senha);
        cartao.debitar(valor);
    }
}

