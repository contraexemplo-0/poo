package com.project.app;

import com.project.model.Patient;

/**
 * Classe utilitária responsável por gerenciar a sessão atual da aplicação.
 * Armazena o paciente autenticado e fornece métodos para consulta e logout.
 */
public class SessionManager {

    /** Paciente atualmente autenticado no sistema. */
    private static Patient currentPatient;

    /**
     * Define o paciente autenticado e inicia a sessão.
     *
     * @param patient paciente autenticado
     */
    public static void setCurrentPatient(Patient patient) {
        currentPatient = patient;
    }

    /**
     * Retorna o paciente atualmente autenticado.
     *
     * @return objeto {@link Patient} da sessão ou {@code null} se ninguém estiver logado
     */
    public static Patient getCurrentPatient() {
        return currentPatient;
    }

    /**
     * Verifica se há um paciente autenticado na sessão.
     *
     * @return {@code true} se houver sessão ativa, {@code false} caso contrário
     */
    public static boolean isLoggedIn() {
        return currentPatient != null;
    }

    /**
     * Finaliza a sessão atual, removendo o paciente autenticado.
     */
    public static void logout() {
        currentPatient = null;
    }
}