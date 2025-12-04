package com.project.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerencia a conexão única com o banco SQLite e garante
 * a criação das tabelas necessárias na inicialização.
 *
 * Não tem nenhuma dependência de UI (console ou JavaFX).
 */
public final class DatabaseConnection {

    // Caminho do arquivo .db (pode ajustar o nome/local se quiser)
    private static final String DB_URL = "jdbc:sqlite:glucose.db";

    private static DatabaseConnection instance;

    private final Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL);
        this.connection.setAutoCommit(true);
        initDatabase();
    }

    /**
     * Obtém a instância única (Singleton).
     */
    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Retorna a Connection nativa do JDBC.
     */
    public Connection getConnection() {
        return connection;
    }

    private void initDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        createTablesIfNeeded();
    }

    private void createTablesIfNeeded() throws SQLException {
        String createPatient = """
            CREATE TABLE IF NOT EXISTS patient (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                date_of_birth TEXT,
                gender TEXT,
                diabetes_type TEXT,
                diagnosis_date TEXT,
                carb_sensitivity REAL
            )
            """;

        String createRoutineEvent = """
            CREATE TABLE IF NOT EXISTS routine_event (
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
                FOREIGN KEY(patient_id) REFERENCES patient(id)
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPatient);
            stmt.execute(createRoutineEvent);
        }
    }

    /**
     * Fecha a conexão explicitamente (pode ser usado em testes ou ao encerrar a aplicação).
     */
    public void close() throws SQLException {
        if (!connection.isClosed()) {
            connection.close();
        }
    }
}
