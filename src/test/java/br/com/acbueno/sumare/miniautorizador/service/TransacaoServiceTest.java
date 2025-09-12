package br.com.acbueno.sumare.miniautorizador.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import br.com.acbueno.sumare.miniautorizador.dto.TransacaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.entity.Transacao;
import br.com.acbueno.sumare.miniautorizador.repository.CartaoRepository;
import br.com.acbueno.sumare.miniautorizador.repository.TransacaoRepository;
import jakarta.persistence.EntityManager;

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
	    
	    @Autowired
	    private EntityManager em;

	    @Test
	    void processarTransacao_saldoSuficiente_senhaCorreta() {
	        Cartao cartao = Cartao.builder().numeroCartao("1111").senha("1234").saldo(new BigDecimal("100")).build();
	        TransacaoDTO dto = TransacaoDTO.builder().numeroCartao("1111").senhaCartao("1234").valor(new BigDecimal("50")).build();

	        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(i -> i.getArgument(0));

	        Transacao tx = transacaoService.processarTransacao(cartao, dto);

	        assertEquals("OK", tx.getStatus());
	        assertEquals(new BigDecimal("50"), tx.getValor());
	        assertEquals(new BigDecimal("50"), cartao.getSaldo());
	    }

	    @Test
	    void processarTransacao_saldoInsuficiente() {
	        Cartao cartao = Cartao.builder().numeroCartao("1111").senha("1234").saldo(new BigDecimal("10")).build();
	        TransacaoDTO dto = TransacaoDTO.builder().numeroCartao("1111").senhaCartao("1234").valor(new BigDecimal("50")).build();

	        Transacao tx = transacaoService.processarTransacao(cartao, dto);

	        assertEquals("SALDO_INSUFICIENTE", tx.getStatus());
	        assertEquals(new BigDecimal("50"), tx.getValor());
	    }

	    @Test
	    void processarTransacao_senhaInvalida() {
	        Cartao cartao = Cartao.builder().numeroCartao("1111").senha("1234").saldo(new BigDecimal("100")).build();
	        TransacaoDTO dto = TransacaoDTO.builder().numeroCartao("1111").senhaCartao("0000").valor(new BigDecimal("50")).build();

	        Transacao tx = transacaoService.processarTransacao(cartao, dto);

	        assertEquals("SENHA_INVALIDA", tx.getStatus());
	        assertEquals(new BigDecimal("50"), tx.getValor());
	    }
	    
	    void testConcorrenciaTransacoes() throws InterruptedException, ExecutionException {
	        // Cria cartão com saldo 10
	        Cartao cartao = Cartao.builder()
	                .numeroCartao("1234567890123456")
	                .senha("1234")
	                .saldo(BigDecimal.valueOf(10))
	                .build();
	        cartaoRepository.save(cartao);
	        em.flush(); // <--- garante que o cartão está visível para outras transações

	        TransacaoDTO dto = TransacaoDTO.builder()
	                .numeroCartao("1234567890123456")
	                .senhaCartao("1234")
	                .valor(BigDecimal.valueOf(10))
	                .build();

	        ExecutorService executor = Executors.newFixedThreadPool(2);

	        Callable<String> task = () -> {
	            return transacaoService.processarTransacaoComLock(dto.getNumeroCartao(), dto).getStatus();
	        };

	        Future<String> f1 = executor.submit(task);
	        Future<String> f2 = executor.submit(task);

	        String resultado1 = f1.get();
	        String resultado2 = f2.get();

	        executor.shutdown();

	        // Apenas uma transação deve ser OK, a outra SALDO_INSUFICIENTE
	        assertEquals(1, (resultado1.equals("OK") ? 1 : 0) + (resultado2.equals("OK") ? 1 : 0));
	        assertEquals(1, (resultado1.equals("SALDO_INSUFICIENTE") ? 1 : 0) + (resultado2.equals("SALDO_INSUFICIENTE") ? 1 : 0));
	    }

}
