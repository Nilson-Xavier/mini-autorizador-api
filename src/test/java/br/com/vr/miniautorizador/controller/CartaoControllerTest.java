package br.com.vr.miniautorizador.controller;

import br.com.vr.miniautorizador.config.SecurityConfig;
import br.com.vr.miniautorizador.entity.Cartao;
import br.com.vr.miniautorizador.exception.CartaoExistenteException;
import br.com.vr.miniautorizador.exception.CartaoInexistenteException;
import br.com.vr.miniautorizador.service.CartaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartaoController.class)
@Import(SecurityConfig.class) // Importa sua configuração de Basic Auth
class CartaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartaoService cartaoService;

    @Test
    @DisplayName("Deve retornar 201 ao criar um cartão com sucesso")
    @WithMockUser(username = "user", password = "password")
    void deveCriarCartaoComSucesso() throws Exception {
        Cartao cartaoRequest = new Cartao("123456", "1234", new BigDecimal("500.00"));

        when(cartaoService.criarCartao(anyString(), anyString())).thenReturn(cartaoRequest);

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroCartao\": \"123456\", \"senha\": \"1234\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value("123456"))
                .andExpect(jsonPath("$.senha").value("1234"));

        verify(cartaoService).criarCartao(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar 422 quando o cartão já existe")
    @WithMockUser(username = "user", password = "password")
    void deveRetornar422QuandoCartaoJaExiste() throws Exception {
        when(cartaoService.criarCartao(anyString(), anyString())).thenThrow(new CartaoExistenteException());

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numeroCartao\": \"123456\", \"senha\": \"1234\"}"))
                .andExpect(status().isUnprocessableEntity()); // Retorna 422

        verify(cartaoService).criarCartao(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar 200 e o saldo ao consultar cartão existente")
    @WithMockUser(username = "user", password = "password")
    void deveRetornarSaldoE200() throws Exception {
        when(cartaoService.obterSaldo(anyString())).thenReturn(new BigDecimal("500.00"));

        mockMvc.perform(get("/cartoes/123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("500.00")); // Retorna o saldo no body

        verify(cartaoService).obterSaldo(anyString());
    }

    @Test
    @DisplayName("Deve retornar 404 ao consultar cartão inexistente")
    @WithMockUser(username = "user", password = "password")
    void deveRetornar404Inexistente() throws Exception {
        when(cartaoService.obterSaldo(anyString())).thenThrow(new CartaoInexistenteException());

        mockMvc.perform(get("/cartoes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404 sem body

        verify(cartaoService).obterSaldo(anyString());
    }
}
