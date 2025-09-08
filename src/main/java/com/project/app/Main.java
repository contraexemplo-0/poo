package com.project.app;

import com.project.persistence.DatabaseManager;
import com.project.service.UserService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (DatabaseManager db = new DatabaseManager()) {
            UserService userService = new UserService(db);
            UserInterface ui = new UserInterface(userService);

            ui.start(); // roda a aplicação (menus e interações)

        } catch (SQLException e) {
            System.out.println("Erro ao iniciar sistema: " + e.getMessage());
        }
    }
}