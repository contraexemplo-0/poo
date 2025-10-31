package com.project.app.menu;

import com.project.model.Historic;
import com.project.model.Patient;
import com.project.model.RoutineEvent;
import com.project.model.User;
import com.project.service.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public abstract class BaseMenu<T extends User> {
    protected final T user;
    protected final UserService userService;
    protected final Scanner scanner;

    protected BaseMenu(T user, UserService userService, Scanner scanner) {
        this.user = user;
        this.userService = userService;
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
                System.out.println("Valor inválido. Digite um número válido para a glicose.");
            }
        }
    }

    protected void showHistoric(Patient patient, String header, int days) {
        try {
            Historic historic = userService.loadHistoric(patient);
            List<RoutineEvent> events = historic.getAll();

            if (events.isEmpty()) {
                System.out.println("Nenhum registro encontrado.");
                return;
            }

            System.out.println(header);
            LocalDate startDate = days > 0 ? LocalDate.now().minusDays(days) : null;
            boolean printed = false;

            for (RoutineEvent event : events) {
                if (startDate == null || !event.getDate().isBefore(startDate)) {
                    System.out.println(event);
                    printed = true;
                }
            }

            if (!printed) {
                System.out.println("Nenhum registro encontrado para o período informado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar histórico: " + e.getMessage());
        }
    }
}
