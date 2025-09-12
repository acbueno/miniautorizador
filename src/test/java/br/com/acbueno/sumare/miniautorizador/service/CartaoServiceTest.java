package br.com.acbueno.sumare.miniautorizador.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.acbueno.sumare.miniautorizador.dto.CartaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.repository.CartaoRepository;

public class CartaoServiceTest {

	@InjectMocks
	private CartaoService cartaoService;

	@Mock
	private CartaoRepository cartaoRepository;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void criarCartao_sucesso() {
		CartaoDTO cartaoDTO = CartaoDTO.builder().numeroCartao("1234567890123456").senha("1234").build();

		when(cartaoRepository.existsByNumeroCartao(cartaoDTO.getNumeroCartao())).thenReturn(false);
		when(cartaoRepository.save(any(Cartao.class))).then(i -> i.getArgument(0));

		Cartao cartao = cartaoService.criarCartao(cartaoDTO);

		assertEquals("1234567890123456", cartao.getNumeroCartao());
		assertEquals("1234", cartao.getSenha());
		verify(cartaoRepository, times(1)).save(any(Cartao.class));
	}

	@Test
	void criarCartao_jaExistente() {

		CartaoDTO cartaoDTO = CartaoDTO.builder().numeroCartao("1234567890123456").senha("1234").build();

		when(cartaoRepository.existsByNumeroCartao(cartaoDTO.getNumeroCartao())).thenReturn(true);
		RuntimeException ex = assertThrows(RuntimeException.class, () -> cartaoService.criarCartao(cartaoDTO));
		assertEquals("CARTAO_EXISTENTE", ex.getMessage());
	}

	@Test
	void consultarSaldo_sucesso() {
		Cartao c = Cartao.builder().numeroCartao("1111").saldo(new BigDecimal("200")).build();
		when(cartaoRepository.findByNumeroCartao("1111")).thenReturn(Optional.of(c));

		BigDecimal saldo = cartaoService.consultarSaldo("1111");
		assertEquals(new BigDecimal("200"), saldo);
	}

	@Test
	void consultarSaldo_cartaoNaoEncontrado() {
		when(cartaoRepository.findByNumeroCartao("0000")).thenReturn(Optional.empty());

		RuntimeException ex = assertThrows(RuntimeException.class, () -> cartaoService.consultarSaldo("0000"));
		assertEquals("CARTAO_NAO_ENCONTRADO", ex.getMessage());
	}

	@Test
	void buscarCartao_sucesso() {
		Cartao c = Cartao.builder().numeroCartao("1111").build();
		when(cartaoRepository.findByNumeroCartao("1111")).thenReturn(Optional.of(c));

		Cartao result = cartaoService.consultarCartao("1111");
		assertEquals("1111", result.getNumeroCartao());
	}

	@Test
	void buscarCartao_inexistente() {
		when(cartaoRepository.findByNumeroCartao("0000")).thenReturn(Optional.empty());

		RuntimeException ex = assertThrows(RuntimeException.class, () -> cartaoService.consultarCartao("0000"));
		assertEquals("CARTAO_INEXISTENTE", ex.getMessage());
	}

}
