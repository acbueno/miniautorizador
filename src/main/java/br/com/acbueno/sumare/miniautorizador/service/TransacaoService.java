package br.com.acbueno.sumare.miniautorizador.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.acbueno.sumare.miniautorizador.dto.TransacaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.entity.Transacao;
import br.com.acbueno.sumare.miniautorizador.repository.CartaoRepository;
import br.com.acbueno.sumare.miniautorizador.repository.TransacaoRepository;

@Service
public class TransacaoService {

	@Autowired
	private TransacaoRepository transacaoRepository;

	@Autowired
	private CartaoRepository cartaoRepository;

	@Transactional
	public Transacao processarTransacao(String numeroCartao, TransacaoDTO dto) {
		Cartao cartao = cartaoRepository.findByNumeroCartao(numeroCartao)
				.orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

		return cartao.getSaldo().compareTo(dto.getValor()) < 0
				? transacaoNaoAutorizada(cartao, dto, "SALDO_INSUFICIENTE")
				: cartao.getSenha().equals(dto.getSenhaCartao()) ? transacaoAutorizada(cartao, dto)
						: transacaoNaoAutorizada(cartao, dto, "SENHA_INVALIDA");
	}

	private Transacao transacaoAutorizada(Cartao cartao, TransacaoDTO dto) {
		cartao.setSaldo(cartao.getSaldo().subtract(dto.getValor()));

		// Persistir cartão para forçar incremento da versão lock otimista
		cartaoRepository.saveAndFlush(cartao);

		Transacao tx = Transacao.builder().cartao(cartao).valor(dto.getValor()).status("OK").build();

		cartao.adicionarTransacao(tx);
		return transacaoRepository.save(tx);
	}

	private Transacao transacaoNaoAutorizada(Cartao cartao, TransacaoDTO dto, String status) {
		return Transacao.builder().cartao(cartao).valor(dto.getValor()).status(status).build();
	}

}
