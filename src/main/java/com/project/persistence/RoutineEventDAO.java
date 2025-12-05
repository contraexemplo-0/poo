package com.project.persistence;

import com.project.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável por operações de persistência da entidade {@link RoutineEvent}.
 *
 * <p>Fornece métodos de CRUD completos, além de consultas por paciente,
 * intervalos de datas e períodos relativos. Toda a comunicação com o banco SQLite
 * referente a eventos de rotina é centralizada nesta classe.</p>
 *
 * <p>Essa classe não depende de UI e pode ser utilizada tanto no modo console
 * quanto em aplicações JavaFX.</p>
 */
public class RoutineEventDAO {

    /** Conexão JDBC ativa obtida via {@link DatabaseConnection}. */
    private final Connection conn;

    /**
     * Cria uma nova instância do DAO, associada à conexão única do sistema.
     *
     * @throws SQLException se ocorrer erro ao obter a conexão
     */
    public RoutineEventDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ===========================================================
    // INSERT
    // ===========================================================

    /**
     * Insere um novo {@link RoutineEvent} no banco de dados.
     *
     * <p>Após a inserção, o ID gerado automaticamente é atribuído ao objeto
     * fornecido.</p>
     *
     * @param e evento a ser inserido
     * @return o próprio evento com ID atualizado
     * @throws RuntimeException se ocorrer erro de SQL
     */
    public RoutineEvent insert(RoutineEvent e) {
        String sql = """
            INSERT INTO routine_event
            (patient_id, glucose_level, glucose_datetime, glucose_category,
             meal_description, carbs, gi, meal_datetime, meal_category,
             weight, activity_minutes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, e.getPatientId());
            stmt.setObject(2, e.getGlucoseLevel());
            stmt.setString(3, toText(e.getGlucoseDateTime()));
            stmt.setString(4, toText(e.getGlucoseCategory()));

            stmt.setString(5, e.getMealDescription());
            stmt.setObject(6, e.getCarbs());
            stmt.setObject(7, e.getGi());
            stmt.setString(8, toText(e.getMealDateTime()));
            stmt.setString(9, toText(e.getMealCategory()));

            stmt.setObject(10, e.getWeight());
            stmt.setObject(11, e.getActivityMinutes());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }

            return e;

        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao inserir evento: " + ex.getMessage(), ex);
        }
    }

    // ===========================================================
    // FIND ALL BY PATIENT
    // ===========================================================

    /**
     * Lista todos os eventos cadastrados para um paciente,
     * ordenados do mais recente para o mais antigo.
     *
     * @param patientId ID do paciente
     * @return lista de eventos associados ao paciente
     * @throws RuntimeException se ocorrer erro de SQL
     */
    public List<RoutineEvent> findAllByPatientId(int patientId) {
        String sql = "SELECT * FROM routine_event WHERE patient_id = ? ORDER BY id DESC";

        List<RoutineEvent> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapEvent(rs));
                }
            }

            return result;

        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao buscar eventos: " + ex.getMessage(), ex);
        }
    }

    // ===========================================================
    // FIND BY PATIENT AND LAST X DAYS
    // ===========================================================

    /**
     * Retorna todos os eventos de um paciente ocorridos nos últimos N dias.
     *
     * <p>A consulta considera tanto datas de glicemia quanto datas de refeição.</p>
     *
     * @param patientId ID do paciente
     * @param days número de dias para filtrar
     * @return lista de eventos dentro do período
     */
    public List<RoutineEvent> findByPatientAndLastDays(int patientId, int days) {

        String modifier = "-" + days + " days";

        String sql = """
            SELECT * FROM routine_event
            WHERE patient_id = ?
              AND (
                    (glucose_datetime IS NOT NULL AND glucose_datetime >= datetime('now', ?))
                 OR (meal_datetime IS NOT NULL AND meal_datetime >= datetime('now', ?))
              )
            ORDER BY id DESC
        """;

        List<RoutineEvent> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.setString(2, modifier);
            stmt.setString(3, modifier);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapEvent(rs));
                }
            }

            return result;

        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao buscar eventos por período: " + ex.getMessage(), ex);
        }
    }

    // ===========================================================
    // UPDATE
    // ===========================================================

    /**
     * Atualiza um evento existente no banco de dados.
     *
     * @param e evento contendo os novos valores
     * @throws RuntimeException se ocorrer erro de SQL
     */
    public void update(RoutineEvent e) {
        String sql = """
            UPDATE routine_event SET
                patient_id = ?, glucose_level = ?, glucose_datetime = ?, glucose_category = ?,
                meal_description = ?, carbs = ?, gi = ?, meal_datetime = ?, meal_category = ?,
                weight = ?, activity_minutes = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, e.getPatientId());
            stmt.setObject(2, e.getGlucoseLevel());
            stmt.setString(3, toText(e.getGlucoseDateTime()));
            stmt.setString(4, toText(e.getGlucoseCategory()));

