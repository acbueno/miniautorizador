package br.com.acbueno.sumare.miniautorizador.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.acbueno.sumare.miniautorizador.dto.TransacaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.repository.CartaoRepository;

@SpringBootTest
@ActiveProfiles("test")
public class TransacaoServiceConcorrenciaTest {


	@Autowired
    private TransacaoService transacaoService;

    @Autowired
    private CartaoRepository cartaoRepository;

    @BeforeEach
    void setup() {
        cartaoRepository.deleteAll();
    }

    @Test
    void testConcorrenciaOtimista() throws Exception {
        // Cria cartão com saldo 10
        Cartao cartao = Cartao.builder()
                .numeroCartao("1234567890123456")
                .senha("1234")
                .saldo(BigDecimal.valueOf(10))
                .build();
        cartaoRepository.saveAndFlush(cartao);

        TransacaoDTO dto = TransacaoDTO.builder()
                .numeroCartao(cartao.getNumeroCartao())
                .senhaCartao(cartao.getSenha())
                .valor(BigDecimal.valueOf(10))
                .build();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<String> task = () -> {
            try {
                return transacaoService.processarTransacao(dto.getNumeroCartao(), dto).getStatus();
            } catch (Exception e) {
                // Captura qualquer exceção de lock otimista
                if (e.getClass().getSimpleName().contains("Optimistic")) {
                    return "OptimisticLockException";
                }
                return e.getClass().getSimpleName();
            }
        };

        Future<String> f1 = executor.submit(task);
        Future<String> f2 = executor.submit(task);

        String resultado1 = f1.get();
        String resultado2 = f2.get();

        executor.shutdown();

        System.out.println("Resultado1: " + resultado1);
        System.out.println("Resultado2: " + resultado2);

        // Apenas uma transação deve ser OK, a outra falha por lock otimista
        assertTrue(resultado1.equals("OK") || resultado2.equals("OK"));
        assertTrue(resultado1.equals("OptimisticLockException") || resultado2.equals("OptimisticLockException"));
    }

}
