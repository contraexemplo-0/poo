package com.project.controller;

import com.project.app.NavigationManager;
import com.project.app.SessionManager;
import com.project.model.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MoreController {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label birthLabel;
    @FXML private Label diabetesTypeLabel;
    @FXML private Label sensitivityLabel;

    @FXML
    private void initialize() {
        Patient p = SessionManager.getCurrentPatient();
        if (p == null) return;

        nameLabel.setText(p.getName());
        emailLabel.setText(p.getEmail() != null ? p.getEmail() : "-");
        birthLabel.setText(p.getDateOfBirth() != null ? p.getDateOfBirth().toString() : "-");
        diabetesTypeLabel.setText(p.getDiabetesType() != null ? p.getDiabetesType() : "-");
        sensitivityLabel.setText(String.valueOf(p.getEffectiveCarbSensitivity()));
    }

    @FXML
    private void onAdjustSensitivity() {
        // FUTURO: Abrir modal para ajustar sensibilidade
        System.out.println("Ajuste de sensibilidade será implementado futuramente.");
    }

    @FXML
    private void onLogout() {
        SessionManager.logout();
        NavigationManager.goToLogin();
    }
}