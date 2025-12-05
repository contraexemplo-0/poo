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

/**
 * Controller responsável pela tela de Relatórios.
 * Permite visualizar estatísticas de glicemia e uma lista de registros
 * filtrados por períodos predefinidos (7, 30 ou 90 dias).
 */
public class ReportsController {

    /** ComboBox para seleção do período de análise (7, 30 ou 90 dias). */
    @FXML private ComboBox<String> periodCombo;

    /** Label exibindo o valor médio de glicemia no período. */
    @FXML private Label meanLabel;

    /** Label exibindo o maior valor de glicemia registrado no período. */
    @FXML private Label maxLabel;

    /** Label exibindo o menor valor de glicemia registrado no período. */
    @FXML private Label minLabel;

    /** Label exibindo a quantidade de episódios de hiperglicemia. */
    @FXML private Label hyperLabel;

    /** Label exibindo a quantidade de episódios de hipoglicemia. */
    @FXML private Label hypoLabel;

    /** Lista contendo os eventos exibidos de forma resumida. */
    @FXML private ListView<String> eventsList;

    /** Serviço responsável por carregar eventos do banco de dados. */
    private RoutineEventService eventService;

    /** Serviço de cálculo estatístico das glicemias. */
    private GlucoseStatsService statsService;

    /** Formatador padrão para datas exibidas nos relatórios. */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    /**
     * Construtor padrão.
     * Inicializa os serviços necessários.
     */
    public ReportsController() {
        try {
            this.eventService = new RoutineEventService(null);
            this.statsService = new GlucoseStatsService();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar controlador de relatórios", e);
        }
    }

    /**
     * Método chamado automaticamente após carregar o FXML.
     * Inicializa o ComboBox de períodos e carrega o relatório inicial.
     */
    @FXML
    private void initialize() {
        periodCombo.getItems().addAll("7 dias", "30 dias", "90 dias");
        periodCombo.setValue("7 dias");

        periodCombo.setOnAction(e -> loadReport());

        loadReport();
    }

    /**
     * Carrega o relatório de acordo com o período selecionado.
     * Obtém o paciente logado, filtra os registros por período e atualiza a interface.
     */
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

    /**
     * Atualiza os valores estatísticos exibidos na tela:
     * média, máximo, mínimo, hiper e hipo.
     *
     * @param events lista de eventos usados no cálculo
     */
    private void updateSummary(List<RoutineEvent> events) {
        var summary = statsService.computeSummary(events);

        meanLabel.setText(String.format("%.1f mg/dL", summary.getMean()));
        maxLabel.setText(String.format("%.1f mg/dL", summary.getMax()));
        minLabel.setText(String.format("%.1f mg/dL", summary.getMin()));
        hyperLabel.setText(String.valueOf(summary.getHyperCount()));
        hypoLabel.setText(String.valueOf(summary.getHypoCount()));
    }

    /**
     * Atualiza a lista textual exibindo os eventos filtrados.
     * Cada entrada pode possuir dados de glicemia e/ou refeição.
     *
     * @param events eventos a serem listados no relatório
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
                sb.append("\n[Refeição] ").append(e.getMealDescription());
            }

            eventsList.getItems().add(sb.toString());
        }
    }
}