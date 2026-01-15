package br.com.vr.miniautorizador.service;

import br.com.vr.miniautorizador.entity.Cartao;
import br.com.vr.miniautorizador.repository.CartaoRepository;
import br.com.vr.miniautorizador.exception.CartaoInexistenteException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransacaoService {

    private final CartaoRepository repository;

    public TransacaoService(CartaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void autorizar(String numero, String senha, BigDecimal valor) {
        Cartao cartao = repository.findByNumeroForUpdate(numero)
                .orElseThrow(CartaoInexistenteException::new);

        cartao.validarSenha(senha);
        cartao.debitar(valor);
    }
}

