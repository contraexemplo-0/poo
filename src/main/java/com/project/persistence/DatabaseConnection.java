package com.project.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados SQLite.
 *
 * <p>Implementa o padrão Singleton, garantindo que toda a aplicação utilize
 * uma única instância de {@link Connection}. Isso evita conflitos e melhora
 * o gerenciamento de recursos, especialmente em aplicações desktop.</p>
 *
 * <p>Na inicialização, ativa chaves estrangeiras e cria as tabelas necessárias
 * caso ainda não existam.</p>
 *
 * <p>Esta classe não possui dependências de interface gráfica, podendo ser usada
 * tanto no modo console quanto no JavaFX.</p>
 */
public final class DatabaseConnection {

    /** Caminho padrão do arquivo SQLite utilizado pelo sistema. */
    private static final String DB_URL = "jdbc:sqlite:glucose.db";

    /** Instância única gerenciada pelo padrão Singleton. */
    private static DatabaseConnection instance;

    /** Conexão JDBC ativa. */
    private final Connection connection;

    /**
     * Construtor privado que abre a conexão com o banco e inicializa a estrutura
     * do banco de dados.
     *
     * @throws SQLException se ocorrer erro durante a abertura da conexão
     */
    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL);
        this.connection.setAutoCommit(true);
        initDatabase();
    }

    /**
     * Obtém a instância única da conexão com o banco.
     *
     * <p>Se a instância ainda não existir ou a conexão tiver sido fechada,
     * uma nova instância será criada automaticamente.</p>
     *
     * @return instância ativa de {@link DatabaseConnection}
     * @throws SQLException se ocorrer erro ao abrir uma nova conexão
     */
    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Retorna a conexão JDBC associada ao banco SQLite.
     *
     * @return a {@link Connection} ativa
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Inicializa o banco ativando suporte a chaves estrangeiras
     * e criando as tabelas necessárias.
     *
     * @throws SQLException se ocorrer erro ao executar comandos SQL
     */
    private void initDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        createTablesIfNeeded();
    }

    /**
     * Cria as tabelas patient e routine_event caso ainda não existam.
     *
     * <p>Esse método garante que o sistema possa iniciar em um banco vazio
     * sem necessidade de scripts externos.</p>
     *
     * @throws SQLException se ocorrer erro na criação das tabelas
     */
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
     * Fecha a conexão com o banco de dados.
     *
     * <p>Este método pode ser usado explicitamente em testes ou no encerramento
     * controlado da aplicação.</p>
     *
     * @throws SQLException se ocorrer erro ao fechar a conexão
     */
    public void close() throws SQLException {
        if (!connection.isClosed()) {
            connection.close();
        }
    }
}
