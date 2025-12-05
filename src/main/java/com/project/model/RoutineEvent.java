package com.project.model;

import java.time.LocalDateTime;

/**
 * Representa um evento de rotina registrado pelo paciente.
 *
 * <p>Um evento pode conter:</p>
 * <ul>
 *     <li><b>apenas uma medição de glicemia</b>;</li>
 *     <li><b>apenas uma refeição</b>;</li>
 *     <li><b>glicemia + refeição</b> (evento completo);</li>
 * </ul>
 *
 * <p>Esta é a entidade central do histórico do paciente e é utilizada tanto
 * para exibições na interface quanto para análises e previsões de glicemia.</p>
 */
public class RoutineEvent {

    /** Identificador único do evento no banco de dados. */
    private int id;

    /** Identificador do paciente ao qual o evento pertence. */
    private int patientId;

    // -------------------------------------------------------------------------
    // Dados de glicemia
    // -------------------------------------------------------------------------

    /** Valor da glicemia medida em mg/dL (pode ser {@code null}). */
    private Float glucoseLevel;

    /** Data e hora em que a glicemia foi medida. */
    private LocalDateTime glucoseDateTime;

    /** Categoria clínica da medição de glicose (jejum, pré, pós, aleatória). */
    private GlucoseCategory glucoseCategory;

    // -------------------------------------------------------------------------
    // Dados da refeição
    // -------------------------------------------------------------------------

    /** Descrição textual da refeição ingerida. */
    private String mealDescription;

    /** Quantidade estimada de carboidratos ingeridos (em gramas). */
    private Float carbs;

    /** Índice glicêmico estimado da refeição. */
    private Float gi;

    /** Data e hora em que a refeição ocorreu. */
    private LocalDateTime mealDateTime;

    /** Categoria da refeição (café, almoço, jantar etc.). */
    private MealCategory mealCategory;

    // -------------------------------------------------------------------------
    // Dados opcionais adicionais
    // -------------------------------------------------------------------------

    /** Peso atual do paciente (em kg) registrado durante o evento. */
    private Float weight;

    /** Minutos de atividade física próximos ao evento. */
    private Integer activityMinutes;

    /**
     * Construtor padrão.
     *
     * <p>Cria um evento vazio, com todos os campos opcionais iniciados como {@code null}.</p>
     */
    public RoutineEvent() {
    }

    // -------------------------------------------------------------------------
    // Getters e Setters
    // -------------------------------------------------------------------------

    /** @return identificador único do evento */
    public int getId() {
        return id;
    }

    /** @param id novo identificador único do evento */
    public void setId(int id) {
        this.id = id;
    }

    /** @return ID do paciente ao qual o evento pertence */
    public int getPatientId() {
        return patientId;
    }

    /** @param patientId novo ID do paciente associado ao evento */
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    /** @return glicemia medida em mg/dL, ou {@code null} */
    public Float getGlucoseLevel() {
        return glucoseLevel;
    }

    /** @param glucoseLevel novo valor de glicemia medida */
    public void setGlucoseLevel(Float glucoseLevel) {
        this.glucoseLevel = glucoseLevel;
    }

    /** @return data e hora da medição de glicemia, ou {@code null} */
    public LocalDateTime getGlucoseDateTime() {
        return glucoseDateTime;
    }

    /** @param glucoseDateTime nova data e hora da medição */
    public void setGlucoseDateTime(LocalDateTime glucoseDateTime) {
        this.glucoseDateTime = glucoseDateTime;
    }

    /** @return categoria da glicemia */
    public GlucoseCategory getGlucoseCategory() {
        return glucoseCategory;
    }

    /** @param glucoseCategory nova categoria da glicemia */
    public void setGlucoseCategory(GlucoseCategory glucoseCategory) {
        this.glucoseCategory = glucoseCategory;
    }

    /** @return descrição textual da refeição */
    public String getMealDescription() {
        return mealDescription;
    }

    /** @param mealDescription nova descrição da refeição */
    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    /** @return carboidratos ingeridos estimados em gramas */
    public Float getCarbs() {
        return carbs;
    }

    /** @param carbs nova quantidade estimada de carboidratos */
    public void setCarbs(Float carbs) {
        this.carbs = carbs;
    }

    /** @return índice glicêmico estimado */
    public Float getGi() {
        return gi;
    }

    /** @param gi novo índice glicêmico estimado */
    public void setGi(Float gi) {
        this.gi = gi;
    }

    /** @return data e hora da refeição */
    public LocalDateTime getMealDateTime() {
        return mealDateTime;
    }

    /** @param mealDateTime nova data e hora da refeição */
    public void setMealDateTime(LocalDateTime mealDateTime) {
        this.mealDateTime = mealDateTime;
    }

    /** @return categoria da refeição */
    public MealCategory getMealCategory() {
        return mealCategory;
    }

    /** @param mealCategory nova categoria da refeição */
    public void setMealCategory(MealCategory mealCategory) {
        this.mealCategory = mealCategory;
    }

    /** @return peso do paciente em kg */
    public Float getWeight() {
        return weight;
    }

    /** @param weight novo peso do paciente registrado durante o evento */
    public void setWeight(Float weight) {
        this.weight = weight;
    }

    /** @return minutos de atividade física */
    public Integer getActivityMinutes() {
        return activityMinutes;
    }

    /** @param activityMinutes novos minutos de atividade física */
    public void setActivityMinutes(Integer activityMinutes) {
        this.activityMinutes = activityMinutes;
    }

    // -------------------------------------------------------------------------
    // Métodos utilitários
    // -------------------------------------------------------------------------

    /**
     * Verifica se o evento contém uma medição de glicemia completa
     * (valor + data/hora).
     *
     * @return {@code true} se ambos os campos estiverem preenchidos
     */
    public boolean hasGlucose() {
        return glucoseLevel != null && glucoseDateTime != null;
    }

    /**
     * Verifica se o evento contém uma refeição registrada.
     *
     * @return {@code true} se houver descrição textual da refeição
     */
    public boolean hasMeal() {
        return mealDescription != null && !mealDescription.isBlank();
    }
}
