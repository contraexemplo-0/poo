package com.project.persistence;

import com.project.model.GlucoseCategory;
import com.project.model.MealCategory;
import com.project.model.Patient;
import com.project.model.RoutineEvent;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Camada de persistência responsável por criar a estrutura do banco SQLite e
 * executar operações relacionadas a usuários e eventos de rotina.
 */
public class DatabaseManager implements AutoCloseable {
    private final Connection conn;

    /**
     * Constrói o gerenciador inicializando a conexão e garantindo as tabelas.
     *
     * @throws SQLException caso ocorra erro ao conectar ou preparar o schema.
     */
    public DatabaseManager() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:glucose.db");
        createTables();
    }

    private void createTables() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
        CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT UNIQUE NOT NULL,
        password TEXT NOT NULL,
        user_type TEXT NOT NULL DEFAULT 'PATIENT'
        )""");

            st.execute("""
        CREATE TABLE IF NOT EXISTS routine_events (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        patient_id INTEGER NOT NULL,
        glucose_level REAL,
        glucose_datetime TEXT,
        glucose_category TEXT,
        meal_description TEXT,
        carbs REAL,
        gi REAL,
        meal_datetime TEXT,
        meal_category TEXT,
        weight REAL,
        activity_minutes INTEGER,
        FOREIGN KEY(patient_id) REFERENCES users(id)
        )""");
        }
    }

    public int insertUser(String name, String password) throws SQLException {
        String sql = "INSERT INTO users(name, password, user_type) VALUES (?, ?, 'PATIENT')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, password);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        }
    }

    public User findUserByName(String name) throws SQLException {
        String sql = "SELECT id, name, password FROM users WHERE name=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("name");
                    String password = rs.getString("password");
                    return new Patient(id, username, password);
                }
            }
        }
        return null;
    }

    public void insertRoutineEvent(RoutineEvent event) throws SQLException {
        String sql = "INSERT INTO routine_events(patient_id, glucose_level, glucose_datetime, glucose_category, meal_description, carbs, gi, meal_datetime, meal_category, weight, activity_minutes) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, event.getPatientId());
            ps.setObject(2, event.getGlucoseLevel());
            ps.setString(3, toText(event.getGlucoseDateTime()));
            ps.setString(4, enumToText(event.getGlucoseCategory()));
            ps.setString(5, event.getMealDescription());
            ps.setObject(6, event.getCarbs());
            ps.setObject(7, event.getGi());
            ps.setString(8, toText(event.getMealDateTime()));
            ps.setString(9, enumToText(event.getMealCategory()));
            ps.setObject(10, event.getWeight());
            ps.setObject(11, event.getActivityMinutes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    event.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateRoutineEvent(RoutineEvent event) throws SQLException {
        String sql = "UPDATE routine_events SET glucose_level=?, glucose_datetime=?, glucose_category=?, meal_description=?, carbs=?, gi=?, meal_datetime=?, meal_category=?, weight=?, activity_minutes=? WHERE id=? AND patient_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, event.getGlucoseLevel());
            ps.setString(2, toText(event.getGlucoseDateTime()));
            ps.setString(3, enumToText(event.getGlucoseCategory()));
            ps.setString(4, event.getMealDescription());
            ps.setObject(5, event.getCarbs());
            ps.setObject(6, event.getGi());
            ps.setString(7, toText(event.getMealDateTime()));
            ps.setString(8, enumToText(event.getMealCategory()));
            ps.setObject(9, event.getWeight());
            ps.setObject(10, event.getActivityMinutes());
            ps.setInt(11, event.getId());
            ps.setInt(12, event.getPatientId());
            ps.executeUpdate();
        }
    }

    public List<RoutineEvent> loadEventsByPatient(Patient patient) throws SQLException {
        String sql = "SELECT * FROM routine_events WHERE patient_id=? ORDER BY COALESCE(glucose_datetime, meal_datetime)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patient.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return mapEvents(rs);
            }
        }
    }

    public List<RoutineEvent> loadEventsByPatientAndDays(Patient patient, int days) throws SQLException {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        String sql = "SELECT * FROM routine_events WHERE patient_id=? AND COALESCE(glucose_datetime, meal_datetime) >= ? ORDER BY COALESCE(glucose_datetime, meal_datetime)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patient.getId());
            ps.setString(2, threshold.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return mapEvents(rs);
            }
        }
    }

    public void deleteEvent(int eventId, int patientId) throws SQLException {
        String sql = "DELETE FROM routine_events WHERE id = ? AND patient_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, patientId);
            ps.executeUpdate();
        }
    }

    private List<RoutineEvent> mapEvents(ResultSet rs) throws SQLException {
        List<RoutineEvent> list = new ArrayList<>();
        while (rs.next()) {
            RoutineEvent event = new RoutineEvent();
            event.setId(rs.getInt("id"));
            event.setPatientId(rs.getInt("patient_id"));
            event.setGlucoseLevel((Float) rs.getObject("glucose_level"));
            event.setGlucoseDateTime(parseDateTime(rs.getString("glucose_datetime")));
            event.setGlucoseCategory(parseGlucoseCategory(rs.getString("glucose_category")));
            event.setMealDescription(rs.getString("meal_description"));
            event.setCarbs((Float) rs.getObject("carbs"));
            event.setGi((Float) rs.getObject("gi"));
            event.setMealDateTime(parseDateTime(rs.getString("meal_datetime")));
            event.setMealCategory(parseMealCategory(rs.getString("meal_category")));
            event.setWeight((Float) rs.getObject("weight"));
            event.setActivityMinutes((Integer) rs.getObject("activity_minutes"));
            list.add(event);
        }
        return list;
    }

    private String enumToText(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    private LocalDateTime parseDateTime(String text) {
        return text != null ? LocalDateTime.parse(text) : null;
    }

    private GlucoseCategory parseGlucoseCategory(String value) {
        return value != null ? GlucoseCategory.valueOf(value) : null;
    }

    private MealCategory parseMealCategory(String value) {
        return value != null ? MealCategory.valueOf(value) : null;
    }

    private String toText(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString() : null;
    }

    /**
     * Fecha a conexão com o banco de dados.
     *
     * @throws SQLException caso ocorra erro durante o fechamento.
     */
    public void close() throws SQLException { conn.close(); }
}
