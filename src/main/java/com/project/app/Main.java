package com.project.app;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principal da aplicação JavaFX.
 *
 * <p>Responsável por iniciar o ciclo de vida JavaFX e configurar a janela inicial
 * por meio do {@link NavigationManager}.</p>
 *
 * <p>Ao iniciar, direciona o usuário para a tela de login.</p>
 */
public class Main extends Application {

    /**
     * Método chamado automaticamente pelo JavaFX após a inicialização do runtime.
     *
     * <p>Aqui ocorre a configuração inicial da UI, definindo o {@link Stage}
     * principal e enviando o usuário para a primeira tela do sistema.</p>
     *
     * @param stage janela principal da aplicação JavaFX
     */
    @Override
    public void start(Stage stage) {
        NavigationManager.initialize(stage);
        NavigationManager.goToLogin();
    }

    /**
     * Ponto de entrada tradicional da aplicação Java.
     *
     * <p>Delegado para o método {@link Application#launch(String...)},
     * que inicializa o ambiente JavaFX e chama {@link #start(Stage)}.</p>
     *
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        launch(args);
    }
}