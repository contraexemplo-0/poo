package com.project.controller;

import com.project.app.NavigationManager;
import com.project.app.SessionManager;
import com.project.model.Patient;
import com.project.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService;

    public LoginController() {
        try {
            this.userService = new UserService();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar UserService", e);
        }
    }

    @FXML
    private void onLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Preencha email e senha.");
            return;
        }

        Patient patient = userService.login(email, password);

        if (patient == null) {
            showError("Credenciais inválidas.");
            return;
        }

        // Guardar sessão
        SessionManager.setCurrentPatient(patient);

        // Ir para o dashboard
        NavigationManager.goToMain();
    }

    @FXML
    private void onGoToRegister() {
        NavigationManager.goToRegister();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    @FXML private ImageView logoImage;

    public void initialize() {
        try {
            logoImage.setImage(new Image(
                    getClass().getResource("/com/project/view/img/logo.png").toExternalForm()
            ));
        } catch (Exception e) {
            System.out.println("Erro ao carregar logo: " + e.getMessage());
        }
    }

}
