package com.project.service;

import com.project.model.Patient;
import com.project.persistence.PatientDAO;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Serviço que concentra as regras de negócio relacionadas ao paciente
 * (único usuário do sistema).
 */
public class UserService {

    private final PatientDAO patientDAO;

    public UserService() throws SQLException {
        this.patientDAO = new PatientDAO();
    }

    // ==========================================================
    // 1. REGISTRO COMPLETO (nova versão)
    // ==========================================================
    public Patient register(
            String name,
            String email,
            String password,
            LocalDate birthDate,
            String gender,
            String diabetesType,
            LocalDate diagnosisDate,
            Float carbSensitivity
    ) {

        Patient p = new Patient(
                name,
                email,
                password,
                birthDate,
                gender,
                diabetesType,
                diagnosisDate,
                carbSensitivity
        );

        return patientDAO.insert(p);
    }

    // ==========================================================
    // 2. REGISTRO SIMPLIFICADO (LEGADO - usado no console)
    // ==========================================================
    public Patient register(String name, String password) {
        Patient p = new Patient(name, null, password, null, null, null, null, null);
        return patientDAO.insert(p);
    }

    // ==========================================================
    // 3. LOGIN POR EMAIL (nova versão correta)
    // ==========================================================
    public Patient login(String email, String password) {
        Patient p = patientDAO.findByEmail(email);

        if (p != null && p.checkPassword(password)) {
            return p;
        }
        return null;
    }

    // ==========================================================
    // 4. BUSCA POR ID
    // ==========================================================
    public Patient findById(int id) {
        return patientDAO.findById(id);
    }

    // ==========================================================
    // 5. BUSCA POR EMAIL
    // ==========================================================
    public Patient findByEmail(String email) {
        return patientDAO.findByEmail(email);
    }
}