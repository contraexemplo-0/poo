package com.project.app;

import com.project.model.User;
import com.project.model.Historic;
import com.project.model.GlucoseMeasure;
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
            User user = userService.register(name, password);
            System.out.println("Usuário registrado com sucesso! ID: " + user.getId());
        } catch (SQLException e) {
            System.out.println("Erro: nome já existe ou problema no banco.");
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
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- Menu do Usuário ---");
            System.out.println("1 - Registrar medida de glicose");
            System.out.println("2 - Remover medida de glicose");
            System.out.println("3 - Consultar histórico (últimos 7 dias)");
            System.out.println("0 - Logout");
            System.out.print("Escolha: ");

            switch (sc.nextLine()) {
                case "1" -> addMeasure(currentUser);
                case "2" -> removeMeasure(currentUser);
                case "3" -> showHistoric(currentUser);
                case "0" -> {
                    loggedIn = false;
                    System.out.println("Logout realizado.");
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void addMeasure(User currentUser) {
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
        }
    }

    private void removeMeasure(User currentUser) {
        try {
            Historic historic = userService.loadHistoric(currentUser);
            if (historic.getAll().isEmpty()) {
                System.out.println("Nenhuma medida registrada.");
                return;
            }

            int index = 1;
            for (GlucoseMeasure m : historic.getAll()) {
                System.out.println(index + " - " + m);
                index++;
            }

            System.out.print("Digite o número da medida que deseja remover: ");
            int choice = Integer.parseInt(sc.nextLine());

            if (choice < 1 || choice > historic.getAll().size()) {
                System.out.println("Opção inválida.");
                return;
            }

            GlucoseMeasure toDelete = historic.getAll().get(choice - 1);

            userService.removeMeasure(currentUser, toDelete.getId());
            System.out.println("Medida removida com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao remover medida: " + e.getMessage());
        }
    }


    private void showHistoric(User currentUser) {
        try {
            System.out.println("Histórico dos últimos 7 dias:");
            Historic historic = userService.loadHistoric(currentUser);
            LocalDate start = LocalDate.now().minusDays(7);

            for (GlucoseMeasure m : historic.getAll()) {
                if (!m.getDate().isBefore(start)) {
                    System.out.println(m);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar histórico: " + e.getMessage());
        }
    }
}
