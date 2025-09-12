package br.com.acbueno.sumare.miniautorizador.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.acbueno.sumare.miniautorizador.entity.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
	

}
