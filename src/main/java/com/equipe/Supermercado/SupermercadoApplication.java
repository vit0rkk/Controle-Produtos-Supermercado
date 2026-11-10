package com.equipe.Supermercado;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;

@SpringBootApplication
public class SupermercadoApplication {

	public static void main(String[] args) {
		Application.launch(TelaPrincipal.class, args); // abre a janela JavaFX
	}

}
