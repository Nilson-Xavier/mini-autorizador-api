package br.com.vr.miniautorizador.service;

import br.com.vr.miniautorizador.entity.Cartao;
import br.com.vr.miniautorizador.exception.CartaoExistenteException;
import br.com.vr.miniautorizador.exception.CartaoInexistenteException;
import br.com.vr.miniautorizador.repository.CartaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartaoServiceTest {
    @Mock
    private CartaoRepository repository;

    @InjectMocks
    private CartaoService service;

    @Test
    @DisplayName("Deve criar um cartão com saldo inicial de 500 reais")
    void deveCriarCartaoComSaldoInicial() {
        Cartao cartaoRequest = new Cartao("1234567", "senha123");
        when(repository.findById(any())).thenReturn(Optional.empty());
        when(repository.save(any(Cartao.class))).thenReturn(cartaoRequest);

        Cartao cartaoCriado = service.criarCartao("1234567", "senha123");

        assertEquals("1234567", cartaoCriado.getNumeroCartao());
        assertEquals(new BigDecimal("500.00"), cartaoCriado.getSaldo());
        verify(repository).save(any(Cartao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção de cartão já existente")
    void deveLancarExcecaoQuandoCartaoJaExiste() {
        Cartao cartaoRequest = new Cartao("1234567", "senha123");
        when(repository.findById(any())).thenReturn(Optional.of(cartaoRequest));

        assertThrows(CartaoExistenteException.class, () -> service.criarCartao("1234567", "123"));
    }

    @Test
    @DisplayName("Deve obter saldo do cartão")
    void deveObterSaldoDoCartao() {
        Cartao cartaoRequest = new Cartao("1234567", "senha123", new BigDecimal("500.00"));
        when(repository.findById(any())).thenReturn(Optional.of(cartaoRequest));

        BigDecimal saldo = service.obterSaldo("1234567");

        assertEquals(new BigDecimal("500.00"), saldo);
    }

    @Test
    @DisplayName("Deve lançar exceção de cartão inexistente")
    void deveLancarExcecaoCartaoInexistente() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CartaoInexistenteException.class, () -> service.obterSaldo("1234567"));
    }
}
