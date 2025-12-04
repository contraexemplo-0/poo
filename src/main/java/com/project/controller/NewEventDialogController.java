package com.project.controller;

import com.project.app.NavigationManager;
import com.project.app.SessionManager;
import com.project.model.*;
import com.project.service.RoutineEventService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NewEventDialogController {

    @FXML private TextField glucoseField;
    @FXML private ComboBox<GlucoseCategory> glucoseCategoryCombo;
    @FXML private TextField glucoseDateTimeField;

    @FXML private TextField mealDescriptionField;
    @FXML private ComboBox<MealCategory> mealCategoryCombo;
    @FXML private TextField carbsField;
    @FXML private TextField giField;
    @FXML private TextField mealDateTimeField;

    @FXML private TabPane tabPane;

    private RoutineEventService eventService;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public NewEventDialogController() {
        try {
            this.eventService = new RoutineEventService(null);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar rotina", e);
        }
    }

    @FXML
    private void initialize() {
        glucoseCategoryCombo.getItems().addAll(GlucoseCategory.values());
        mealCategoryCombo.getItems().addAll(MealCategory.values());
    }

    @FXML
    private void onCancel() {
        NavigationManager.closeModal();
    }

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

    private void saveGlucose(Patient patient) throws Exception {
        Float value = Float.parseFloat(glucoseField.getText());
        GlucoseCategory category = glucoseCategoryCombo.getValue();
        LocalDateTime dateTime = LocalDateTime.parse(glucoseDateTimeField.getText(), dtf);

        eventService.registerGlucose(patient, value, category, dateTime);
    }

    private void saveMeal(Patient patient) throws Exception {
        String desc = mealDescriptionField.getText();
        MealCategory category = mealCategoryCombo.getValue();

        Float carbs = carbsField.getText().isEmpty() ? null : Float.parseFloat(carbsField.getText());
        Float gi = giField.getText().isEmpty() ? null : Float.parseFloat(giField.getText());

        LocalDateTime mealDateTime = LocalDateTime.parse(mealDateTimeField.getText(), dtf);

        if (glucoseField.getText().isEmpty()) {
            // Refeição sem glicemia
            eventService.registerMealOnly(patient, desc, mealDateTime, category);
        } else {
            // Com glicemia associada
            Float glucose = Float.parseFloat(glucoseField.getText());
            GlucoseCategory gCat = glucoseCategoryCombo.getValue();
            LocalDateTime gDate = LocalDateTime.parse(glucoseDateTimeField.getText(), dtf);

            eventService.registerMealAndGlucose(
                    patient, desc, mealDateTime, category,
                    glucose, gCat, gDate
            );
        }
    }

    private void showError(String msg) {
        System.out.println(msg);
    }
}