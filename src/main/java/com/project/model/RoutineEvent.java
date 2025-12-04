package com.project.model;

import java.time.LocalDateTime;

/**
 * Representa um evento de rotina do paciente.
 *
 * Pode conter:
 * - apenas glicemia
 * - apenas refeição
 * - glicemia + refeição (evento completo)
 */
public class RoutineEvent {

    private int id;
    private int patientId;

    // --- Dados de glicemia ---
    private Float glucoseLevel;              // mg/dL (pode ser null)
    private LocalDateTime glucoseDateTime;   // quando a glicemia foi medida
    private GlucoseCategory glucoseCategory; // JEJUM, PRE, POS, ALEATÓRIA

    // --- Dados da refeição ---
    private String mealDescription;          // descrição textual
    private Float carbs;                     // carboidratos estimados (g)
    private Float gi;                        // índice glicêmico estimado
    private LocalDateTime mealDateTime;      // quando a refeição ocorreu
    private MealCategory mealCategory;       // café, almoço, lanche, etc.

    // --- Dados opcionais ---
    private Float weight;                    // peso em kg
    private Integer activityMinutes;         // minutos de atividade física

    public RoutineEvent() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Float getGlucoseLevel() {
        return glucoseLevel;
    }

    public void setGlucoseLevel(Float glucoseLevel) {
        this.glucoseLevel = glucoseLevel;
    }

    public LocalDateTime getGlucoseDateTime() {
        return glucoseDateTime;
    }

    public void setGlucoseDateTime(LocalDateTime glucoseDateTime) {
        this.glucoseDateTime = glucoseDateTime;
    }

    public GlucoseCategory getGlucoseCategory() {
        return glucoseCategory;
    }

    public void setGlucoseCategory(GlucoseCategory glucoseCategory) {
        this.glucoseCategory = glucoseCategory;
    }

    public String getMealDescription() {
        return mealDescription;
    }

    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    public Float getCarbs() {
        return carbs;
    }

    public void setCarbs(Float carbs) {
        this.carbs = carbs;
    }

    public Float getGi() {
        return gi;
    }

    public void setGi(Float gi) {
        this.gi = gi;
    }

    public LocalDateTime getMealDateTime() {
        return mealDateTime;
    }

    public void setMealDateTime(LocalDateTime mealDateTime) {
        this.mealDateTime = mealDateTime;
    }

    public MealCategory getMealCategory() {
        return mealCategory;
    }

    public void setMealCategory(MealCategory mealCategory) {
        this.mealCategory = mealCategory;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getActivityMinutes() {
        return activityMinutes;
    }

    public void setActivityMinutes(Integer activityMinutes) {
        this.activityMinutes = activityMinutes;
    }

    // Helpers que podem ser úteis no futuro (JavaFX, relatórios, etc.)

    public boolean hasGlucose() {
        return glucoseLevel != null && glucoseDateTime != null;
    }

    public boolean hasMeal() {
        return mealDescription != null && !mealDescription.isBlank();
    }

}
