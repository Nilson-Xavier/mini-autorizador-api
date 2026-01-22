package br.com.vr.miniautorizador.concurrency;

import br.com.vr.miniautorizador.repository.CartaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test") // Usa application-test.properties para apontar para o MySQL de teste
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransacaoConcorrenteIntegrationTest {

    @Autowired
    private CartaoRepository repository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve criar cartão e realizar transação com sucesso")
    void deveAutorizarApenasUmaTransacaoConcorrente() throws Exception {
        String numeroCartao = "123456";
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());

        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionTemplate.execute(status -> {
                try {
                    mockMvc.perform(post("/cartoes")
                                    .header("Authorization", authHeader)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senha\": \"1234\"}"))
                            .andExpect(status().isCreated());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create cartao", e);
                }
                return null;
            });

            // Preparar 2 transações simultâneas de 300.00 cada
            int threads = 2;
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            CountDownLatch latch = new CountDownLatch(1);

            Callable<Integer> task = () -> {
                latch.await(); // Espera o sinal para disparar juntas
                return mockMvc.perform(post("/transacoes")
                                .header("Authorization", authHeader)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"numeroCartao\": \"" + numeroCartao + "\", \"senhaCartao\": \"1234\", \"valor\": 300.00}"))
                        .andReturn().getResponse().getStatus();
            };

            Future<Integer> f1 = executor.submit(task);
            Future<Integer> f2 = executor.submit(task);

            latch.countDown(); // DISPARA!

            List<Integer> resultados = List.of(f1.get(), f2.get());

            // Assert: Uma deve ter tido sucesso (201) e a outra deve ter falhado por saldo (422)
            assertTrue(resultados.contains(201), "Uma transação deveria ter sido aprovada");
            assertTrue(resultados.contains(422), "Uma transação deveria ter falhado por saldo insuficiente");

            // Verifica saldo final no banco: 500 - 300 = 200 (E não -100)
            mockMvc.perform(get("/cartoes/" + numeroCartao)
                            .header("Authorization", authHeader)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("200.00"));
        } finally {
            // Remover cartao criado
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionTemplate.execute(status -> {
                repository.deleteBynumeroCartao(numeroCartao);
                return null;
            });
        }
    }
}
