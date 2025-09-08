package com.project.persistence;

import com.project.model.GlucoseMeasure;
import com.project.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements AutoCloseable {
    private final Connection conn;

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
        password TEXT NOT NULL
        )""");

            st.execute("CREATE TABLE IF NOT EXISTS glucose_measures (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, level REAL NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, note TEXT, FOREIGN KEY(user_id) REFERENCES users(id))");
        }
    }

    public int insertUser(String name, String password) throws SQLException {
        String sql = "INSERT INTO users(name, password) VALUES (?, ?)";
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

    public boolean checkLogin(String name, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE name = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true se encontrou o usuário
            }
        }
    }

    public User findUserByName(String name) throws SQLException {
        String sql = "SELECT id, name, password FROM users WHERE name=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new User(rs.getInt("id"), rs.getString("name"), rs.getString("password"));
            }
        }
        return null;
    }

    public void insertMeasure(int userId, GlucoseMeasure m) throws SQLException {
        String sql = "INSERT INTO glucose_measures(user_id, level, date, time, note) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setFloat(2, m.getLevel());
            ps.setString(3, m.getDate().toString());
            ps.setString(4, m.getTime().toString());
            ps.setString(5, m.getNote());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) m.setId(rs.getInt(1));
            }
        }
    }

    public void deleteMeasure(int userId, int measureId) throws SQLException {
        String sql = "DELETE FROM glucose_measures WHERE id = ? and user_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, measureId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public List<GlucoseMeasure> loadMeasures(int userId) throws SQLException {
        List<GlucoseMeasure> list = new ArrayList<>();
        String sql = "SELECT id, level, date, time, note FROM glucose_measures WHERE user_id=? ORDER BY date,time";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GlucoseMeasure m = new GlucoseMeasure(rs.getFloat("level"),
                            LocalDate.parse(rs.getString("date")),
                            LocalTime.parse(rs.getString("time")),
                            rs.getString("note"));
                    m.setId(rs.getInt("id"));
                    list.add(m);
                }
            }
        }
        return list;
    }

    public void close() throws SQLException { conn.close(); }
}