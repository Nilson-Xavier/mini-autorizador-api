package br.com.vr.miniautorizador.controller;

import br.com.vr.miniautorizador.exception.CartaoInexistenteException;
import br.com.vr.miniautorizador.exception.SaldoInsuficienteException;
import br.com.vr.miniautorizador.exception.SenhaInvalidaException;
import br.com.vr.miniautorizador.model.TransacaoRequest;
import br.com.vr.miniautorizador.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService service;

    @PostMapping
    public ResponseEntity<String> transacao(@RequestBody TransacaoRequest request) {
        try {
            service.autorizar(request.numeroCartao(), request.senhaCartao(), request.valor());
            return ResponseEntity.status(HttpStatus.CREATED).body("OK");
        } catch (SenhaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("SENHA_INVALIDA");
        } catch (SaldoInsuficienteException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("SALDO_INSUFICIENTE");
        } catch (CartaoInexistenteException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("CARTAO_INEXISTENTE");
        }
    }
}

