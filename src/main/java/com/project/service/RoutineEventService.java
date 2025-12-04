package com.project.service;

import com.project.model.GlucoseCategory;
import com.project.model.MealCategory;
import com.project.model.ParsedFoodInfo;
import com.project.model.Patient;
import com.project.model.RoutineEvent;
import com.project.persistence.RoutineEventDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio para eventos de rotina:
 * glicemias, refeições e combinações.
 *
 * Agora utiliza RoutineEventDAO em vez de DatabaseManager.
 */
public class RoutineEventService {

    private final RoutineEventDAO eventDAO;
    private final FoodParserService foodParser;

    public RoutineEventService(FoodParserService foodParser) throws SQLException {
        this.eventDAO = new RoutineEventDAO();
        this.foodParser = foodParser;
    }

    // ==========================================================
    // Registrar apenas glicemia
    // ==========================================================
    public RoutineEvent registerGlucose(Patient patient,
                                        float level,
                                        GlucoseCategory category,
                                        LocalDateTime dateTime) {

        RoutineEvent event = new RoutineEvent();
        event.setPatientId(patient.getId());
        event.setGlucoseLevel(level);
        event.setGlucoseCategory(category);
        event.setGlucoseDateTime(dateTime);

        return eventDAO.insert(event);
    }

    // ==========================================================
    // Registrar refeição + glicemia completa
    // ==========================================================
    public RoutineEvent registerMealAndGlucose(Patient patient,
                                               String mealDescription,
                                               LocalDateTime mealDateTime,
                                               MealCategory mealCategory,
                                               float glucoseLevel,
                                               GlucoseCategory glucoseCategory,
                                               LocalDateTime glucoseDateTime) {

        RoutineEvent event = createMealEvent(patient, mealDescription, mealDateTime, mealCategory);

        event.setGlucoseLevel(glucoseLevel);
        event.setGlucoseCategory(glucoseCategory);
        event.setGlucoseDateTime(glucoseDateTime);

        return eventDAO.insert(event);
    }

    // ==========================================================
    // Registrar apenas refeição
    // ==========================================================
    public RoutineEvent registerMealOnly(Patient patient,
                                         String mealDescription,
                                         LocalDateTime mealDateTime,
                                         MealCategory mealCategory) {

        RoutineEvent event = createMealEvent(patient, mealDescription, mealDateTime, mealCategory);
        return eventDAO.insert(event);
    }

    // ==========================================================
    // Atualizar evento adicionando glicemia mais tarde
    // ==========================================================
    public void updateEventWithGlucose(RoutineEvent event,
                                       float glucoseLevel,
                                       GlucoseCategory category,
                                       LocalDateTime dateTime) {

        event.setGlucoseLevel(glucoseLevel);
        event.setGlucoseCategory(category);
        event.setGlucoseDateTime(dateTime);

        eventDAO.update(event);
    }

    // ==========================================================
    // Carregar eventos (com filtro opcional por dias)
    // ==========================================================
    public List<RoutineEvent> loadEvents(Patient patient, int days) {
        if (days > 0) {
            return eventDAO.findByPatientAndLastDays(patient.getId(), days);
        }
        return eventDAO.findAllByPatientId(patient.getId());
    }

    // ==========================================================
    // Carregar todos eventos do paciente (sem filtro)
    // ==========================================================
    public List<RoutineEvent> loadEvents(Patient patient) {
        return eventDAO.findAllByPatientId(patient.getId());
    }

    // ==========================================================
    // Deletar evento
    // ==========================================================
    public void deleteEvent(int eventId) {
        eventDAO.delete(eventId);
    }

    // ==========================================================
    // Factory interna para criar eventos de refeição
    // ==========================================================
    private RoutineEvent createMealEvent(Patient patient,
                                         String mealDescription,
                                         LocalDateTime mealDateTime,
                                         MealCategory mealCategory) {

        RoutineEvent event = new RoutineEvent();
        event.setPatientId(patient.getId());
        event.setMealDescription(mealDescription);
        event.setMealDateTime(mealDateTime);
        event.setMealCategory(mealCategory);

        // Integração com o parser automático (BasicFoodParser, LLM, etc.)
        ParsedFoodInfo info = (foodParser != null)
                ? foodParser.parse(mealDescription)
                : null;

        if (info != null) {
            event.setCarbs(info.getCarbs());
            event.setGi(info.getGi());
        }

        return event;
    }

    public RoutineEvent saveEvent(RoutineEvent event) {
        if (event.getId() == 0) {
            return eventDAO.insert(event);
        } else {
            eventDAO.update(event);
            return event;
        }
    }
}
