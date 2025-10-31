package com.project.service;

import com.project.model.GlucoseMeasure;
import com.project.model.HealthProfessional;
import com.project.model.Patient;
import com.project.model.User;
import com.project.model.Historic;
import com.project.model.UserType;
import com.project.persistence.DatabaseManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class UserService {
    private final DatabaseManager db;

    public UserService(DatabaseManager db) {
        this.db = db;
    }

    // Cadastro
    public User register(String name, String password, UserType type) throws SQLException {
        int id = db.insertUser(name, password, type);
        return createUser(id, name, password, type);
    }

    // Login
    public User login(String name, String password) throws SQLException {
        User user = db.findUserByName(name);
        if (user != null && user.checkPassword(password)) {
            if (user instanceof Patient) {
                loadHistoric(user);
            }
            return user;
        }
        return null;
    }

    public Historic loadHistoric(User user) throws SQLException {
        List<GlucoseMeasure> measures = db.loadMeasures(user.getId());
        return new Historic(measures);
    }

    // Adiciona nova medida
    public GlucoseMeasure addMeasure(User user, float level, String note) throws SQLException {
        ensurePatient(user);
        if (level < 0) throw new IllegalArgumentException("Valor de glicose não pode ser negativo");
        GlucoseMeasure m = new GlucoseMeasure(level, LocalDate.now(), LocalTime.now(), note);
        db.insertMeasure(user.getId(), m);
        return m;
    }

    public GlucoseMeasure addMeasure(User user, float level) throws SQLException {
        ensurePatient(user);
        GlucoseMeasure m = new GlucoseMeasure(level, LocalDate.now(), LocalTime.now()); // note = null
        db.insertMeasure(user.getId(), m);
        return m;
    }


    public void removeMeasure(User user, int measureId) throws SQLException{
        ensurePatient(user);
        db.deleteMeasure(user.getId(), measureId);
    }

    public User findByName(String name) throws SQLException {
        return db.findUserByName(name);
    }

    private void ensurePatient(User user) {
        if (!(user instanceof Patient)) {
            throw new IllegalArgumentException("Somente pacientes podem registrar ou remover medidas.");
        }
    }

    private User createUser(int id, String name, String password, UserType type) {
        return switch (type) {
            case PATIENT -> new Patient(id, name, password);
            case HEALTH_PROFESSIONAL -> new HealthProfessional(id, name, password);
        };
    }
}
