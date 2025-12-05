package com.project.service;

import com.project.model.RoutineEvent;

import java.util.List;

/**
 * Serviço responsável por calcular estatísticas derivadas de valores de glicemia
 * registrados nos {@link RoutineEvent} do paciente.
 *
 * <p>Essas estatísticas são utilizadas para compor o dashboard, fornecer feedback
 * ao paciente e apoiar análises clínicas simples dentro do sistema.</p>
 */
public class GlucoseStatsService {

    /**
     * Estrutura imutável contendo o resultado das estatísticas calculadas.
     *
     * <p>Inclui média glicêmica, estimativa de HbA1c, contagem de episódios
     * de hipoglicemia e hiperglicemia, além de valores mínimo e máximo.</p>
     */
    public static class GlucoseSummary {
        private final float mean;
        private final float estimatedHbA1c;
        private final int hyperCount;
        private final int hypoCount;
        private final Float min;
        private final Float max;
        private final int totalMeasurements;

        /**
         * Cria um resumo estatístico de glicemia.
         *
         * @param mean média glicêmica
         * @param estimatedHbA1c valor estimado de HbA1c conforme fórmula simplificada
         * @param hyperCount episódios de hiperglicemia (> 180 mg/dL)
         * @param hypoCount episódios de hipoglicemia (< 70 mg/dL)
         * @param min menor glicemia registrada
         * @param max maior glicemia registrada
         * @param totalMeasurements número total de medições válidas
         */
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
     * Calcula estatísticas básicas de glicemia a partir de uma lista de eventos.
     *
     * <p>Somente eventos com glicemia registrada são considerados. Os valores
     * computados incluem:</p>
     * <ul>
     *     <li><b>mean</b>: média aritmética da glicemia;</li>
     *     <li><b>hyperCount</b>: número de valores acima de 180 mg/dL;</li>
     *     <li><b>hypoCount</b>: número de valores abaixo de 70 mg/dL;</li>
     *     <li><b>min / max</b>: menor e maior glicemia encontradas;</li>
     *     <li><b>estimatedHbA1c</b>: HbA1c estimada pela fórmula:</li>
     * </ul>
     *
     * <pre>
     * HbA1c ≈ (mean_glucose + 46.7) / 28.7
     * </pre>
     *
     * <p>Essa fórmula é amplamente utilizada em aplicativos para fornecer
     * uma estimativa rápida da hemoglobina glicada com base na média glicêmica.</p>
     *
     * @param events lista de eventos contendo potenciais medições de glicemia
     * @return objeto {@link GlucoseSummary} contendo todas as estatísticas calculadas
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

        float mean = count > 0 ? sum / count : 0f;

        // Fórmula utilizada para estimar HbA1c a partir da média glicêmica.
        float estimatedHbA1c =
                count > 0 ? (mean + 46.7f) / 28.7f : 0f;

        return new GlucoseSummary(mean, estimatedHbA1c, hyper, hypo, min, max, count);
    }
}
