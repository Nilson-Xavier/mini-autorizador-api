package br.com.vr.miniautorizador.integration;

import br.com.vr.miniautorizador.repository.CartaoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test") // Usa application-test.properties para apontar para o MySQL de teste
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransacaoIntegrationTest {

    @Autowired
    private CartaoRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "password")
    @DisplayName("Deve criar cartão e realizar transação com sucesso")
    void deveRealizarTransacao() throws Exception {
        String numeroCartao = "123456";

        try {
            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senha\": \"1234\"}"))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senhaCartao\": \"1234\", \"valor\": 400.00}"))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("OK"));
        } finally {
            repository.deleteBynumeroCartao(numeroCartao);
        }
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "password")
    @DisplayName("Deve criar cartão e realizar transação com sucesso")
    void deveTentarTransacaoComErroSenhaInvalida() throws Exception {
        String numeroCartao = "123456";

        try {
            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senha\": \"1234\"}"))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senhaCartao\": \"12345\", \"valor\": 400.00}"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().string("SENHA_INVALIDA"));
        } finally {
            repository.deleteBynumeroCartao(numeroCartao);
        }
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "password")
    @DisplayName("Deve criar cartão e realizar transação com sucesso")
    void deveTentarTransacaoComErroCartaoInvalido() throws Exception {
        String numeroCartao = "123456";

        try {
            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senha\": \"1234\"}"))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "7\", \"senhaCartao\": \"1234\", \"valor\": 400.00}"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().string("CARTAO_INEXISTENTE"));
        } finally {
            repository.deleteBynumeroCartao(numeroCartao);
        }
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "password")
    @DisplayName("Deve criar cartão e realizar transação com sucesso")
    void deveTentarTransacaoComErroSaldoInsuficiente() throws Exception {
        String numeroCartao = "123456";

        try {
            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senha\": \"1234\"}"))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/transacoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senhaCartao\": \"1234\", \"valor\": 600.00}"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().string("SALDO_INSUFICIENTE"));
        } finally {
            repository.deleteBynumeroCartao(numeroCartao);
        }
    }
}
