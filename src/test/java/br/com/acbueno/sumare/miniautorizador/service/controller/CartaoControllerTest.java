package br.com.acbueno.sumare.miniautorizador.service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;


import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.acbueno.sumare.miniautorizador.controller.CartaoController;
import br.com.acbueno.sumare.miniautorizador.dto.CartaoDTO;
import br.com.acbueno.sumare.miniautorizador.entity.Cartao;
import br.com.acbueno.sumare.miniautorizador.service.CartaoService;

@WebMvcTest(CartaoController.class)
public class CartaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartaoService cartaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criarCartao_deveRetornarCreated() throws Exception {
        CartaoDTO dto = CartaoDTO.builder()
                .numeroCartao("1234567890123456")
                .senha("1234")
                .sado(new BigDecimal("100"))
                .build();

        Cartao cartao = Cartao.builder()
                .numeroCartao(dto.getNumeroCartao())
                .senha(dto.getSenha())
                .saldo(dto.getSado())
                .build();

        when(cartaoService.criarCartao(any(CartaoDTO.class))).thenReturn(cartao);

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value(dto.getNumeroCartao()))
                .andExpect(jsonPath("$.senha").value(dto.getSenha()))
                .andExpect(jsonPath("$.sado").value(dto.getSado()));
    }

    @Test
    void consultaSaldo_deveRetornarSaldo() throws Exception {
        String numeroCartao = "1234567890123456";
        BigDecimal saldo = new BigDecimal("150");

        when(cartaoService.consultarSaldo(numeroCartao)).thenReturn(saldo);

        mockMvc.perform(get("/cartoes/{numeroCartao}/saldo", numeroCartao))
                .andExpect(status().isOk())
                .andExpect(content().string(saldo.toString()));
    }
}