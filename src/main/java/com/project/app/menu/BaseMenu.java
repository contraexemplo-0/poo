package com.project.app.menu;

import com.project.model.Patient;
import com.project.model.RoutineEvent;
import com.project.model.User;
import com.project.service.RoutineEventService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Menu base que centraliza funcionalidades comuns para diferentes tipos de
 * usuários, como leitura de entradas e exibição de históricos.
 *
 * @param <T> tipo específico de usuário atendido pelo menu.
 */
public abstract class BaseMenu<T extends User> {
    protected final T user;
    protected final RoutineEventService routineEventService;
    protected final Scanner scanner;

    protected BaseMenu(T user, RoutineEventService routineEventService, Scanner scanner) {
        this.user = user;
        this.routineEventService = routineEventService;
        this.scanner = scanner;
    }

    public abstract void startMenu();

    protected String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    protected int readInt(String prompt) {
        while (true) {
            try {
                String input = readLine(prompt);
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    protected float readFloat(String prompt) {
        while (true) {
            try {
                String input = readLine(prompt).replace(',', '.');
                return Float.parseFloat(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número válido.");
            }
        }
    }

    protected void showHistoric(Patient patient, String header, int days) {
        try {
            List<RoutineEvent> events = routineEventService.loadEvents(patient, days);

            if (events.isEmpty()) {
                System.out.println("Nenhum registro encontrado.");
                return;
            }

            System.out.println(header);
            for (RoutineEvent event : events) {
                System.out.println(formatEvent(event));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar histórico: " + e.getMessage());
        }
    }

    protected String formatEvent(RoutineEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Evento #").append(event.getId());
        if (event.getMealDateTime() != null) {
            sb.append(" | Refeição: ").append(event.getMealDateTime());
            if (event.getMealCategory() != null) {
                sb.append(" (").append(event.getMealCategory()).append(")");
            }
            if (event.getMealDescription() != null) {
                sb.append(" - ").append(event.getMealDescription());
            }
            if (event.getCarbs() != null || event.getGi() != null) {
                sb.append(" [carbs: ").append(event.getCarbs()).append(", GI: ").append(event.getGi()).append("]");
            }
        }
        if (event.getGlucoseDateTime() != null) {
            sb.append(" | Glicemia: ").append(event.getGlucoseLevel()).append(" mg/dL em ").append(event.getGlucoseDateTime());
            if (event.getGlucoseCategory() != null) {
                sb.append(" (").append(event.getGlucoseCategory()).append(")");
            }
        }
        if (event.getWeight() != null) {
            sb.append(" | Peso: ").append(event.getWeight()).append(" kg");
        }
        if (event.getActivityMinutes() != null) {
            sb.append(" | Atividade: ").append(event.getActivityMinutes()).append(" min");
        }
        return sb.toString();
    }
}
