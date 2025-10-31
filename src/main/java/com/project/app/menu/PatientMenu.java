package com.project.app.menu;

import com.project.model.GlucoseMeasure;
import com.project.model.Patient;
import com.project.model.RoutineEvent;
import com.project.service.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PatientMenu extends BaseMenu<Patient> {

    public PatientMenu(Patient patient, UserService userService, Scanner scanner) {
        super(patient, userService, scanner);
    }

    @Override
    public void startMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- Menu do Paciente ---");
            System.out.println("1 - Registrar medida de glicose");
            System.out.println("2 - Remover medida de glicose");
            System.out.println("3 - Consultar histórico (últimos 7 dias)");
            System.out.println("0 - Logout");
            String choice = readLine("Escolha: ");

            switch (choice) {
                case "1" -> addMeasure();
                case "2" -> removeMeasure();
                case "3" -> showRecentHistoric();
                case "0" -> {
                    loggedIn = false;
                    System.out.println("Logout realizado.");
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void addMeasure() {
        float level = readFloat("Digite o valor da glicose: ");
        String note = readLine("Observação (opcional): ");

        try {
            GlucoseMeasure measure = note.isBlank()
                    ? userService.addMeasure(user, level)
                    : userService.addMeasure(user, level, note);
            System.out.println("Medida registrada: " + measure);
        } catch (SQLException e) {
            System.out.println("Erro ao salvar medida: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void removeMeasure() {
        try {
            List<RoutineEvent> events = userService.loadHistoric(user).getAll();
            if (events.isEmpty()) {
                System.out.println("Nenhuma medida registrada.");
                return;
            }

            for (int i = 0; i < events.size(); i++) {
                System.out.println((i + 1) + " - " + events.get(i));
            }

            int choice = readInt("Digite o número da medida que deseja remover: ");
            if (choice < 1 || choice > events.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            RoutineEvent toDelete = events.get(choice - 1);
            userService.removeMeasure(user, toDelete.getId());
            System.out.println("Medida removida com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao remover medida: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showRecentHistoric() {
        showHistoric(user, "Histórico dos últimos 7 dias:", 7);
    }
}
