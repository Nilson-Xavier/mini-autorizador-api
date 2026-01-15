package br.com.vr.miniautorizador.model;

import java.math.BigDecimal;

public record TransacaoRequest(
        String numeroCartao,
        String senhaCartao,
        BigDecimal valor
) {}

