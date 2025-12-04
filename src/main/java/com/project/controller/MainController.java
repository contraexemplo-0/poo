package com.project.controller;

import com.project.app.NavigationManager;
import com.project.app.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private void initialize() {
        // Mostra o dashboard ao abrir a tela principal
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/com/project/view/dashboard.fxml");
    }

    @FXML
    private void showReports() {
        loadView("/com/project/view/reports.fxml");
    }

    @FXML
    private void showEducation() {
        loadView("/com/project/view/education.fxml");
    }

    @FXML
    private void showMore() {
        loadView("/com/project/view/more.fxml");
    }

    @FXML
    private void logout() {
        SessionManager.logout();
        NavigationManager.goToLogin();
    }

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
