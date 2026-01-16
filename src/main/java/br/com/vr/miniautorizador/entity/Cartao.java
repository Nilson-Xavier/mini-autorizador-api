package br.com.vr.miniautorizador.entity;

import br.com.vr.miniautorizador.exception.SaldoInsuficienteException;
import br.com.vr.miniautorizador.exception.SenhaInvalidaException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Table(name = "cartao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cartao {

    @Id
    private String numeroCartao;

    private String senha;

    private BigDecimal saldo;

    public Cartao(String numeroCartao, String senha) {
        this.numeroCartao = numeroCartao;
        this.senha = senha;
        this.saldo = new BigDecimal("500.00");
    }

    // getters e setters
    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getSenha() {
        return senha;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void validarSenha(String senhaInformada) {
        Optional.ofNullable(senhaInformada)
                .filter(senha -> !this.senha.equals(senha))
                .ifPresent(s -> { throw new SenhaInvalidaException(); });
    }

    public void debitar(BigDecimal valor) {
        Optional.ofNullable(valor)
                .filter(v -> v.compareTo(BigDecimal.ZERO) > 0)
                .filter(v -> this.saldo.compareTo(v) >= 0)
                .ifPresentOrElse(
                        v -> this.saldo = this.saldo.subtract(v),
                        () -> { throw new SaldoInsuficienteException(); }
                );
    }
}

