package br.com.acbueno.sumare.miniautorizador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.acbueno.sumare.miniautorizador.dto.TransacaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.entity.Transacao;
import br.com.acbueno.sumare.miniautorizador.service.CartaoService;
import br.com.acbueno.sumare.miniautorizador.service.TransacaoService;

@RestController
@RequestMapping("/trasacaoes")
public class TrasacaoController {
	
	@Autowired
	private CartaoService cartaoService;
	
	@Autowired
	private TransacaoService transacaoService;
	
	@PostMapping
	public ResponseEntity<TransacaoDTO> realizar(@RequestBody TransacaoDTO dto) {
		Cartao cartao = cartaoService.consultarCartao(dto.getNumeroCartao());
		Transacao tx = transacaoService.processarTransacao(cartao.getNumeroCartao(), dto);
		
		if(!tx.getStatus().equals("OK")) {
			throw new RuntimeException(tx.getStatus());
		}
		TransacaoDTO responseDTO = TransacaoDTO.builder()
		.numeroCartao(tx.getCartao().getNumeroCartao())
		.valor(tx.getValor())
		.status(tx.getStatus())
		.build();
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(responseDTO);
	}

}
