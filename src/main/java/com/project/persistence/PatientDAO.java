package com.project.persistence;

import com.project.model.Patient;

import java.sql.*;
import java.time.LocalDate;

/**
 * DAO responsável pelas operações de persistência da entidade {@link Patient}.
 *
 * <p>Esta classe encapsula toda interação com a tabela <b>patient</b> do banco SQLite,
 * fornecendo métodos de CRUD simplificados para uso nas camadas de serviço.</p>
 *
 * <p>Não possui dependência de UI e pode ser usada tanto no modo console
 * quanto no JavaFX.</p>
 */
public class PatientDAO {

    /** Conexão JDBC ativa obtida via {@link DatabaseConnection}. */
    private final Connection conn;

    /**
     * Cria uma nova instância do DAO utilizando a conexão única do sistema.
     *
     * @throws SQLException se ocorrer erro ao obter a conexão
     */
    public PatientDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // =======================================================
    // INSERT
    // =======================================================

    /**
     * Insere um novo paciente no banco de dados.
     *
     * <p>Após a inserção, o ID gerado automaticamente (AUTOINCREMENT)
     * é atribuído ao objeto {@link Patient} fornecido.</p>
     *
     * @param p paciente a ser inserido
     * @return o próprio paciente com o ID atualizado
     * @throws RuntimeException se ocorrer erro de SQL
     */
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

            // Recupera o ID gerado automaticamente
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
    // FIND BY EMAIL
    // =======================================================

    /**
     * Busca um paciente pelo email.
     *
     * <p>Método típico utilizado no fluxo de login.</p>
     *
     * @param email email a ser pesquisado
     * @return o paciente correspondente ou {@code null} se não encontrado
     * @throws RuntimeException se ocorrer erro de SQL
     */
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

    /**
     * Busca um paciente pelo ID.
     *
     * @param id identificador do paciente
     * @return o paciente correspondente ou {@code null} se não encontrado
     * @throws RuntimeException se ocorrer erro de SQL
     */
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

    /**
     * Atualiza os dados de um paciente existente.
     *
     * @param p paciente contendo os novos dados
     * @throws RuntimeException se ocorrer erro de SQL
     */
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
    // MAP: RESULTSET → PATIENT
    // =======================================================

    /**
     * Constrói um objeto {@link Patient} a partir de um {@link ResultSet}.
     *
     * <p>Este método centraliza o mapeamento entre a tabela SQL e o modelo
     * de domínio.</p>
     *
     * @param rs linha do banco contendo os dados do paciente
     * @return instância preenchida de {@link Patient}
     * @throws SQLException se ocorrer erro durante a leitura dos campos
     */
    private Patient mapPatient(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String password = rs.getString("password");

        String dobStr = rs.getString("date_of_birth");
        LocalDate dob = (dobStr != null) ? LocalDate.parse(dobStr) : null;

        String diagStr = rs.getString("diagnosis_date");
        LocalDate diag = (diagStr != null) ? LocalDate.parse(diagStr) : null;

        Float sensitivity =
                (rs.getObject("carb_sensitivity") != null)
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