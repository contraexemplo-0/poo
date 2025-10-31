package com.project.app.menu;

import com.project.model.HealthProfessional;
import com.project.model.Patient;
import com.project.model.User;
import com.project.service.UserService;

import java.sql.SQLException;
import java.util.Scanner;

public class ProfessionalMenu extends BaseMenu<HealthProfessional> {

    public ProfessionalMenu(HealthProfessional professional, UserService userService, Scanner scanner) {
        super(professional, userService, scanner);
    }

    @Override
    public void startMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- Menu do Profissional de Saúde ---");
            System.out.println("1 - Ver resumo profissional");
            System.out.println("2 - Consultar histórico de um paciente");
            System.out.println("0 - Logout");
            String choice = readLine("Escolha: ");

            switch (choice) {
                case "1" -> showProfessionalSummary();
                case "2" -> showPatientHistoric();
                case "0" -> {
                    loggedIn = false;
                    System.out.println("Logout realizado.");
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void showProfessionalSummary() {
        System.out.println(user.getSummary());
        System.out.println(user.getDashboardSummary());
    }

    private void showPatientHistoric() {
        String patientName = readLine("Digite o nome do paciente: ");

        try {
            User foundUser = userService.findByName(patientName);
            if (foundUser == null) {
                System.out.println("Paciente não encontrado.");
            } else if (foundUser instanceof Patient patient) {
                showHistoric(patient, "Histórico do paciente " + patient.getName() + ":", 7);
            } else {
                System.out.println("O usuário informado não é um paciente.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar paciente: " + e.getMessage());
        }
    }
}
