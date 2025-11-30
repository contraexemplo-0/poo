package com.project.app.menu;

import com.project.model.GlucoseCategory;
import com.project.model.MealCategory;
import com.project.model.Patient;
import com.project.model.RoutineEvent;
import com.project.service.RoutineEventService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Menu dedicado aos pacientes, responsável por registrar e consultar eventos de rotina.
 */
public class PatientMenu extends BaseMenu<Patient> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PatientMenu(Patient patient, RoutineEventService routineEventService, Scanner scanner) {
        super(patient, routineEventService, scanner);
    }

    @Override
    public void startMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- Menu do Paciente ---");
            System.out.println("1 - Registrar glicemia");
            System.out.println("2 - Registrar refeição + glicemia");
            System.out.println("3 - Registrar apenas refeição");
            System.out.println("4 - Ver histórico (últimos 7 dias)");
            System.out.println("0 - Logout");
            String choice = readLine("Escolha: ");

            switch (choice) {
                case "1" -> addGlucose();
                case "2" -> addMealAndGlucose();
                case "3" -> addMealOnly();
                case "4" -> showRecentHistoric();
                case "0" -> {
                    loggedIn = false;
                    System.out.println("Logout realizado.");
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void addGlucose() {
        float level = readFloat("Digite o valor da glicose: ");
        GlucoseCategory category = readGlucoseCategory();
        LocalDateTime dateTime = readDateTime("Data e hora da glicemia (yyyy-MM-dd HH:mm): ");

        try {
            RoutineEvent event = routineEventService.registerGlucose(user, level, category, dateTime);
            System.out.println("Glicemia registrada: " + formatEvent(event));
        } catch (SQLException e) {
            System.out.println("Erro ao salvar glicemia: " + e.getMessage());
        }
    }

    private void addMealAndGlucose() {
        String description = readLine("Descreva a refeição: ");
        MealCategory mealCategory = readMealCategory();
        LocalDateTime mealDateTime = readDateTime("Data e hora da refeição (yyyy-MM-dd HH:mm): ");
        float glucoseLevel = readFloat("Valor da glicemia: ");
        GlucoseCategory glucoseCategory = readGlucoseCategory();
        LocalDateTime glucoseDateTime = readDateTime("Data e hora da glicemia (yyyy-MM-dd HH:mm): ");

        try {
            RoutineEvent event = routineEventService.registerMealAndGlucose(user, description, mealDateTime, mealCategory, glucoseLevel, glucoseCategory, glucoseDateTime);
            System.out.println("Evento registrado: " + formatEvent(event));
        } catch (SQLException e) {
            System.out.println("Erro ao salvar evento: " + e.getMessage());
        }
    }

    private void addMealOnly() {
        String description = readLine("Descreva a refeição: ");
        MealCategory mealCategory = readMealCategory();
        LocalDateTime mealDateTime = readDateTime("Data e hora da refeição (yyyy-MM-dd HH:mm): ");

        try {
            RoutineEvent event = routineEventService.registerMealOnly(user, description, mealDateTime, mealCategory);
            System.out.println("Refeição registrada: " + formatEvent(event));
        } catch (SQLException e) {
            System.out.println("Erro ao salvar refeição: " + e.getMessage());
        }
    }

    private void showRecentHistoric() {
        try {
            List<RoutineEvent> events = routineEventService.loadEvents(user, 7);
            if (events.isEmpty()) {
                System.out.println("Nenhum registro encontrado.");
                return;
            }
            System.out.println("Histórico dos últimos 7 dias:");
            for (RoutineEvent event : events) {
                System.out.println(formatEvent(event));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar histórico: " + e.getMessage());
        }
    }

    private LocalDateTime readDateTime(String prompt) {
        while (true) {
            try {
                String input = readLine(prompt);
                return LocalDateTime.parse(input, DATE_TIME_FORMATTER);
            } catch (Exception e) {
                System.out.println("Formato inválido. Utilize yyyy-MM-dd HH:mm.");
            }
        }
    }

    private GlucoseCategory readGlucoseCategory() {
        System.out.println("Selecione a categoria de glicemia:");
        for (GlucoseCategory category : GlucoseCategory.values()) {
            System.out.println("- " + category.name());
        }
        while (true) {
            String input = readLine("Categoria: ").toUpperCase();
            try {
                return GlucoseCategory.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Categoria inválida. Tente novamente.");
            }
        }
    }

    private MealCategory readMealCategory() {
        System.out.println("Selecione a categoria da refeição:");
        for (MealCategory category : MealCategory.values()) {
            System.out.println("- " + category.name());
        }
        while (true) {
            String input = readLine("Categoria: ").toUpperCase();
            try {
                return MealCategory.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Categoria inválida. Tente novamente.");
            }
        }
    }
}
