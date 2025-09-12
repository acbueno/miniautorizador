package br.com.acbueno.sumare.miniautorizador.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class CartaoDTO {
	
	private String numeroCartao;
	
	private String senha;
	
	private BigDecimal sado;

}
