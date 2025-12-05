package com.project.model;

import java.time.LocalDate;

/**
 * Representa o paciente diabético — o único tipo de usuário do sistema.
 *
 * <p>Armazena informações pessoais e clínicas essenciais para personalização
 * do tratamento, análise de histórico e previsões de glicemia.</p>
 *
 * <p>Principais atributos:</p>
 * <ul>
 *     <li><b>email</b> — utilizado para login;</li>
 *     <li><b>dateOfBirth</b> — data de nascimento;</li>
 *     <li><b>gender</b> — campo livre no MVP;</li>
 *     <li><b>diabetesType</b> — ex.: "Tipo 1", "Tipo 2";</li>
 *     <li><b>diagnosisDate</b> — data do diagnóstico médico;</li>
 *     <li><b>carbSensitivity</b> — sensibilidade a carboidratos
 *         em mg/dL por grama;</li>
 * </ul>
 *
 * <p>A classe herda de {@link User} os atributos básicos de identificação e senha.</p>
 */
public class Patient extends User {

    /** Valor padrão utilizado caso a sensibilidade não seja definida. */
    public static final float DEFAULT_CARB_SENSITIVITY = 3.0f;

    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String diabetesType;
    private LocalDate diagnosisDate;
    private Float carbSensitivity;

    /**
     * Construtor completo, geralmente utilizado ao carregar dados do banco de dados.
     *
     * @param id identificador do paciente
     * @param name nome do paciente
     * @param email email utilizado para login
     * @param password senha em texto simples (MVP)
     * @param dateOfBirth data de nascimento
     * @param gender gênero declarado
     * @param diabetesType tipo de diabetes (ex.: "Tipo 2")
     * @param diagnosisDate data do diagnóstico clínico
     * @param carbSensitivity sensibilidade a carboidratos em mg/dL por grama
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
     * Construtor utilizado durante o cadastro do paciente.
     * O ID será gerado automaticamente pelo banco (AUTOINCREMENT).
     *
     * @param name nome do paciente
     * @param email email para login
     * @param password senha em texto simples
     * @param dateOfBirth data de nascimento
     * @param gender gênero declarado
     * @param diabetesType tipo de diabetes
     * @param diagnosisDate data do diagnóstico
     * @param carbSensitivity sensibilidade a carboidratos
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
     * Construtor legado mantido para compatibilidade com código antigo.
     * Os demais campos são atribuídos como {@code null}.
     *
     * @param id identificador do paciente
     * @param name nome do paciente
     * @param password senha em texto simples
     */
    public Patient(int id, String name, String password) {
        this(id, name, null, password, null, null, null, null, null);
    }

    // -----------------------------------------------------------
    // Getters e Setters
    // -----------------------------------------------------------

    /** @return email do paciente */
    public String getEmail() {
        return email;
    }

    /** @param email novo email do paciente */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return data de nascimento */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /** @param dateOfBirth nova data de nascimento */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /** @return gênero declarado */
    public String getGender() {
        return gender;
    }

    /** @param gender novo gênero */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /** @return tipo de diabetes */
    public String getDiabetesType() {
        return diabetesType;
    }

    /** @param diabetesType novo tipo de diabetes */
    public void setDiabetesType(String diabetesType) {
        this.diabetesType = diabetesType;
    }

    /** @return data do diagnóstico */
    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    /** @param diagnosisDate nova data de diagnóstico */
    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    /** @return sensibilidade a carboidratos (mg/dL por grama) */
    public Float getCarbSensitivity() {
        return carbSensitivity;
    }

    /** @param carbSensitivity nova sensibilidade a carboidratos */
    public void setCarbSensitivity(Float carbSensitivity) {
        this.carbSensitivity = carbSensitivity;
    }

    /**
     * Retorna a sensibilidade efetiva do paciente.
     * Caso o valor não esteja definido, utiliza {@link #DEFAULT_CARB_SENSITIVITY}.
     *
     * @return sensibilidade em mg/dL por grama de carboidrato
     */
    public float getEffectiveCarbSensitivity() {
        return carbSensitivity != null ? carbSensitivity : DEFAULT_CARB_SENSITIVITY;
    }

    // -----------------------------------------------------------
    // Métodos de resumo usados na UI
    // -----------------------------------------------------------

    /**
     * Retorna um texto breve de apresentação do paciente.
     *
     * @return descrição resumida para uso em telas gerais
     */
    @Override
    public String getSummary() {
        return "Paciente " + getName() + ": acompanhe suas medições e mantenha seu tratamento em dia.";
    }

    /**
     * Retorna um resumo informativo utilizado no dashboard.
     *
     * @return mensagem com contexto para a tela principal
     */
    @Override
    public String getDashboardSummary() {
        return "Resumo do paciente " + getName()
                + ": registre novas medições para acompanhar tendências recentes.";
    }
}
