package com.project.controller;

import com.project.app.NavigationManager;
import com.project.app.SessionManager;
import com.project.model.*;
import com.project.service.RoutineEventService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller responsável pelo modal de criação de um novo evento.
 * Permite registrar glicemias, refeições ou ambos, dependendo da aba selecionada.
 */
public class NewEventDialogController {

    /** Campo para inserção do valor de glicemia. */
    @FXML private TextField glucoseField;

    /** ComboBox para seleção da categoria glicêmica (hipo, normal, hiper). */
    @FXML private ComboBox<GlucoseCategory> glucoseCategoryCombo;

    /** Campo para inserção da data e hora da glicemia. */
    @FXML private TextField glucoseDateTimeField;

    /** Campo para descrição da refeição. */
    @FXML private TextField mealDescriptionField;

    /** ComboBox para seleção do tipo de refeição. */
    @FXML private ComboBox<MealCategory> mealCategoryCombo;

    /** Campo para entrada de carboidratos estimados. */
    @FXML private TextField carbsField;

    /** Campo para entrada do índice glicêmico estimado. */
    @FXML private TextField giField;

    /** Campo para data e hora da refeição. */
    @FXML private TextField mealDateTimeField;

    /** Abas de seleção entre: registro de glicemia ou refeição. */
    @FXML private TabPane tabPane;

    /** Serviço responsável por registrar eventos na base de dados. */
    private RoutineEventService eventService;

    /** Formatador padrão para datas e horários digitados pelo usuário. */
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Construtor padrão do controller.
     * Inicializa o serviço de eventos.
     */
    public NewEventDialogController() {
        try {
            this.eventService = new RoutineEventService(null);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar rotina", e);
        }
    }

    /**
     * Método chamado automaticamente após o carregamento do FXML.
     * Preenche os ComboBox com valores dos enums correspondentes.
     */
    @FXML
    private void initialize() {
        glucoseCategoryCombo.getItems().addAll(GlucoseCategory.values());
        mealCategoryCombo.getItems().addAll(MealCategory.values());
    }

    /**
     * Ação para fechar o modal sem salvar nada.
     */
    @FXML
    private void onCancel() {
        NavigationManager.closeModal();
    }

    /**
     * Ação principal do botão "Salvar".
     * Detecta a aba ativa (glicemia ou refeição) e processa o tipo de registro apropriado.
     * Após salvar, fecha o modal.
     */
    @FXML
    private void onSave() {
        Patient patient = SessionManager.getCurrentPatient();

        if (patient == null) {
            NavigationManager.closeModal();
            return;
        }

        int selectedTab = tabPane.getSelectionModel().getSelectedIndex();

        try {
            if (selectedTab == 0) {
                saveGlucose(patient);
            } else if (selectedTab == 1) {
                saveMeal(patient);
            }

            NavigationManager.closeModal();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * Salva um registro de glicemia simples.
     *
     * @param patient paciente logado
     * @throws Exception caso valores inseridos sejam inválidos
     */
    private void saveGlucose(Patient patient) throws Exception {
        Float value = Float.parseFloat(glucoseField.getText());
        GlucoseCategory category = glucoseCategoryCombo.getValue();
        LocalDateTime dateTime = LocalDateTime.parse(glucoseDateTimeField.getText(), dtf);

        eventService.registerGlucose(patient, value, category, dateTime);
    }

    /**
     * Salva um registro de refeição, podendo incluir glicemia associada.
     *
     * @param patient paciente logado
     * @throws Exception caso algum campo seja inválido
     */
    private void saveMeal(Patient patient) throws Exception {
        String desc = mealDescriptionField.getText();
        MealCategory category = mealCategoryCombo.getValue();

        Float carbs = carbsField.getText().isEmpty() ? null : Float.parseFloat(carbsField.getText());
        Float gi = giField.getText().isEmpty() ? null : Float.parseFloat(giField.getText());

        LocalDateTime mealDateTime = LocalDateTime.parse(mealDateTimeField.getText(), dtf);

        if (glucoseField.getText().isEmpty()) {
            // Refeição sem glicemia associada
            eventService.registerMealOnly(patient, desc, mealDateTime, category);
        } else {
            // Refeição com glicemia associada
            Float glucose = Float.parseFloat(glucoseField.getText());
            GlucoseCategory gCat = glucoseCategoryCombo.getValue();
            LocalDateTime gDate = LocalDateTime.parse(glucoseDateTimeField.getText(), dtf);

            eventService.registerMealAndGlucose(
                    patient, desc, mealDateTime, category,
                    glucose, gCat, gDate
            );
        }
    }

    /**
     * Exibe mensagem de erro no console.
     * (Futuro: pode ser substituído por alerta visual na UI)
     *
     * @param msg mensagem de erro
     */
    private void showError(String msg) {
        System.out.println(msg);
    }
}