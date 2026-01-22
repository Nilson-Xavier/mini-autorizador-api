package br.com.vr.miniautorizador.repository;

import br.com.vr.miniautorizador.entity.Cartao;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartaoRepositoryTest {

    @Autowired
    private CartaoRepository cartaoRepository;

    private static final String NUMERO_CARTAO = "123456";
    private static final String SENHA = "1234";
    private static final BigDecimal SALDO_INICIAL = new BigDecimal("500.00");

    @BeforeEach
    @AfterEach
    void setUp() {
        // Limpa os dados antes e depois de cada teste
        cartaoRepository.deleteAll();
    }

    @Test
    @Order(1)
    void deveSalvarEEncontrarCartao() {
        Cartao cartao = Cartao.builder()
                .numeroCartao(NUMERO_CARTAO)
                .senha(SENHA)
                .saldo(SALDO_INICIAL)
                .build();

        cartaoRepository.save(cartao);
        Optional<Cartao> cartaoEncontrado = cartaoRepository.findById(NUMERO_CARTAO);

        assertThat(cartaoEncontrado).isPresent();
        assertThat(cartaoEncontrado.get().getNumeroCartao()).isEqualTo(NUMERO_CARTAO);
        assertThat(cartaoEncontrado.get().getSenha()).isEqualTo(SENHA);
        assertThat(cartaoEncontrado.get().getSaldo()).isEqualByComparingTo(SALDO_INICIAL);
    }

    @Test
    @Order(2)
    void deveEncontrarCartao() {
        Cartao cartao = Cartao.builder()
                .numeroCartao(NUMERO_CARTAO)
                .senha(SENHA)
                .saldo(SALDO_INICIAL)
                .build();
        cartaoRepository.save(cartao);

        Optional<Cartao> cartaoEncontrado = cartaoRepository.findByNumeroForUpdate(NUMERO_CARTAO);

        assertThat(cartaoEncontrado).isPresent();
        assertThat(cartaoEncontrado.get().getNumeroCartao()).isEqualTo(NUMERO_CARTAO);
    }

    @Test
    @Order(3)
    void deveApagarCartao() {
        Cartao cartao = Cartao.builder()
                .numeroCartao(NUMERO_CARTAO)
                .senha(SENHA)
                .saldo(SALDO_INICIAL)
                .build();
        cartaoRepository.save(cartao);

        cartaoRepository.deleteBynumeroCartao(NUMERO_CARTAO);
        Optional<Cartao> cartaoRemovido = cartaoRepository.findById(NUMERO_CARTAO);

        assertThat(cartaoRemovido).isNotPresent();
    }

    @Test
    @Order(4)
    void deveNaoEncontrarCartao() {
        Optional<Cartao> cartaoEncontrado = cartaoRepository.findById("999999");

        assertThat(cartaoEncontrado).isNotPresent();
    }

    @Test
    @Order(5)
    void deveAtualizarSaldoCartao() {
        Cartao cartao = Cartao.builder()
                .numeroCartao(NUMERO_CARTAO)
                .senha(SENHA)
                .saldo(SALDO_INICIAL)
                .build();
        cartaoRepository.save(cartao);

        BigDecimal novoSaldo = new BigDecimal("1000.00");
        cartao.setSaldo(novoSaldo);
        cartaoRepository.save(cartao);
        Optional<Cartao> cartaoAtualizado = cartaoRepository.findById(NUMERO_CARTAO);

        assertThat(cartaoAtualizado).isPresent();
        assertThat(cartaoAtualizado.get().getSaldo()).isEqualByComparingTo(novoSaldo);
    }
}
