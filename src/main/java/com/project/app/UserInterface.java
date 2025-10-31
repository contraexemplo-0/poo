package com.project.app;

import com.project.model.GlucoseMeasure;
import com.project.model.HealthProfessional;
import com.project.model.Historic;
import com.project.model.Patient;
import com.project.model.RoutineEvent;
import com.project.model.User;
import com.project.model.UserType;
import com.project.service.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class UserInterface {
    private final UserService userService;
    private final Scanner sc = new Scanner(System.in);

    public UserInterface(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        boolean running = true;
        User currentUser = null;

        while (running) {
            System.out.println("\n=== Sistema de Glicose ===");
            System.out.println("1 - Registrar novo usuário");
            System.out.println("2 - Login");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            switch (sc.nextLine()) {
                case "1" -> registerUser();
                case "2" -> currentUser = login();
                case "0" -> running = false;
                default -> System.out.println("Opção inválida.");
            }
        }

        System.out.println("Sistema encerrado.");
    }

    private void registerUser() {
        System.out.print("Digite seu nome: ");
        String name = sc.nextLine();
        System.out.print("Digite sua senha: ");
        String password = sc.nextLine();

        try {
            UserType type = askUserType();
            User user = userService.register(name, password, type);
            System.out.println("Usuário registrado com sucesso! ID: " + user.getId());
            System.out.println(user.getSummary());
        } catch (SQLException e) {
            System.out.println("Erro: nome já existe ou problema no banco.");
        }
    }

    private UserType askUserType() {
        while (true) {
            System.out.println("Selecione o tipo de usuário:");
            System.out.println("1 - Paciente");
            System.out.println("2 - Profissional de Saúde");
            System.out.print("Escolha: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    return UserType.PATIENT;
                case "2":
                    return UserType.HEALTH_PROFESSIONAL;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private User login() {
        System.out.print("Digite seu nome: ");
        String loginName = sc.nextLine();
        System.out.print("Digite sua senha: ");
        String loginPassword = sc.nextLine();

        try {
            User user = userService.login(loginName, loginPassword);
            if (user != null) {
                System.out.println("Login realizado com sucesso!");
                System.out.println(user.getSummary());
                userMenu(user);
            } else {
                System.out.println("Login falhou. Nome ou senha incorretos.");
            }
            return user;
        } catch (Exception e) {
            System.out.println("Error ao tentar login: " + e.getMessage());
            return null;
        }
    }

    private void userMenu(User currentUser) {
        if (currentUser instanceof Patient patient) {
            patientMenu(patient);
        } else if (currentUser instanceof HealthProfessional professional) {
            professionalMenu(professional);
        } else {
            System.out.println("Tipo de usuário desconhecido. Encerrando sessão.");
        }
    }

    private void patientMenu(Patient patient) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- Menu do Paciente ---");
            System.out.println("1 - Registrar medida de glicose");
            System.out.println("2 - Remover medida de glicose");
            System.out.println("3 - Consultar histórico (últimos 7 dias)");
            System.out.println("0 - Logout");
            System.out.print("Escolha: ");

            switch (sc.nextLine()) {
                case "1" -> addMeasure(patient);
                case "2" -> removeMeasure(patient);
                case "3" -> showHistoric(patient);
                case "0" -> {
                    loggedIn = false;
                    System.out.println("Logout realizado.");
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void professionalMenu(HealthProfessional professional) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- Menu do Profissional de Saúde ---");
            System.out.println("1 - Ver resumo profissional");
            System.out.println("2 - Consultar histórico de um paciente");
            System.out.println("0 - Logout");
            System.out.print("Escolha: ");

            switch (sc.nextLine()) {
                case "1" -> System.out.println(professional.getSummary());
                case "2" -> showPatientHistoric();
                case "0" -> {
                    loggedIn = false;
                    System.out.println("Logout realizado.");
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void addMeasure(Patient currentUser) {
        System.out.print("Digite o valor da glicose: ");
        float level = Float.parseFloat(sc.nextLine());
        System.out.print("Observação (opcional): ");
        String note = sc.nextLine();

        try {
            GlucoseMeasure m;

            if (note.isBlank()) {
                // chama a sobrecarga sem nota
                m = userService.addMeasure(currentUser, level);
            } else {
                // chama a versão com nota
                m = userService.addMeasure(currentUser, level, note);
            }

            System.out.println("Medida registrada: " + m);
        } catch (SQLException e) {
            System.out.println("Erro ao salvar medida: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void removeMeasure(Patient currentUser) {
        try {
            Historic historic = userService.loadHistoric(currentUser);
            if (historic.getAll().isEmpty()) {
                System.out.println("Nenhuma medida registrada.");
                return;
            }

            int index = 1;
            for (RoutineEvent event : historic.getAll()) {
                System.out.println(index + " - " + event);
                index++;
            }

            System.out.print("Digite o número da medida que deseja remover: ");
            int choice = Integer.parseInt(sc.nextLine());

            if (choice < 1 || choice > historic.getAll().size()) {
                System.out.println("Opção inválida.");
                return;
            }

            RoutineEvent toDelete = historic.getAll().get(choice - 1);

            userService.removeMeasure(currentUser, toDelete.getId());
            System.out.println("Medida removida com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao remover medida: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showHistoric(Patient patient) {
        showHistoric(patient, "Histórico dos últimos 7 dias:");
    }

    private void showHistoric(Patient patient, String header) {
        try {
            System.out.println(header);
            Historic historic = userService.loadHistoric(patient);
            LocalDate start = LocalDate.now().minusDays(7);

            for (RoutineEvent event : historic.getAll()) {
                if (!event.getDate().isBefore(start)) {
                    System.out.println(event);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar histórico: " + e.getMessage());
        }
    }

    private void showPatientHistoric() {
        System.out.print("Digite o nome do paciente: ");
        String patientName = sc.nextLine();

        try {
            User user = userService.findByName(patientName);
            if (user == null) {
                System.out.println("Paciente não encontrado.");
            } else if (user instanceof Patient patient) {
                showHistoric(patient, "Histórico do paciente " + patient.getName() + ":");
            } else {
                System.out.println("O usuário informado não é um paciente.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar paciente: " + e.getMessage());
        }
    }
}
