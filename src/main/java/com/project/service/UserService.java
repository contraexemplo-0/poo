package com.project.service;

import com.project.model.GlucoseMeasure;
import com.project.model.HealthProfessional;
import com.project.model.Historic;
import com.project.model.Patient;
import com.project.model.User;
import com.project.model.UserType;
import com.project.persistence.DatabaseManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Serviço que concentra as regras de negócio relacionadas a usuários e suas
 * medições de glicose.
 */
public class UserService {
    private final DatabaseManager db;

    /**
     * Cria o serviço utilizando a camada de persistência fornecida.
     *
     * @param db gerenciador de banco de dados compartilhado.
     */
    public UserService(DatabaseManager db) {
        this.db = db;
    }

    // Cadastro
    /**
     * Registra um novo usuário com o tipo informado.
     *
     * @param name     nome do usuário.
     * @param password senha do usuário.
     * @param type     tipo de usuário a ser criado.
     * @return instância concreta do usuário criado.
     * @throws SQLException em falhas de persistência.
     */
    public User register(String name, String password, UserType type) throws SQLException {
        int id = db.insertUser(name, password, type);
        return createUser(id, name, password, type);
    }

    // Login
    /**
     * Realiza o processo de autenticação e carrega dados adicionais se necessário.
     *
     * @param name     nome informado.
     * @param password senha informada.
     * @return usuário autenticado ou {@code null} se inválido.
     * @throws SQLException em falhas de consulta.
     */
    public User login(String name, String password) throws SQLException {
        User user = db.findUserByName(name);
        if (user != null && user.checkPassword(password)) {
            if (user instanceof Patient patient) {
                loadHistoric(patient);
            }
            return user;
        }
        return null;
    }

    /**
     * Carrega o histórico completo de medidas para um paciente.
     *
     * @param patient paciente que terá os dados atualizados.
     * @return instância de {@link Historic} com os registros encontrados.
     * @throws SQLException em falhas de consulta.
     */
    public Historic loadHistoric(Patient patient) throws SQLException {
        List<GlucoseMeasure> measures = db.loadMeasures(patient);
        return new Historic(measures);
    }

    // Adiciona nova medida
    /**
     * Adiciona uma nova medida com observação opcional.
     *
     * @param user  usuário autenticado.
     * @param level valor da glicemia.
     * @param note  observação opcional associada.
     * @return medida criada.
     * @throws SQLException              em falhas de persistência.
     * @throws IllegalArgumentException  se o usuário não for paciente ou nível inválido.
     */
    public GlucoseMeasure addMeasure(User user, float level, String note) throws SQLException {
        ensurePatient(user);
        if (level < 0) throw new IllegalArgumentException("Valor de glicose não pode ser negativo");
        Patient patient = (Patient) user;
        GlucoseMeasure m = new GlucoseMeasure(patient, level, LocalDate.now(), LocalTime.now(), note);
        db.insertMeasure(patient.getId(), m);
        return m;
    }

    /**
     * Adiciona uma nova medida sem observação.
     *
     * @param user  usuário autenticado.
     * @param level valor da glicemia.
     * @return medida criada.
     * @throws SQLException             em falhas de persistência.
     * @throws IllegalArgumentException se o usuário não for paciente.
     */
    public GlucoseMeasure addMeasure(User user, float level) throws SQLException {
        ensurePatient(user);
        Patient patient = (Patient) user;
        GlucoseMeasure m = new GlucoseMeasure(patient, level, LocalDate.now(), LocalTime.now()); // note = null
        db.insertMeasure(patient.getId(), m);
        return m;
    }


    /**
     * Remove uma medida de glicose de um paciente autenticado.
     *
     * @param user      paciente que solicita a remoção.
     * @param measureId identificador da medida a ser removida.
     * @throws SQLException             em falhas de exclusão.
     * @throws IllegalArgumentException se o usuário não for paciente.
     */
    public void removeMeasure(User user, int measureId) throws SQLException{
        ensurePatient(user);
        db.deleteMeasure(user.getId(), measureId);
    }

    /**
     * Busca um usuário pelo nome.
     *
     * @param name nome utilizado na pesquisa.
     * @return usuário encontrado ou {@code null} caso não exista.
     * @throws SQLException em falhas de consulta.
     */
    public User findByName(String name) throws SQLException {
        return db.findUserByName(name);
    }

    /**
     * Garante que o usuário informado é um paciente, evitando operações inválidas.
     *
     * @param user usuário que terá o tipo verificado.
     */
    private void ensurePatient(User user) {
        if (!(user instanceof Patient)) {
            throw new IllegalArgumentException("Somente pacientes podem registrar ou remover medidas.");
        }
    }

    /**
     * Cria instâncias concretas de usuários a partir dos metadados recuperados.
     *
     * @param id       identificador do usuário.
     * @param name     nome do usuário.
     * @param password senha do usuário.
     * @param type     tipo do usuário.
     * @return instância concreta de {@link User}.
     */
    private User createUser(int id, String name, String password, UserType type) {
        return switch (type) {
            case PATIENT -> new Patient(id, name, password);
            case HEALTH_PROFESSIONAL -> new HealthProfessional(id, name, password);
        };
    }
}
