package com.project.persistence;

import com.project.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável por persistir RoutineEvent (eventos de rotina).
 */
public class RoutineEventDAO {

    private final Connection conn;

    public RoutineEventDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ===========================================================
    // INSERT
    // ===========================================================
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
    private RoutineEvent mapEvent(ResultSet rs) throws SQLException {

        RoutineEvent e = new RoutineEvent();

        e.setId(rs.getInt("id"));
        e.setPatientId(rs.getInt("patient_id"));

        // ---- FLOATS (sempre via Number) ----
        e.setGlucoseLevel(
                rs.getObject("glucose_level") != null ?
                        ((Number) rs.getObject("glucose_level")).floatValue() : null
        );

        e.setCarbs(
                rs.getObject("carbs") != null ?
                        ((Number) rs.getObject("carbs")).floatValue() : null
        );

        e.setGi(
                rs.getObject("gi") != null ?
                        ((Number) rs.getObject("gi")).floatValue() : null
        );

        e.setWeight(
                rs.getObject("weight") != null ?
                        ((Number) rs.getObject("weight")).floatValue() : null
        );

        // ---- INTEIROS ----
        e.setActivityMinutes(
                rs.getObject("activity_minutes") != null ?
                        ((Number) rs.getObject("activity_minutes")).intValue() : null
        );

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
    private String toText(LocalDateTime dt) {
        return dt != null ? dt.toString() : null;
    }

    private String toText(Enum<?> e) {
        return e != null ? e.name() : null;
    }

    private LocalDateTime parseDateTime(String s) {
        return s != null ? LocalDateTime.parse(s) : null;
    }

    private <T extends Enum<T>> T parseEnum(Class<T> clazz, String val) {
        return (val == null) ? null : Enum.valueOf(clazz, val);
    }

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
