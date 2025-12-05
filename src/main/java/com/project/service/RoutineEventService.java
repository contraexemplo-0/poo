package com.project.service;

import com.project.model.*;
import com.project.persistence.RoutineEventDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio relacionadas aos
 * {@link RoutineEvent} do paciente.
 *
 * <p>Centraliza toda a lógica de registro, atualização e recuperação de eventos,
 * delegando a persistência para {@link RoutineEventDAO}.</p>
 *
 * <p>Também integra automaticamente um {@link FoodParserService} durante o
 * registro de refeições, permitindo que carboidratos e índice glicêmico sejam
 * estimados a partir da descrição textual.</p>
 */
public class RoutineEventService {

    private final RoutineEventDAO eventDAO;
    private final FoodParserService foodParser;

    /**
     * Cria o serviço de eventos de rotina, inicializando o DAO e o parser.
     *
     * @param foodParser implementação de {@link FoodParserService} para estimar carboidratos/IG
     * @throws SQLException se ocorrer erro ao inicializar o DAO
     */
    public RoutineEventService(FoodParserService foodParser) throws SQLException {
        this.eventDAO = new RoutineEventDAO();
        this.foodParser = foodParser;
    }

    // ==========================================================
    // Registrar apenas glicemia
    // ==========================================================

    /**
     * Registra uma medição de glicemia isolada para o paciente.
     *
     * @param patient paciente responsável pelo registro
     * @param level nível de glicemia em mg/dL
     * @param category categoria clínica da medição
     * @param dateTime data e hora da medição
     * @return evento persistido contendo a glicemia registrada
     */
    public RoutineEvent registerGlucose(Patient patient,
                                        float level,
                                        GlucoseCategory category,
                                        LocalDateTime dateTime) {

        RoutineEvent event = new RoutineEvent();
        event.setPatientId(patient.getId());
        event.setGlucoseLevel(level);
        event.setGlucoseCategory(category);
        event.setGlucoseDateTime(dateTime);

        return eventDAO.insert(event);
    }

    // ==========================================================
    // Registrar refeição + glicemia completa
    // ==========================================================

    /**
     * Registra uma refeição e uma glicemia pós-prandial no mesmo evento.
     *
     * <p>A refeição é processada pelo {@link FoodParserService}, caso disponível,
     * para estimar carboidratos e índice glicêmico automaticamente.</p>
     *
     * @param patient paciente associado ao evento
     * @param mealDescription descrição textual da refeição
     * @param mealDateTime horário da refeição
     * @param mealCategory categoria da refeição
     * @param glucoseLevel glicemia aferida
     * @param glucoseCategory categoria da glicemia
     * @param glucoseDateTime horário da aferição
     * @return evento persistido contendo refeição + glicemia
     */
    public RoutineEvent registerMealAndGlucose(Patient patient,
                                               String mealDescription,
                                               LocalDateTime mealDateTime,
                                               MealCategory mealCategory,
                                               float glucoseLevel,
                                               GlucoseCategory glucoseCategory,
                                               LocalDateTime glucoseDateTime) {

        RoutineEvent event = createMealEvent(patient, mealDescription, mealDateTime, mealCategory);

        event.setGlucoseLevel(glucoseLevel);
        event.setGlucoseCategory(glucoseCategory);
        event.setGlucoseDateTime(glucoseDateTime);

        return eventDAO.insert(event);
    }

    // ==========================================================
    // Registrar apenas refeição
    // ==========================================================

    /**
     * Registra apenas uma refeição, sem glicemia associada.
     *
     * <p>O serviço tenta interpretar automaticamente a refeição via parser,
     * estimando carboidratos e índice glicêmico (se possível).</p>
     *
     * @param patient paciente responsável pelo registro
     * @param mealDescription descrição textual da refeição
     * @param mealDateTime horário da refeição
     * @param mealCategory categoria da refeição
     * @return evento persistido contendo apenas a refeição
     */
    public RoutineEvent registerMealOnly(Patient patient,
                                         String mealDescription,
                                         LocalDateTime mealDateTime,
                                         MealCategory mealCategory) {

        RoutineEvent event = createMealEvent(patient, mealDescription, mealDateTime, mealCategory);
        return eventDAO.insert(event);
    }

