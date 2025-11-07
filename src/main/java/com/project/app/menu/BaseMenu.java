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

/**
 * Menu base que centraliza funcionalidades comuns para diferentes tipos de
 * usuários, como leitura de entradas e exibição de históricos.
 *
 * @param <T> tipo específico de usuário atendido pelo menu.
 */
public abstract class BaseMenu<T extends User> {
    protected final T user;
    protected final UserService userService;
    protected final Scanner scanner;

    /**
     * Cria uma nova instância do menu parametrizado para um usuário específico.
     *
     * @param user        usuário autenticado.
     * @param userService camada de serviço utilizada para carregar dados.
     * @param scanner     fonte de entrada utilizada para interações.
     */
    protected BaseMenu(T user, UserService userService, Scanner scanner) {
        this.user = user;
        this.userService = userService;
        this.scanner = scanner;
    }

    /**
     * Inicia o fluxo do menu especializado.
     */
    public abstract void startMenu();

    /**
     * Lê uma linha de entrada exibindo um texto para o usuário.
     *
     * @param prompt texto exibido antes da leitura.
     * @return conteúdo digitado pelo usuário.
     */
    protected String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Lê um valor inteiro garantindo a validação de entrada.
     *
     * @param prompt texto exibido antes da leitura.
     * @return número inteiro informado pelo usuário.
     */
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

    /**
     * Lê um valor {@code float} aceitando vírgula ou ponto na entrada.
     *
     * @param prompt texto exibido antes da leitura.
     * @return valor numérico validado.
     */
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

    /**
     * Exibe o histórico de eventos de um paciente filtrando por período.
     *
     * @param patient paciente cujo histórico será consultado.
     * @param header  cabeçalho exibido antes da listagem.
     * @param days    quantidade de dias a considerar; quando menor ou igual a zero não filtra.
     */
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