            stmt.setString(5, e.getMealDescription());
            stmt.setObject(6, e.getCarbs());
            stmt.setObject(7, e.getGi());
            stmt.setString(8, toText(e.getMealDateTime()));
            stmt.setString(9, toText(e.getMealCategory()));

            stmt.setObject(10, e.getWeight());
            stmt.setObject(11, e.getActivityMinutes());
            stmt.setInt(12, e.getId());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao atualizar evento: " + ex.getMessage(), ex);
        }
    }

    // ===========================================================
    // DELETE
    // ===========================================================

    /**
     * Remove um evento pelo ID.
     *
     * @param id identificador do evento
     */
    public void delete(int id) {
        try (PreparedStatement stmt =
                     conn.prepareStatement("DELETE FROM routine_event WHERE id = ?")) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar evento: " + e.getMessage(), e);
        }
    }

    // ===========================================================
    // MAP RESULTSET → ROUTINEEVENT
    // ===========================================================

    /**
     * Constrói um objeto {@link RoutineEvent} a partir de uma linha
     * de {@link ResultSet}.
     *
     * <p>Este método centraliza o mapeamento SQL → modelo de domínio.</p>
     *
     * @param rs resultado da consulta SQL
     * @return instância preenchida de {@link RoutineEvent}
     * @throws SQLException se ocorrer erro na leitura dos campos
     */
    private RoutineEvent mapEvent(ResultSet rs) throws SQLException {

        RoutineEvent e = new RoutineEvent();

        e.setId(rs.getInt("id"));
        e.setPatientId(rs.getInt("patient_id"));

        // ---- FLOATS ----
        e.setGlucoseLevel(rs.getObject("glucose_level") != null
                ? ((Number) rs.getObject("glucose_level")).floatValue()
                : null);

        e.setCarbs(rs.getObject("carbs") != null
                ? ((Number) rs.getObject("carbs")).floatValue()
                : null);

        e.setGi(rs.getObject("gi") != null
                ? ((Number) rs.getObject("gi")).floatValue()
                : null);

        e.setWeight(rs.getObject("weight") != null
                ? ((Number) rs.getObject("weight")).floatValue()
                : null);

        // ---- INTEIROS ----
        e.setActivityMinutes(rs.getObject("activity_minutes") != null
                ? ((Number) rs.getObject("activity_minutes")).intValue()
                : null);

        // ---- DATAS ----
        e.setGlucoseDateTime(parseDateTime(rs.getString("glucose_datetime")));
        e.setMealDateTime(parseDateTime(rs.getString("meal_datetime")));

        // ---- ENUMS ----
        e.setGlucoseCategory(parseEnum(GlucoseCategory.class, rs.getString("glucose_category")));
        e.setMealCategory(parseEnum(MealCategory.class, rs.getString("meal_category")));

        // ---- STRINGS ----
        e.setMealDescription(rs.getString("meal_description"));

        return e;
    }

    // ===========================================================
    // HELPERS
    // ===========================================================

    /**
     * Converte um {@link LocalDateTime} para texto ou retorna {@code null}.
     */
    private String toText(LocalDateTime dt) {
        return dt != null ? dt.toString() : null;
    }

    /**
     * Converte um {@link Enum} para texto ou {@code null}.
     */
    private String toText(Enum<?> e) {
        return e != null ? e.name() : null;
    }

    /**
     * Converte uma string textual em {@link LocalDateTime}, caso não seja {@code null}.
     */
    private LocalDateTime parseDateTime(String s) {
        return s != null ? LocalDateTime.parse(s) : null;
    }

    /**
     * Converte texto em um valor de enumeração do tipo especificado.
     *
     * @param clazz classe do enum
     * @param val valor textual salvo no banco
     * @return enum correspondente ou {@code null}
     */
    private <T extends Enum<T>> T parseEnum(Class<T> clazz, String val) {
        return (val == null) ? null : Enum.valueOf(clazz, val);
    }

    /**
     * Busca eventos ocorridos entre duas datas específicas.
     *
     * <p>Considera tanto datas de glicemia quanto de refeição.</p>
     *
     * @param patientId ID do paciente
     * @param start início do intervalo
     * @param end fim do intervalo
     * @return lista de eventos dentro do período
     */
    public List<RoutineEvent> findBetween(int patientId,
                                          LocalDateTime start,
                                          LocalDateTime end) {

        String sql = """
        SELECT * FROM routine_event
        WHERE patient_id = ?
          AND (
                (glucose_datetime IS NOT NULL AND glucose_datetime BETWEEN ? AND ?)
             OR (meal_datetime IS NOT NULL AND meal_datetime BETWEEN ? AND ?)
          )
        ORDER BY id DESC
        """;

        List<RoutineEvent> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.setString(2, start.toString());
            stmt.setString(3, end.toString());
            stmt.setString(4, start.toString());
            stmt.setString(5, end.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapEvent(rs));
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao buscar eventos por intervalo: " + ex.getMessage(), ex);
        }

        return result;
    }
}
