package com.project.service;

import com.project.model.Patient;
import com.project.model.User;
import com.project.persistence.DatabaseManager;

import java.sql.SQLException;

/**
 * Serviço que concentra as regras de negócio relacionadas a usuários.
 */
public class UserService {
    private final DatabaseManager db;

    public UserService(DatabaseManager db) {
        this.db = db;
    }

    public User register(String name, String password) throws SQLException {
        int id = db.insertUser(name, password);
        return new Patient(id, name, password);
    }

    public User login(String name, String password) throws SQLException {
        User user = db.findUserByName(name);
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }

    public User findByName(String name) throws SQLException {
        return db.findUserByName(name);
    }
}
