package br.com.acbueno.sumare.miniautorizador.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.acbueno.sumare.miniautorizador.entity.Cartao;

@Repository
public interface CartaoRepository  extends JpaRepository<Cartao, Long>{
	
	Optional<Cartao> findByNumeroCartao(String numeroCartao);
	
	boolean existsByNumeroCartao(String numeroCartao);


}
