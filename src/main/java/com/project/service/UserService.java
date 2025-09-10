package com.project.service;

import com.project.model.GlucoseMeasure;
import com.project.model.User;
import com.project.model.Historic;
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
    public User register(String name, String password) throws SQLException {
        int id = db.insertUser(name, password);
        return new User(id, name, password); // agora com id correto
    }

    // Login
    public User login(String name, String password) throws SQLException {
        User user = db.findUserByName(name);
        if (user != null && user.checkPassword(password)) {
            loadHistoric(user);
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
        if (level < 0) throw new IllegalArgumentException("Valor de glicose não pode ser negativo");
        GlucoseMeasure m = new GlucoseMeasure(level, LocalDate.now(), LocalTime.now(), note);
        db.insertMeasure(user.getId(), m);
        return m;
    }

    public GlucoseMeasure addMeasure(User user, float level) throws SQLException {
        GlucoseMeasure m = new GlucoseMeasure(level, LocalDate.now(), LocalTime.now()); // note = null
        db.insertMeasure(user.getId(), m);
        return m;
    }


    public void removeMeasure(User user, int measureId) throws SQLException{
        db.deleteMeasure(user.getId(), measureId);
    }
}