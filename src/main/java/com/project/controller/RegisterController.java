package com.project.controller;

import com.project.app.NavigationManager;
import com.project.model.Patient;
import com.project.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private DatePicker birthDatePicker;
    @FXML private DatePicker diagnosisDatePicker;

    @FXML private ComboBox<String> genderCombo;
    @FXML private ComboBox<String> diabetesTypeCombo;

    @FXML private Label errorLabel;

    private final UserService userService;

    public RegisterController() {
        try {
            this.userService = new UserService();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar UserService", e);
        }
    }

    @FXML
    private void initialize() {
        genderCombo.getItems().addAll("Masculino", "Feminino", "Outro");
        diabetesTypeCombo.getItems().addAll("Tipo 1", "Tipo 2", "LADA", "Gestacional");
    }

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

            // Se cadastrou, vai para Login
            NavigationManager.goToLogin();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    @FXML
    private void onGoToLogin() {
        NavigationManager.goToLogin();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
