package br.com.acbueno.sumare.miniautorizador.service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.acbueno.sumare.miniautorizador.controller.TrasacaoController;
import br.com.acbueno.sumare.miniautorizador.dto.TransacaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.entity.Transacao;
import br.com.acbueno.sumare.miniautorizador.service.CartaoService;
import br.com.acbueno.sumare.miniautorizador.service.TransacaoService;

@WebMvcTest(TrasacaoController.class)
public class TrasacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartaoService cartaoService;

    @MockBean
    private TransacaoService transacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void realizarTransacao_deveRetornarCreated_quandoStatusOK() throws Exception {
        // DTO de entrada
        TransacaoDTO dto = TransacaoDTO.builder()
                .numeroCartao("1234567890123456")
                .valor(new BigDecimal("50"))
                .senhaCartao("1234")
                .build();

        // Mock do Cartao retornado pelo serviço
        Cartao cartao = Cartao.builder()
                .numeroCartao(dto.getNumeroCartao())
                .senha("1234")
                .saldo(new BigDecimal("100"))
                .build();

        // Mock do Transacao retornado pelo serviço
        Transacao tx = Transacao.builder()
                .cartao(cartao)
                .valor(dto.getValor())
                .status("OK")
                .build();

        when(cartaoService.consultarCartao(dto.getNumeroCartao())).thenReturn(cartao);     
        when(transacaoService.processarTransacao(
                anyString(),
                any(TransacaoDTO.class)
        )).thenReturn(tx);

        mockMvc.perform(post("/trasacaoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value(dto.getNumeroCartao()))
                .andExpect(jsonPath("$.valor").value(dto.getValor().doubleValue()))
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    void realizarTransacao_deveLancarException_quandoStatusNaoOK() throws Exception {
        TransacaoDTO dto = TransacaoDTO.builder()
                .numeroCartao("1234567890123456")
                .valor(new BigDecimal("50"))
                .build();

        Cartao cartao = Cartao.builder()
                .numeroCartao(dto.getNumeroCartao())
                .senha("1234")
                .saldo(new BigDecimal("30"))
                .build();

        Transacao tx = Transacao.builder()
                .cartao(cartao)
                .valor(dto.getValor())
                .status("SALDO_INSUFICIENTE")
                .build();

        when(cartaoService.consultarCartao(dto.getNumeroCartao())).thenReturn(cartao);
        when(transacaoService.processarTransacao(dto.getNumeroCartao(), dto)).thenReturn(tx);

        mockMvc.perform(post("/trasacaoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError()); // RuntimeException disparada
    }
}