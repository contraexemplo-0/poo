package com.project.model;

import java.util.ArrayList;
import java.util.List;

public class Historic {
    private final List<GlucoseMeasure> measures;

    public Historic(List<GlucoseMeasure> measures) {
        this.measures = new ArrayList<>(measures);
    }

    public List<GlucoseMeasure> getAll() {
        return new ArrayList<>(measures);
    }
}

