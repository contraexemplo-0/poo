package com.project.service;

import com.project.model.GlucoseCategory;
import com.project.model.MealCategory;
import com.project.model.ParsedFoodInfo;
import com.project.model.Patient;
import com.project.model.RoutineEvent;
import com.project.persistence.DatabaseManager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RoutineEventService {

    private final DatabaseManager db;
    private final FoodParserService foodParser;

    public RoutineEventService(DatabaseManager db, FoodParserService foodParser) {
        this.db = db;
        this.foodParser = foodParser;
    }

    public RoutineEvent registerGlucose(Patient patient, float level, GlucoseCategory category, LocalDateTime dateTime) throws SQLException {
        RoutineEvent event = new RoutineEvent();
        event.setPatientId(patient.getId());
        event.setGlucoseLevel(level);
        event.setGlucoseCategory(category);
        event.setGlucoseDateTime(dateTime);
        db.insertRoutineEvent(event);
        return event;
    }

    public RoutineEvent registerMealAndGlucose(Patient patient,
                                               String mealDescription,
                                               LocalDateTime mealDateTime,
                                               MealCategory mealCategory,
                                               float glucoseLevel,
                                               GlucoseCategory glucoseCategory,
                                               LocalDateTime glucoseDateTime) throws SQLException {
        RoutineEvent event = createMealEvent(patient, mealDescription, mealDateTime, mealCategory);
        event.setGlucoseLevel(glucoseLevel);
        event.setGlucoseCategory(glucoseCategory);
        event.setGlucoseDateTime(glucoseDateTime);
        db.insertRoutineEvent(event);
        return event;
    }

    public RoutineEvent registerMealOnly(Patient patient,
                                         String mealDescription,
                                         LocalDateTime mealDateTime,
                                         MealCategory mealCategory) throws SQLException {
        RoutineEvent event = createMealEvent(patient, mealDescription, mealDateTime, mealCategory);
        db.insertRoutineEvent(event);
        return event;
    }

    public void updateEventWithGlucose(RoutineEvent event,
                                       float glucoseLevel,
                                       GlucoseCategory category,
                                       LocalDateTime dateTime) throws SQLException {
        event.setGlucoseLevel(glucoseLevel);
        event.setGlucoseCategory(category);
        event.setGlucoseDateTime(dateTime);
        db.updateRoutineEvent(event);
    }

    public List<RoutineEvent> loadEvents(Patient patient, int days) throws SQLException {
        if (days > 0) {
            return db.loadEventsByPatientAndDays(patient, days);
        }
        return db.loadEventsByPatient(patient);
    }

    public void deleteEvent(int eventId, Patient patient) throws SQLException {
        db.deleteEvent(eventId, patient.getId());
    }

    private RoutineEvent createMealEvent(Patient patient, String mealDescription, LocalDateTime mealDateTime, MealCategory mealCategory) {
        RoutineEvent event = new RoutineEvent();
        event.setPatientId(patient.getId());
        event.setMealDescription(mealDescription);
        event.setMealDateTime(mealDateTime);
        event.setMealCategory(mealCategory);

        ParsedFoodInfo info = foodParser != null ? foodParser.parse(mealDescription) : null;
        if (info != null) {
            event.setCarbs(info.getCarbs());
            event.setGi(info.getGi());
        }
        return event;
    }
}
