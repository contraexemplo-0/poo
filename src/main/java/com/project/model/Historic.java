package com.project.model;

import java.util.ArrayList;
import java.util.List;

public class Historic {
    private final List<RoutineEvent> events;

    public Historic(List<? extends RoutineEvent> events) {
        this.events = new ArrayList<>(events);
    }

    public List<RoutineEvent> getAll() {
        return new ArrayList<>(events);
    }
}

