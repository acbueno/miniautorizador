package br.com.acbueno.sumare.miniautorizador.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.acbueno.sumare.miniautorizador.dto.CartaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.repository.CartaoRepository;

@Service
public class CartaoService {
	
	@Autowired
	private CartaoRepository cartaoRepository;
	
	public Cartao criarCartao(CartaoDTO dto) {
		if(cartaoRepository.existsByNumeroCartao(dto.getNumeroCartao())) {
			throw new RuntimeException("CARTAO_EXISTENTE");
		}
		
		Cartao cartao = Cartao.builder()
				.numeroCartao(dto.getNumeroCartao())
				.senha(dto.getSenha())
				.saldo(new BigDecimal("500.00"))
				.build();
		
		return cartaoRepository.save(cartao);
	}
	
	public BigDecimal consultarSaldo(String numeroCartao) {
		return cartaoRepository.findByNumeroCartao(numeroCartao)
				.map(Cartao::getSaldo)
				.orElseThrow(() -> new RuntimeException("CARTAO_NAO_ENCONTRADO"));
	}
	
	public Cartao consultarCartao(String numeroCartao) {
		return cartaoRepository.findByNumeroCartao(numeroCartao)
				.orElseThrow(() -> new RuntimeException("CARTAO_INEXISTENTE"));
	}

}
