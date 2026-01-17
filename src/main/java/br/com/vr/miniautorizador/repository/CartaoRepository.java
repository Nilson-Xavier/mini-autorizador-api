package br.com.vr.miniautorizador.repository;

import br.com.vr.miniautorizador.entity.Cartao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartaoRepository extends JpaRepository<Cartao, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cartao c where c.numeroCartao = :numero")
    Optional<Cartao> findByNumeroForUpdate(String numero);

    void deleteBynumeroCartao(String numeroCartao);
}

