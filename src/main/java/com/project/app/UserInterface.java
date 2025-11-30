package com.project.app;

import com.project.app.menu.BaseMenu;
import com.project.app.menu.PatientMenu;
import com.project.model.Patient;
import com.project.model.User;
import com.project.service.RoutineEventService;
import com.project.service.UserService;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Camada responsável por orquestrar a interação via terminal com o usuário
 * final, conectando comandos com a lógica de negócios.
 */
public class UserInterface {
    private final UserService userService;
    private final RoutineEventService routineEventService;
    private final Scanner sc = new Scanner(System.in);

    public UserInterface(UserService userService, RoutineEventService routineEventService) {
        this.userService = userService;
        this.routineEventService = routineEventService;
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
            User user = userService.register(name, password);
            System.out.println("Usuário registrado com sucesso! ID: " + user.getId());
            System.out.println(user.getSummary());
        } catch (SQLException e) {
            System.out.println("Erro: nome já existe ou problema no banco.");
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
            menu = new PatientMenu(patient, routineEventService, sc);
        }

        if (menu == null) {
            System.out.println("Tipo de usuário desconhecido. Encerrando sessão.");
            return;
        }

        menu.startMenu();
    }
}
