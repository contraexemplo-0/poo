package com.project.controller;

import com.project.app.NavigationManager;
import com.project.app.SessionManager;
import com.project.model.Patient;
import com.project.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller responsável pela tela de login da aplicação.
 * Realiza autenticação do paciente e redireciona para o dashboard.
 */
public class LoginController {

    /** Campo de texto para entrada do e-mail do usuário. */
    @FXML private TextField emailField;

    /** Campo de senha para entrada da senha do usuário. */
    @FXML private PasswordField passwordField;

    /** Label usada para exibir mensagens de erro de login. */
    @FXML private Label errorLabel;

    /** Serviço responsável pela autenticação de usuários. */
    private final UserService userService;

    /**
     * Construtor padrão do controller.
     * Inicializa a instância de {@link UserService}.
     */
    public LoginController() {
        try {
            this.userService = new UserService();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar UserService", e);
        }
    }

    /**
     * Ação executada ao pressionar o botão de login.
     * Valida campos, tenta autenticar o usuário e, caso bem-sucedido,
     * inicia a sessão e navega para o dashboard.
     */
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

    /**
     * Ação executada ao clicar no botão "Registrar".
     * Navega para a tela de registro de usuário.
     */
    @FXML
    private void onGoToRegister() {
        NavigationManager.goToRegister();
    }

    /**
     * Exibe uma mensagem de erro no label dedicado.
     *
     * @param msg mensagem a ser exibida.
     */
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    /** Elemento visual para exibir o logo da aplicação. */
    @FXML private ImageView logoImage;

    /**
     * Método chamado automaticamente após o carregamento do FXML.
     * Responsável por carregar a imagem do logo na tela de login.
     */
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