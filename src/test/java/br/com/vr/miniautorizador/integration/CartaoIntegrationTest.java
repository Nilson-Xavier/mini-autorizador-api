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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test") // Usa application-test.properties para apontar para o MySQL de teste
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartaoIntegrationTest {

    @Autowired
    private CartaoRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "password")
    @DisplayName("Deve criar cartão e realizar transação com sucesso")
    void deveCriarCartaoEConsultarSaldo() throws Exception {
        String numeroCartao = "123456";

        try {
            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senha\": \"1234\"}"))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/cartoes/" + numeroCartao)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("500.00"));
        } finally {
            repository.deleteBynumeroCartao(numeroCartao);
        }
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "password")
    @DisplayName("Deve criar cartão e gerar erro de cartao inexistente")
    void deveCriarCartaoEerroCartaoInexistente() throws Exception {
        String numeroCartao = "123456";

        try {
            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senha\": \"1234\"}"))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/cartoes/" + numeroCartao + "7")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(""));
        } finally {
            repository.deleteBynumeroCartao(numeroCartao);
        }
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "password")
    @DisplayName("Deve criar cartão e gerar erro de cartao inexistente")
    void deveCriarCartaoEerroCartaoExistente() throws Exception {
        String numeroCartao = "123456";

        try {
            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senha\": \"1234\"}"))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/cartoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senha\": \"1234\"}"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().string("{\"numeroCartao\":\"" + numeroCartao + "\",\"senha\":\"1234\"}"));
        } finally {
            repository.deleteBynumeroCartao(numeroCartao);
        }
    }
}
