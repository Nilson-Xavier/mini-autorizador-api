package br.com.vr.miniautorizador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.vr.miniautorizador")
public class MiniautorizadorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniautorizadorApplication.class, args);
	}

}
