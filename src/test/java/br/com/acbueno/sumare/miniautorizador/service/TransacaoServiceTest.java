package br.com.acbueno.sumare.miniautorizador.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.acbueno.sumare.miniautorizador.dto.TransacaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.entity.Transacao;
import br.com.acbueno.sumare.miniautorizador.repository.CartaoRepository;
import br.com.acbueno.sumare.miniautorizador.repository.TransacaoRepository;

public class TransacaoServiceTest {
	
	 @InjectMocks
	    private TransacaoService transacaoService;

	    @Mock
	    private TransacaoRepository transacaoRepository;
	    
	    @Mock
	    private CartaoRepository cartaoRepository;

	    @BeforeEach
	    void setup() {
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    void processarTransacao_saldoSuficiente_senhaCorreta() {
	        Cartao cartao = Cartao.builder().numeroCartao("1111").senha("1234").saldo(new BigDecimal("100")).build();
	        TransacaoDTO dto = TransacaoDTO.builder().numeroCartao("1111").senhaCartao("1234").valor(new BigDecimal("50")).build();

	        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(i -> i.getArgument(0));
	        when(cartaoRepository.findByNumeroCartao("1111"))
            .thenReturn(Optional.of(cartao));
	        Transacao tx = transacaoService.processarTransacao(cartao.getNumeroCartao(), dto);

	        assertEquals("OK", tx.getStatus());
	        assertEquals(new BigDecimal("50"), tx.getValor());
	        assertEquals(new BigDecimal("50"), cartao.getSaldo());
	    }

	    @Test
	    void processarTransacao_saldoInsuficiente() {
	        Cartao cartao = Cartao.builder().numeroCartao("1111").senha("1234").saldo(new BigDecimal("10")).build();
	        TransacaoDTO dto = TransacaoDTO.builder().numeroCartao("1111").senhaCartao("1234").valor(new BigDecimal("50")).build();
	        when(cartaoRepository.findByNumeroCartao("1111"))
            .thenReturn(Optional.of(cartao));
	        Transacao tx = transacaoService.processarTransacao(cartao.getNumeroCartao(), dto);

	        assertEquals("SALDO_INSUFICIENTE", tx.getStatus());
	        assertEquals(new BigDecimal("50"), tx.getValor());
	    }

	    @Test
	    void processarTransacao_senhaInvalida() {
	        Cartao cartao = Cartao.builder().numeroCartao("1111").senha("1234").saldo(new BigDecimal("100")).build();
	        TransacaoDTO dto = TransacaoDTO.builder().numeroCartao("1111").senhaCartao("0000").valor(new BigDecimal("50")).build();
	        when(cartaoRepository.findByNumeroCartao("1111"))
            .thenReturn(Optional.of(cartao));
	        Transacao tx = transacaoService.processarTransacao(cartao.getNumeroCartao(), dto);

	        assertEquals("SENHA_INVALIDA", tx.getStatus());
	        assertEquals(new BigDecimal("50"), tx.getValor());
	    }

}
