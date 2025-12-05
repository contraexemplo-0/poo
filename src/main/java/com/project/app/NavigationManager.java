package com.project.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe responsável por gerenciar a navegação entre telas da aplicação em JavaFX.
 * Centraliza a troca de cenas, abertura de janelas modais e carregamento de FXML.
 */
public class NavigationManager {

    /** Janela principal da aplicação. */
    private static Stage primaryStage;

    /** Referência da janela modal atualmente aberta (se houver). */
    private static Stage modalStage;

    /**
     * Inicializa o gerenciador de navegação configurando o Stage principal.
     *
     * @param stage janela principal da aplicação
     */
    public static void initialize(Stage stage) {
        primaryStage = stage;
        primaryStage.setResizable(false);
    }

    /**
     * Carrega um arquivo FXML e substitui a cena atual da janela principal.
     *
     * @param fxmlPath caminho do arquivo FXML relativo ao diretório resources
     * @param title título da janela após o carregamento
     */
    public static void goTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            primaryStage.setTitle(title);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    NavigationManager.class.getResource("/com/project/view/css/app.css").toExternalForm()
            );

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar tela: " + fxmlPath);
        }
    }

    // ---- Atalhos de Navegação ----

    /**
     * Redireciona para a tela de login.
     */
    public static void goToLogin() {
        goTo("/com/project/view/login.fxml", "Login");
    }

    /**
     * Redireciona para a tela de cadastro.
     */
    public static void goToRegister() {
        goTo("/com/project/view/register.fxml", "Cadastro");
    }

    /**
     * Redireciona para a tela principal (dashboard + navegação).
     */
    public static void goToMain() {
        goTo("/com/project/view/main.fxml", "Sistema de Glicose");
    }

    // ---- Janelas Modais ----

    /**
     * Abre uma janela modal sobre a janela principal.
     * O modal bloqueia interação com a janela principal até ser fechado.
     *
     * @param fxmlPath caminho do FXML a ser carregado
     * @param title título da janela modal
     */
    public static void openModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            modalStage = new Stage();
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(primaryStage);
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);

            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao abrir modal: " + fxmlPath);
        }
    }

    /**
     * Fecha o modal atualmente aberto, caso exista.
     * Remove a referência interna após fechar.
     */
    public static void closeModal() {
        if (modalStage != null) {
            modalStage.close();
            modalStage = null;
        }
    }
}