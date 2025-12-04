package com.project.controller;

import com.project.app.SessionManager;
import com.project.model.Patient;
import com.project.model.RoutineEvent;
import com.project.service.GlucoseStatsService;
import com.project.service.RoutineEventService;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsController {

    @FXML private ComboBox<String> periodCombo;

    @FXML private Label meanLabel;
    @FXML private Label maxLabel;
    @FXML private Label minLabel;
    @FXML private Label hyperLabel;
    @FXML private Label hypoLabel;

    @FXML private ListView<String> eventsList;

    private RoutineEventService eventService;
    private GlucoseStatsService statsService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    public ReportsController() {
        try {
            this.eventService = new RoutineEventService(null);
            this.statsService = new GlucoseStatsService();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar controlador de relatórios", e);
        }
    }

    @FXML
    private void initialize() {
        periodCombo.getItems().addAll("7 dias", "30 dias", "90 dias");
        periodCombo.setValue("7 dias");

        periodCombo.setOnAction(e -> loadReport());

        loadReport();
    }

    private void loadReport() {
        Patient patient = SessionManager.getCurrentPatient();
        if (patient == null) return;

        int days = switch (periodCombo.getValue()) {
            case "30 dias" -> 30;
            case "90 dias" -> 90;
            default -> 7;
        };

        List<RoutineEvent> events = eventService.loadEvents(patient, days);

        updateSummary(events);
        updateList(events);
    }

    private void updateSummary(List<RoutineEvent> events) {
        var summary = statsService.computeSummary(events);

        meanLabel.setText(String.format("%.1f mg/dL", summary.getMean()));
        maxLabel.setText(String.format("%.1f mg/dL", summary.getMax()));
        minLabel.setText(String.format("%.1f mg/dL", summary.getMin()));
        hyperLabel.setText(String.valueOf(summary.getHyperCount()));
        hypoLabel.setText(String.valueOf(summary.getHypoCount()));
    }

    private void updateList(List<RoutineEvent> events) {
        eventsList.getItems().clear();

        for (RoutineEvent e : events) {
            StringBuilder sb = new StringBuilder();

            if (e.getGlucoseDateTime() != null) {
                sb.append("[Glicemia] ")
                        .append(e.getGlucoseLevel())
                        .append(" mg/dL • ")
                        .append(e.getGlucoseDateTime().format(formatter));
            }

            if (e.getMealDescription() != null) {
                sb.append("\n[Refeição] ").append(e.getMealDescription());
            }

            eventsList.getItems().add(sb.toString());
        }
    }
}