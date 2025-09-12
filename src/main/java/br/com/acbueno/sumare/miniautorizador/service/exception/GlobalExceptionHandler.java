package br.com.acbueno.sumare.miniautorizador.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntime(RuntimeException ex) {
		String msg = ex.getMessage();
		HttpStatus status  = switch (msg) {
		case "CARTAO_EXISTENTE", "SALDO_INSUFICIENTE", "SENHA_INVALIDA", "CARTAO_INEXISTENTE" -> HttpStatus.UNPROCESSABLE_ENTITY;
		case  "CARTAO_NAO_ENCONTRADO" -> HttpStatus.NOT_FOUND;
		default -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
		return ResponseEntity.status(status).body(msg);
	}

}
