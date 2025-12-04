package com.project.model;

import java.time.LocalDate;

/**
 * Representa o paciente diabético (único tipo de usuário do sistema).
 *
 * Campos principais:
 * - id: identificador no banco
 * - name: nome exibido
 * - email: usado para login
 * - password: senha em texto plano (MVP) – depois dá para evoluir para hash
 * - dateOfBirth: data de nascimento
 * - gender: gênero (string livre no MVP)
 * - diabetesType: tipo de diabetes (ex.: "Tipo 1", "Tipo 2")
 * - diagnosisDate: data do diagnóstico
 * - carbSensitivity: sensibilidade a carboidrato (mg/dL por grama)
 */
public class Patient extends User {

    public static final float DEFAULT_CARB_SENSITIVITY = 3.0f;

    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String diabetesType;
    private LocalDate diagnosisDate;
    private Float carbSensitivity; // mg/dL por grama de carboidrato

    /**
     * Construtor completo, pensado para quando você já tem todos os dados
     * (por exemplo, ao carregar do banco).
     */
    public Patient(int id,
                   String name,
                   String email,
                   String password,
                   LocalDate dateOfBirth,
                   String gender,
                   String diabetesType,
                   LocalDate diagnosisDate,
                   Float carbSensitivity) {
        super(id, name, password);
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.diabetesType = diabetesType;
        this.diagnosisDate = diagnosisDate;
        this.carbSensitivity = carbSensitivity;
    }

    /**
     * Construtor pensado para cadastro (sem id ainda).
     * O id será preenchido pelo banco (AUTOINCREMENT).
     */
    public Patient(String name,
                   String email,
                   String password,
                   LocalDate dateOfBirth,
                   String gender,
                   String diabetesType,
                   LocalDate diagnosisDate,
                   Float carbSensitivity) {
        this(0, name, email, password, dateOfBirth, gender, diabetesType, diagnosisDate, carbSensitivity);
    }

    /**
     * Construtor legado para não quebrar o código atual que usa
     * (int id, String name, String password).
     *
     * Ele delega para o construtor completo, deixando os campos novos como null.
     */
    public Patient(int id, String name, String password) {
        this(id, name, null, password, null, null, null, null, null);
    }

    // --- Getters e setters novos ---

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDiabetesType() {
        return diabetesType;
    }

    public void setDiabetesType(String diabetesType) {
        this.diabetesType = diabetesType;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public Float getCarbSensitivity() {
        return carbSensitivity;
    }

    public void setCarbSensitivity(Float carbSensitivity) {
        this.carbSensitivity = carbSensitivity;
    }

    /**
     * Retorna a sensibilidade efetiva (campo armazenado ou valor padrão).
     */
    public float getEffectiveCarbSensitivity() {
        return carbSensitivity != null ? carbSensitivity : DEFAULT_CARB_SENSITIVITY;
    }

    // --- Textos de resumo usados na UI (console/JavaFX) ---

    @Override
    public String getSummary() {
        return "Paciente " + getName() + ": acompanhe suas medições e mantenha seu tratamento em dia.";
    }

    @Override
    public String getDashboardSummary() {
        return "Resumo do paciente " + getName()
                + ": registre novas medições para acompanhar tendências recentes.";
    }
}
