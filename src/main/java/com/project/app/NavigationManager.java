package com.project.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationManager {

    private static Stage primaryStage;
    private static Stage modalStage; // <-- Referência do modal atual

    public static void initialize(Stage stage) {
        primaryStage = stage;
        primaryStage.setResizable(false);
    }

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

    // ---- Atalhos ----
    public static void goToLogin() {
        goTo("/com/project/view/login.fxml", "Login");
    }

    public static void goToRegister() {
        goTo("/com/project/view/register.fxml", "Cadastro");
    }

    public static void goToMain() {
        goTo("/com/project/view/main.fxml", "Sistema de Glicose");
    }

    // ---- JANELAS MODAIS ----
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

    public static void closeModal() {
        if (modalStage != null) {
            modalStage.close();
            modalStage = null;
        }
    }
}