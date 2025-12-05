package com.project.controller;

import com.project.app.NavigationManager;
import com.project.app.SessionManager;
import com.project.model.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller responsável pela tela "Mais" (More),
 * que exibe informações do paciente logado e opções adicionais.
 */
public class MoreController {

    /** Label com o nome completo do paciente. */
    @FXML private Label nameLabel;

    /** Label com o email do paciente. */
    @FXML private Label emailLabel;

    /** Label com a data de nascimento do paciente. */
    @FXML private Label birthLabel;

    /** Label com o tipo de diabetes informado pelo paciente. */
    @FXML private Label diabetesTypeLabel;

    /** Label com a sensibilidade a carboidratos efetiva usada no sistema. */
    @FXML private Label sensitivityLabel;

    /**
     * Método chamado automaticamente pelo JavaFX ao carregar o FXML.
     * Preenche todos os campos com os dados do paciente logado.
     */
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

    /**
     * Ação para futura implementação: abertura de modal
     * para ajuste de sensibilidade a carboidratos.
     */
    @FXML
    private void onAdjustSensitivity() {
        System.out.println("Ajuste de sensibilidade será implementado futuramente.");
    }

    /**
     * Finaliza a sessão atual e redireciona o usuário para a tela de login.
     */
    @FXML
    private void onLogout() {
        SessionManager.logout();
        NavigationManager.goToLogin();
    }
}