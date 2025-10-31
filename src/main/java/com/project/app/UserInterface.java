package com.project.app;

import com.project.app.menu.BaseMenu;
import com.project.app.menu.PatientMenu;
import com.project.app.menu.ProfessionalMenu;
import com.project.model.HealthProfessional;
import com.project.model.Patient;
import com.project.model.User;
import com.project.model.UserType;
import com.project.service.UserService;

import java.sql.SQLException;
import java.util.Scanner;

public class UserInterface {
    private final UserService userService;
    private final Scanner sc = new Scanner(System.in);

    public UserInterface(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Sistema de Glicose ===");
            System.out.println("1 - Registrar novo usuário");
            System.out.println("2 - Login");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            switch (sc.nextLine()) {
                case "1" -> registerUser();
                case "2" -> login();
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

    private void login() {
        System.out.print("Digite seu nome: ");
        String loginName = sc.nextLine();
        System.out.print("Digite sua senha: ");
        String loginPassword = sc.nextLine();

        try {
            User user = userService.login(loginName, loginPassword);
            if (user != null) {
                System.out.println("Login realizado com sucesso!");
                System.out.println(user.getSummary());
                System.out.println(user.getDashboardSummary());
                startUserMenu(user);
            } else {
                System.out.println("Login falhou. Nome ou senha incorretos.");
            }
        } catch (Exception e) {
            System.out.println("Error ao tentar login: " + e.getMessage());
        }
    }

    private void startUserMenu(User currentUser) {
        BaseMenu<?> menu = null;
        if (currentUser instanceof Patient patient) {
            menu = new PatientMenu(patient, userService, sc);
        } else if (currentUser instanceof HealthProfessional professional) {
            menu = new ProfessionalMenu(professional, userService, sc);
        }

        if (menu == null) {
            System.out.println("Tipo de usuário desconhecido. Encerrando sessão.");
            return;
        }

        menu.startMenu();
    }
}
