package com.project.persistence;

import com.project.model.Patient;

import java.sql.*;
import java.time.LocalDate;

/**
 * DAO responsável por operações de persistência para Patient.
 */
public class PatientDAO {

    private final Connection conn;

    public PatientDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // =======================================================
    // INSERT
    // =======================================================
    public Patient insert(Patient p) {

        String sql = """
            INSERT INTO patient
            (name, email, password, date_of_birth, gender, diabetes_type, diagnosis_date, carb_sensitivity)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getName());
            stmt.setString(2, p.getEmail());
            stmt.setString(3, p.getPassword());

            stmt.setString(4, p.getDateOfBirth() != null ? p.getDateOfBirth().toString() : null);
            stmt.setString(5, p.getGender());
            stmt.setString(6, p.getDiabetesType());
            stmt.setString(7, p.getDiagnosisDate() != null ? p.getDiagnosisDate().toString() : null);

            stmt.setObject(8, p.getCarbSensitivity());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }

            return p;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir paciente: " + e.getMessage(), e);
        }
    }

    // =======================================================
    // FIND BY EMAIL (usado para login)
    // =======================================================
    public Patient findByEmail(String email) {
        String sql = "SELECT * FROM patient WHERE email = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente por email: " + e.getMessage(), e);
        }
    }

    // =======================================================
    // FIND BY ID
    // =======================================================
    public Patient findById(int id) {
        String sql = "SELECT * FROM patient WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente por id: " + e.getMessage(), e);
        }
    }

    // =======================================================
    // UPDATE
    // =======================================================
    public void update(Patient p) {

        String sql = """
            UPDATE patient SET
                name = ?,
                email = ?,
                password = ?,
                date_of_birth = ?,
                gender = ?,
                diabetes_type = ?,
                diagnosis_date = ?,
                carb_sensitivity = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName());
            stmt.setString(2, p.getEmail());
            stmt.setString(3, p.getPassword());
            stmt.setString(4, p.getDateOfBirth() != null ? p.getDateOfBirth().toString() : null);
            stmt.setString(5, p.getGender());
            stmt.setString(6, p.getDiabetesType());
            stmt.setString(7, p.getDiagnosisDate() != null ? p.getDiagnosisDate().toString() : null);
            stmt.setObject(8, p.getCarbSensitivity());
            stmt.setInt(9, p.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar paciente: " + e.getMessage(), e);
        }
    }

    // =======================================================
    // MAPEAR RESULTSET → PATIENT
    // =======================================================
    private Patient mapPatient(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String password = rs.getString("password");

        String dobStr = rs.getString("date_of_birth");
        LocalDate dob = (dobStr != null) ? LocalDate.parse(dobStr) : null;

        String diagStr = rs.getString("diagnosis_date");
        LocalDate diag = (diagStr != null) ? LocalDate.parse(diagStr) : null;

        Float sensitivity = rs.getObject("carb_sensitivity") != null
                ? rs.getFloat("carb_sensitivity")
                : null;

        return new Patient(
                id,
                name,
                email,
                password,
                dob,
                rs.getString("gender"),
                rs.getString("diabetes_type"),
                diag,
                sensitivity
        );
    }
}
