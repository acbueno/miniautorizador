package br.com.acbueno.sumare.miniautorizador.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cartao")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class Cartao {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "numero-cartao", unique = true, nullable = false, length = 16)
	private String numeroCartao;
	
	@Column(name = "senha", nullable = false, length = 10)
	private String senha;
	
	@Column(nullable = false)
	private BigDecimal saldo;
	
	@OneToMany(mappedBy = "cartao", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Transacao> transacoes = new ArrayList<>();
	
	@Version
	private Long version;
	
    public void adicionarTransacao(Transacao tx) {
        tx.setCartao(this);
        transacoes.add(tx);
    }
    
    

}
