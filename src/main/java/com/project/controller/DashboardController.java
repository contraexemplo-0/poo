package com.project.controller;

import com.project.app.NavigationManager;
import com.project.app.SessionManager;
import com.project.model.Patient;
import com.project.model.RoutineEvent;
import com.project.service.GlucoseStatsService;
import com.project.service.RoutineEventService;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML private LineChart<String, Number> glucoseChart;

    @FXML private Label hba1cLabel;
    @FXML private Label meanLabel;
    @FXML private Label hyperLabel;
    @FXML private Label hypoLabel;

    @FXML private ListView<String> eventsList;

    private RoutineEventService eventService;
    private GlucoseStatsService statsService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    public DashboardController() {
        try {
            this.eventService = new RoutineEventService(null);
            this.statsService = new GlucoseStatsService();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar controller", e);
        }
    }

    @FXML
    private void initialize() {
        loadDashboard();
    }

    private void loadDashboard() {
        Patient patient = SessionManager.getCurrentPatient();

        if (patient == null) return;

        List<RoutineEvent> events = eventService.loadEvents(patient, 7);

        updateChart(events);
        updateSummary(events);
        updateList(events);
    }

    private void updateChart(List<RoutineEvent> events) {
        glucoseChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Glicose");

        events.stream()
                .filter(e -> e.getGlucoseLevel() != null && e.getGlucoseDateTime() != null)
                .sorted((a, b) -> a.getGlucoseDateTime().compareTo(b.getGlucoseDateTime()))
                .forEach(e -> {
                    String x = e.getGlucoseDateTime().format(formatter);
                    series.getData().add(new XYChart.Data<>(x, e.getGlucoseLevel()));
                });

        glucoseChart.getData().add(series);
    }

    private void updateSummary(List<RoutineEvent> events) {
        var summary = statsService.computeSummary(events);

        hba1cLabel.setText(String.format("%.2f %%", summary.getEstimatedHbA1c()));
        meanLabel.setText(String.format("%.1f mg/dL", summary.getMean()));
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
                sb.append(" | [Refeição] ")
                        .append(e.getMealDescription())
                        .append(" • ")
                        .append(e.getMealDateTime() != null
                                ? e.getMealDateTime().format(formatter)
                                : "sem horário");
            }

            eventsList.getItems().add(sb.toString());
        }
    }

    @FXML
    private void onNewEvent() {
        NavigationManager.openModal("/com/project/view/new_event_dialog.fxml", "Novo Registro");

        // Recarregar o dashboard após fechar o modal
        loadDashboard();
    }
}
