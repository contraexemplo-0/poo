package com.project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa um evento recorrente associado a um paciente.
 * Armazena data, horário e disponibiliza utilitários comuns para
 * subclasses que representam eventos específicos.
 */
public abstract class RoutineEvent {
    private int id;
    private LocalDate date;
    private LocalTime time;
    private Patient patient;

    protected RoutineEvent(LocalDate date, LocalTime time, Patient patient) {
        setDate(date);
        setTime(time);
        setPatient(patient);
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) {
        this.date = Objects.requireNonNull(date, "Data não pode ser nula");
    }

    public LocalTime getTime() { return time; }

    public void setTime(LocalTime time) {
        this.time = Objects.requireNonNull(time, "Hora não pode ser nula");
    }

    public Patient getPatient() { return patient; }

    public void setPatient(Patient patient) {
        this.patient = Objects.requireNonNull(patient, "Paciente não pode ser nulo");
    }

    public LocalDateTime getTimestamp() {
        return LocalDateTime.of(date, time);
    }

    public String getFormattedTimestamp() {
        return getDate() + " " + getTime();
    }

    public abstract String getFormattedSummary();

    @Override
    public String toString() {
        return getFormattedTimestamp() + " - " + getFormattedSummary();
    }
}

