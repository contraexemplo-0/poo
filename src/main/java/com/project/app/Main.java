package com.project.app;

import com.project.persistence.DatabaseManager;
import com.project.service.UserService;

import java.sql.SQLException;

/**
 * Classe de inicialização da aplicação em modo console.
 */
public class Main {
    /**
     * Ponto de entrada responsável por configurar dependências e iniciar a UI.
     *
     * @param args argumentos de linha de comando (não utilizados).
     */
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