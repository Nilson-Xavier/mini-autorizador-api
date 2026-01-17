package br.com.vr.miniautorizador.controller;

import br.com.vr.miniautorizador.config.SecurityConfig;
import br.com.vr.miniautorizador.exception.CartaoInexistenteException;
import br.com.vr.miniautorizador.exception.SaldoInsuficienteException;
import br.com.vr.miniautorizador.exception.SenhaInvalidaException;
import br.com.vr.miniautorizador.service.TransacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransacaoController.class)
@Import(SecurityConfig.class) // Importa sua configuração de Basic Auth
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransacaoService transacaoService;

    @Test
    @DisplayName("Deve retornar 201 e OK quando a transação for autorizada")
    @WithMockUser(username = "user", password = "password")
    void deveRetornar201QuandoAutorizada() throws Exception {
        doNothing().when(transacaoService).autorizar(anyString(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroCartao\": \"123456789\", \"senhaCartao\": \"1234\", \"valor\": 10.00}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));

        verify(transacaoService).autorizar(anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Deve retornar 422 e SALDO_INSUFICIENTE quando falhar regra de saldo")
    @WithMockUser(username = "user", password = "password")
    void deveRetornar422SaldoInsuficiente() throws Exception {
        doThrow(new SaldoInsuficienteException()).when(transacaoService).autorizar(anyString(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroCartao\": \"123456789\", \"senhaCartao\": \"1234\", \"valor\": 10.00}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SALDO_INSUFICIENTE"));

        verify(transacaoService).autorizar(anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Deve retornar 422 e SENHA_INVALIDA quando a senha estiver incorreta")
    @WithMockUser(username = "user", password = "password")
    void deveRetornar422SenhaInvalida() throws Exception {
        doThrow(new SenhaInvalidaException()).when(transacaoService).autorizar(anyString(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroCartao\": \"123456789\", \"senhaCartao\": \"1234\", \"valor\": 10.00}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SENHA_INVALIDA"));

        verify(transacaoService).autorizar(anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Deve retornar 422 e CARTAO_INEXISTENTE quando falhar regra do numero do cartao")
    @WithMockUser(username = "user", password = "password")
    void deveRetornar422CartaoInexistente() throws Exception {
        doThrow(new CartaoInexistenteException()).when(transacaoService).autorizar(anyString(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroCartao\": \"123456789\", \"senhaCartao\": \"1234\", \"valor\": 10.00}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("CARTAO_INEXISTENTE"));

        verify(transacaoService).autorizar(anyString(), anyString(), any(BigDecimal.class));
    }
}
