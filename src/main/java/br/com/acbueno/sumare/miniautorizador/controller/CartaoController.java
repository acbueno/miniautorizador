package br.com.acbueno.sumare.miniautorizador.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.acbueno.sumare.miniautorizador.dto.CartaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.service.CartaoService;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {
	
	@Autowired
	private CartaoService cartaoService;
	
	@PostMapping
	public ResponseEntity<CartaoDTO> criarCartao(@RequestBody CartaoDTO dto) {
		Cartao cartao = cartaoService.criarCartao(dto);
		CartaoDTO responseDTO = CartaoDTO.builder()
		.numeroCartao(cartao.getNumeroCartao())
		.senha(cartao.getSenha())
		.sado(cartao.getSaldo())
		.build();
		
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
	}
	
	@GetMapping("/{numeroCartao}/saldo")
	public ResponseEntity<BigDecimal> consultaSaldo(@PathVariable String numeroCartao) {
		return ResponseEntity.ok().body(cartaoService.consultarSaldo(numeroCartao));
	}

}
