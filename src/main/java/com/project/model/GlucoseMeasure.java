package com.project.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class GlucoseMeasure {
    private int id;
    private float level;
    private LocalDate date;
    private LocalTime time;
    private String note;

    public GlucoseMeasure(float level, LocalDate date, LocalTime time, String note) {
        this.level = level;
        this.date = date;
        this.time = time;
        this.note = note;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getLevel() { return level; }
    public void setLevel(float level) { this.level = level; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return date + " " + time + " - " + level + " - " + note;
    }
}
