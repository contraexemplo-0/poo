package com.project.service;

import com.project.model.RoutineEvent;
import java.util.List;

public class GlucoseStatsService {

    public static class GlucoseSummary {
        private final float mean;
        private final float estimatedHbA1c;
        private final int hyperCount;
        private final int hypoCount;
        private final Float min;
        private final Float max;
        private final int totalMeasurements;

        public GlucoseSummary(float mean, float estimatedHbA1c,
                              int hyperCount, int hypoCount,
                              Float min, Float max, int totalMeasurements) {
            this.mean = mean;
            this.estimatedHbA1c = estimatedHbA1c;
            this.hyperCount = hyperCount;
            this.hypoCount = hypoCount;
            this.min = min;
            this.max = max;
            this.totalMeasurements = totalMeasurements;
        }

        public float getMean() { return mean; }
        public float getEstimatedHbA1c() { return estimatedHbA1c; }
        public int getHyperCount() { return hyperCount; }
        public int getHypoCount() { return hypoCount; }
        public Float getMin() { return min; }
        public Float getMax() { return max; }
        public int getTotalMeasurements() { return totalMeasurements; }
    }

    /**
     * Computa estatísticas básicas (média, hiper, hipo, HbA1c)
     * com base na lista de eventos contendo glicemia.
     */
    public GlucoseSummary computeSummary(List<RoutineEvent> events) {

        float sum = 0f;
        int count = 0;
        int hyper = 0;
        int hypo = 0;
        Float min = null;
        Float max = null;

        for (RoutineEvent e : events) {
            if (e.getGlucoseLevel() == null) continue;

            float g = e.getGlucoseLevel();
            sum += g;
            count++;

            if (min == null || g < min) min = g;
            if (max == null || g > max) max = g;

            if (g < 70) hypo++;
            if (g > 180) hyper++;
        }

        float mean = count > 0 ? sum / count : 0;

        // Fórmula comum usada em muitos apps:
        // HbA1c ≈ (média_glicose + 46.7) / 28.7
        float estimatedHbA1c = count > 0 ? (mean + 46.7f) / 28.7f : 0;

        return new GlucoseSummary(mean, estimatedHbA1c, hyper, hypo, min, max, count);
    }
}
