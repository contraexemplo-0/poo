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

/**
 * Controller responsável pela tela de dashboard principal.
 * Exibe gráfico de glicose, estatísticas resumidas e lista de eventos recentes.
 */
public class DashboardController {

    /** Gráfico de linha exibindo valores de glicemia. */
    @FXML private LineChart<String, Number> glucoseChart;

    /** Label exibindo a HbA1c estimada. */
    @FXML private Label hba1cLabel;
    /** Label exibindo a média glicêmica dos últimos registros. */
    @FXML private Label meanLabel;
    /** Label exibindo quantidade de episódios de hiperglicemia. */
    @FXML private Label hyperLabel;
    /** Label exibindo quantidade de episódios de hipoglicemia. */
    @FXML private Label hypoLabel;

    /** Lista textual contendo os eventos recentes. */
    @FXML private ListView<String> eventsList;

    /** Serviço responsável por carregar eventos do paciente. */
    private RoutineEventService eventService;

    /** Serviço responsável por calcular estatísticas de glicose. */
    private GlucoseStatsService statsService;

    /** Formatador de data para exibição amigável. */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    /**
     * Construtor padrão do controller.
     * Inicializa serviços necessários ao funcionamento do dashboard.
     */
    public DashboardController() {
        try {
            this.eventService = new RoutineEventService(null);
            this.statsService = new GlucoseStatsService();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar controller", e);
        }
    }

    /**
     * Método automaticamente chamado pelo JavaFX após a carga do FXML.
     * Responsável por iniciar o carregamento dos dados do dashboard.
     */
    @FXML
    private void initialize() {
        loadDashboard();
    }

    /**
     * Carrega todos os dados necessários para preencher o dashboard:
     * gráfico, resumo estatístico e lista de eventos.
     */
    private void loadDashboard() {
        Patient patient = SessionManager.getCurrentPatient();

        if (patient == null) return;

        List<RoutineEvent> events = eventService.loadEvents(patient, 7);

        updateChart(events);
        updateSummary(events);
        updateList(events);
    }

    /**
     * Atualiza o gráfico de glicose exibindo os registros ordenados por data.
     *
     * @param events lista de eventos do paciente contendo medições de glicemia.
     */
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

    /**
     * Atualiza os indicadores numéricos do dashboard (média, HbA1c, hipo e hiper).
     *
     * @param events eventos utilizados para cálculo estatístico.
     */
    private void updateSummary(List<RoutineEvent> events) {
        var summary = statsService.computeSummary(events);

        hba1cLabel.setText(String.format("%.2f %%", summary.getEstimatedHbA1c()));
        meanLabel.setText(String.format("%.1f mg/dL", summary.getMean()));
        hyperLabel.setText(String.valueOf(summary.getHyperCount()));
        hypoLabel.setText(String.valueOf(summary.getHypoCount()));
    }

    /**
     * Atualiza a lista textual de eventos exibida na interface.
     *
     * @param events lista de eventos recentes.
     */
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

    /**
     * Abre o modal de criação de novo evento.
     * Após o modal ser fechado, o dashboard é recarregado.
     */
    @FXML
    private void onNewEvent() {
        NavigationManager.openModal("/com/project/view/new_event_dialog.fxml", "Novo Registro");

        loadDashboard();
    }
}