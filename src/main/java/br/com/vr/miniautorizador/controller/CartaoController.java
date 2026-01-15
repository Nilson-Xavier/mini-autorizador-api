package br.com.vr.miniautorizador.controller;

import br.com.vr.miniautorizador.entity.Cartao;
import br.com.vr.miniautorizador.exception.CartaoExistenteException;
import br.com.vr.miniautorizador.exception.CartaoInexistenteException;
import br.com.vr.miniautorizador.service.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService service;

    @PostMapping
    public ResponseEntity<Cartao> criar(@RequestBody Cartao cartao) {
        try {
            Cartao novo = service.criarCartao(cartao.getNumeroCartao(), cartao.getSenha());
            return ResponseEntity.status(HttpStatus.CREATED).body(novo);
        } catch (CartaoExistenteException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(cartao);
        }
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> saldo(@PathVariable String numeroCartao) {
        try {
            return ResponseEntity.ok(service.obterSaldo(numeroCartao));
        } catch (CartaoInexistenteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

