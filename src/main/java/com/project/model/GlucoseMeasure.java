package com.project.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class GlucoseMeasure extends RoutineEvent {
    private float level;
    private String note;

    public GlucoseMeasure(Patient patient, float level, LocalDate date, LocalTime time, String note) {
        super(date, time, patient);
        this.level = level;
        this.note = note;
    }

    public GlucoseMeasure(Patient patient, float level, LocalDate date, LocalTime time) {
        this(patient, level, date, time, null);
    }

    public float getLevel() { return level; }

    public void setLevel(float level) { this.level = level; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    @Override
    public String getFormattedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(level);
        if (note != null && !note.isBlank()) {
            sb.append(" - ").append(note.trim());
        }
        return sb.toString();
    }
}
