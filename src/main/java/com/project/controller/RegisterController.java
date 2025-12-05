package com.project.controller;

import com.project.app.NavigationManager;
import com.project.model.Patient;
import com.project.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

import java.time.LocalDate;

/**
 * Controller responsável pela tela de registro de novos pacientes.
 * Valida os campos da interface, cria um novo usuário e redireciona para a tela de login.
 */
public class RegisterController {

    /** Campo de texto para inserção do nome do paciente. */
    @FXML private TextField nameField;

    /** Campo de texto para inserção do e-mail do paciente. */
    @FXML private TextField emailField;

    /** Campo de senha para inserção da senha do paciente. */
    @FXML private PasswordField passwordField;

    /** Seletor de data de nascimento. */
    @FXML private DatePicker birthDatePicker;

    /** Seletor de data de diagnóstico da diabetes. */
    @FXML private DatePicker diagnosisDatePicker;

    /** ComboBox para seleção de gênero. */
    @FXML private ComboBox<String> genderCombo;

    /** ComboBox para seleção do tipo de diabetes. */
    @FXML private ComboBox<String> diabetesTypeCombo;

    /** Label responsável por exibir mensagens de erro. */
    @FXML private Label errorLabel;

    /** Imagem do logo exibido na tela de registro. */
    @FXML private ImageView logoImage;

    /** Serviço responsável pela lógica de cadastro de usuários. */
    private final UserService userService;

    /**
     * Construtor padrão.
     * Inicializa o {@link UserService}.
     */
    public RegisterController() {
        try {
            this.userService = new UserService();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar UserService", e);
        }
    }

    /**
     * Método executado automaticamente pelo JavaFX após o carregamento do FXML.
     * Responsável por carregar a imagem do logo e preencher os ComboBoxes.
     */
    @FXML
    private void initialize() {

        try {
            logoImage.setImage(
                    new Image(Objects.requireNonNull(
                            getClass().getResourceAsStream("/com/project/view/img/logo.png")
                    ))
            );
        } catch (Exception e) {
            System.out.println("Erro ao carregar logo: " + e.getMessage());
        }

        genderCombo.getItems().addAll("Masculino", "Feminino", "Outro");
        diabetesTypeCombo.getItems().addAll("Tipo 1", "Tipo 2", "LADA", "Gestacional");
    }

    /**
     * Ação executada ao clicar no botão de registro.
     * Valida todos os dados, cria o paciente e redireciona para a tela de login.
     */
    @FXML
    private void onRegister() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            LocalDate birthDate = birthDatePicker.getValue();
            LocalDate diagnosisDate = diagnosisDatePicker.getValue();

            String gender = genderCombo.getValue();
            String diabetesType = diabetesTypeCombo.getValue();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                    birthDate == null || diagnosisDate == null ||
                    gender == null || diabetesType == null) {
                showError("Preencha todos os campos.");
                return;
            }

            // Sensibilidade padrão
            Float carbSensitivity = Patient.DEFAULT_CARB_SENSITIVITY;

            Patient newPatient = userService.register(
                    name, email, password,
                    birthDate, gender, diabetesType, diagnosisDate,
                    carbSensitivity
            );

            // Se cadastro for bem-sucedido, retorna à tela de login
            NavigationManager.goToLogin();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    /**
     * Retorna o usuário para a tela de login.
     */
    @FXML
    private void onGoToLogin() {
        NavigationManager.goToLogin();
    }

    /**
     * Exibe uma mensagem de erro na interface.
     *
     * @param msg texto da mensagem de erro
     */
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
