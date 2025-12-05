package com.project.controller;

import com.project.app.NavigationManager;
import com.project.app.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controller principal da aplicação após o login.
 * Gerencia a navegação entre as abas (Dashboard, Relatórios, Educação e Mais),
 * carregando as views dinamicamente no espaço central da interface.
 */
public class MainController {

    /**
     * Área central onde as telas (views) são carregadas dinamicamente.
     */
    @FXML
    private StackPane contentArea;

    /**
     * Método chamado automaticamente após o carregamento do FXML.
     * Exibe o dashboard como tela inicial.
     */
    @FXML
    private void initialize() {
        showDashboard();
    }

    /**
     * Exibe a tela de Dashboard na área central.
     */
    @FXML
    private void showDashboard() {
        loadView("/com/project/view/dashboard.fxml");
    }

    /**
     * Exibe a tela de Relatórios.
     */
    @FXML
    private void showReports() {
        loadView("/com/project/view/reports.fxml");
    }

    /**
     * Exibe a tela de Educação.
     */
    @FXML
    private void showEducation() {
        loadView("/com/project/view/education.fxml");
    }

    /**
     * Exibe a tela de Opções Adicionais ("Mais").
     */
    @FXML
    private void showMore() {
        loadView("/com/project/view/more.fxml");
    }

    /**
     * Encerra a sessão atual e redireciona o usuário para a tela de login.
     */
    @FXML
    private void logout() {
        SessionManager.logout();
        NavigationManager.goToLogin();
    }

    /**
     * Carrega um arquivo FXML e o insere dentro da área principal de conteúdo.
     *
     * @param fxmlPath caminho do arquivo FXML relativo ao resources.
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar view: " + fxmlPath);
        }
    }
}