    // ==========================================================
    // Atualizar evento adicionando glicemia mais tarde
    // ==========================================================

    /**
     * Atualiza um evento previamente registrado adicionando ou substituindo
     * o valor de glicemia.
     *
     * @param event evento já existente
     * @param glucoseLevel novo valor de glicemia
     * @param category categoria da glicemia
     * @param dateTime data/hora da medição
     */
    public void updateEventWithGlucose(RoutineEvent event,
                                       float glucoseLevel,
                                       GlucoseCategory category,
                                       LocalDateTime dateTime) {

        event.setGlucoseLevel(glucoseLevel);
        event.setGlucoseCategory(category);
        event.setGlucoseDateTime(dateTime);

        eventDAO.update(event);
    }

    // ==========================================================
    // Carregar eventos com opção de filtro por dias
    // ==========================================================

    /**
     * Carrega eventos do paciente, com opção de limitar pelos últimos N dias.
     *
     * @param patient paciente dono dos eventos
     * @param days número de dias para filtro; se 0 ou negativo, retorna todos
     * @return lista de eventos do paciente
     */
    public List<RoutineEvent> loadEvents(Patient patient, int days) {
        if (days > 0) {
            return eventDAO.findByPatientAndLastDays(patient.getId(), days);
        }
        return eventDAO.findAllByPatientId(patient.getId());
    }

    /**
     * Carrega todos os eventos do paciente, sem filtros.
     *
     * @param patient paciente dono dos eventos
     * @return lista completa de eventos
     */
    public List<RoutineEvent> loadEvents(Patient patient) {
        return eventDAO.findAllByPatientId(patient.getId());
    }

    // ==========================================================
    // Deletar evento
    // ==========================================================

    /**
     * Remove um evento do banco de dados pelo ID.
     *
     * @param eventId identificador do evento a ser removido
     */
    public void deleteEvent(int eventId) {
        eventDAO.delete(eventId);
    }

    // ==========================================================
    // Factory interna para criação de eventos de refeição
    // ==========================================================

    /**
     * Cria um {@link RoutineEvent} do tipo refeição, preenchendo descrição,
     * horário e categoria. Utiliza o parser de alimentos para estimar
     * carboidratos e índice glicêmico.
     *
     * @param patient paciente associado
     * @param mealDescription descrição textual
     * @param mealDateTime data/hora da refeição
     * @param mealCategory categoria da refeição
     * @return evento parcialmente preenchido (ainda não persistido)
     */
    private RoutineEvent createMealEvent(Patient patient,
                                         String mealDescription,
                                         LocalDateTime mealDateTime,
                                         MealCategory mealCategory) {

        RoutineEvent event = new RoutineEvent();
        event.setPatientId(patient.getId());
        event.setMealDescription(mealDescription);
        event.setMealDateTime(mealDateTime);
        event.setMealCategory(mealCategory);

        // Integração com parser automático (BasicFoodParser, LLM, etc.)
        ParsedFoodInfo info = (foodParser != null)
                ? foodParser.parse(mealDescription)
                : null;

        if (info != null) {
            event.setCarbs(info.getCarbs());
            event.setGi(info.getGi());
        }

        return event;
    }

    /**
     * Salva o evento, decidindo automaticamente entre INSERT e UPDATE.
     *
     * @param event evento a ser persistido
     * @return o próprio evento salvo
     */
    public RoutineEvent saveEvent(RoutineEvent event) {
        if (event.getId() == 0) {
            return eventDAO.insert(event);
        } else {
            eventDAO.update(event);
            return event;
        }
    }
}