package br.com.vr.miniautorizador.service;

import br.com.vr.miniautorizador.entity.Cartao;
import br.com.vr.miniautorizador.exception.CartaoInexistenteException;
import br.com.vr.miniautorizador.exception.SaldoInsuficienteException;
import br.com.vr.miniautorizador.exception.SenhaInvalidaException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceTest {
    @Mock
    private CartaoRepository repository;

    @InjectMocks
    private TransacaoService service;

    @Test
    @DisplayName("Deve autorizar transação quando saldo do cartao suficiente")
    void deveAutorizarTransacaoQuandoSaldoSuficiente() {
        Cartao cartaoRequest = new Cartao("1234567", "123", new BigDecimal("500.00"));
        when(repository.findByNumeroForUpdate(anyString())).thenReturn(Optional.of(cartaoRequest));

        service.autorizar("1234567", "123", new BigDecimal("400.00"));

        assertEquals(new BigDecimal("100.00"), cartaoRequest.getSaldo());
    }
    @Test
    @DisplayName("Deve lançar exceção de cartão inexistente")
    void deveLancarExcecaoCartaoInexistente() {
        when(repository.findByNumeroForUpdate(anyString())).thenReturn(Optional.empty());

        assertThrows(CartaoInexistenteException.class, () -> service.autorizar("1234567", "123", new BigDecimal("400.00")));
    }

    @Test
    @DisplayName("Deve lançar exceção de senha invalida")
    void deveLancarExcecaoSenhaInvalida() {
        Cartao cartaoRequest = new Cartao("1234567", "senha123", new BigDecimal("500.00"));
        when(repository.findByNumeroForUpdate(anyString())).thenReturn(Optional.of(cartaoRequest));

        assertThrows(SenhaInvalidaException.class, () -> service.autorizar("1234567", "1234", new BigDecimal("400.00")));
    }

    @Test
    @DisplayName("Deve lançar exceção de saldo insuficiente")
    void deveLancarSaldoInsuficienteException() {
        Cartao cartaoRequest = new Cartao("1234567", "123", new BigDecimal("500.00"));
        when(repository.findByNumeroForUpdate(anyString())).thenReturn(Optional.of(cartaoRequest));

        assertThrows(SaldoInsuficienteException.class, () -> service.autorizar("1234567", "123", new BigDecimal("600.00")));
    }
}
