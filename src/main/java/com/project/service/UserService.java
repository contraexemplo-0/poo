package com.project.service;

import com.project.model.Patient;
import com.project.persistence.PatientDAO;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Serviço responsável pelas regras de negócio relacionadas ao paciente,
 * que é o único usuário do sistema.
 *
 * <p>Centraliza operações como registro, login e consulta de pacientes.
 * A persistência é delegada ao {@link PatientDAO}.</p>
 *
 * <p>Este serviço não possui dependência de UI e é utilizado tanto no modo
 * console quanto na interface JavaFX.</p>
 */
public class UserService {

    /** DAO responsável pela persistência de pacientes. */
    private final PatientDAO patientDAO;

    /**
     * Cria o serviço e inicializa o {@link PatientDAO}.
     *
     * @throws SQLException se ocorrer erro ao acessar o banco de dados
     */
    public UserService() throws SQLException {
        this.patientDAO = new PatientDAO();
    }

    // ==========================================================
    // 1. REGISTRO COMPLETO (nova versão)
    // ==========================================================

    /**
     * Registra um novo paciente com todas as informações clínicas e pessoais
     * necessárias para o sistema.
     *
     * <p>Esse é o método recomendado para uso na interface JavaFX.</p>
     *
     * @param name nome do paciente
     * @param email email utilizado para login
     * @param password senha em texto simples (MVP)
     * @param birthDate data de nascimento
     * @param gender gênero declarado
     * @param diabetesType tipo de diabetes (ex.: "Tipo 1", "Tipo 2")
     * @param diagnosisDate data do diagnóstico clínico
     * @param carbSensitivity sensibilidade individual a carboidratos
     * @return o paciente recém-registrado com ID preenchido
     */
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

    /**
     * Registra um paciente no modo reduzido, utilizado pela interface console.
     *
     * <p>Esse método é mantido por compatibilidade com versões anteriores do sistema
     * e deve ser evitado no uso moderno (preferir o registro completo).</p>
     *
     * @param name nome do paciente
     * @param password senha em texto simples
     * @return paciente criado e persistido
     */
    public Patient register(String name, String password) {
        Patient p = new Patient(name, null, password, null, null, null, null, null);
        return patientDAO.insert(p);
    }

    // ==========================================================
    // 3. LOGIN POR EMAIL (nova versão correta)
    // ==========================================================

    /**
     * Realiza login verificando o email e a senha do paciente.
     *
     * <p>A autenticação consiste em:</p>
     * <ol>
     *     <li>buscar paciente pelo email;</li>
     *     <li>verificar se a senha informada corresponde à senha armazenada.</li>
     * </ol>
     *
     * <p>Se qualquer etapa falhar, retorna {@code null}.</p>
     *
     * @param email email do paciente
     * @param password senha fornecida no login
     * @return paciente autenticado ou {@code null} se inválido
     */
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

    /**
     * Busca um paciente pelo seu identificador único.
     *
     * @param id ID do paciente
     * @return instância de {@link Patient} ou {@code null} se não encontrado
     */
    public Patient findById(int id) {
        return patientDAO.findById(id);
    }

    // ==========================================================
    // 5. BUSCA POR EMAIL
    // ==========================================================

    /**
     * Recupera um paciente pelo seu email.
     *
     * @param email email cadastrado
     * @return paciente encontrado ou {@code null} se inexistente
     */
    public Patient findByEmail(String email) {
        return patientDAO.findByEmail(email);
    }
}