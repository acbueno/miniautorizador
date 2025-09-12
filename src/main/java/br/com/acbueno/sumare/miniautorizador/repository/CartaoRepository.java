package br.com.acbueno.sumare.miniautorizador.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import jakarta.persistence.LockModeType;

@Repository
public interface CartaoRepository  extends JpaRepository<Cartao, Long>{
	
	Optional<Cartao> findByNumeroCartao(String numeroCartao);
	
	boolean existsByNumeroCartao(String numeroCartao);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Cartao c where c.numeroCartao = :numeroCartao")
	Optional<Cartao> findByNumeroCartaoForUpdate(@Param("numeroCartao") String numeroCartao);


}
