package com.project.service;

import com.project.model.Patient;

import java.util.List;

/**
 * Serviço responsável por cálculos de previsão de glicemia pós-prandial
 * e ajuste de sensibilidade do paciente.
 *
 * Não acessa banco de dados – trabalha apenas com objetos em memória.
 */
public class PredictionService {

    private static final float DEFAULT_BASELINE_GLUCOSE = 110.0f;
    private static final float DEFAULT_GI = 55.0f;

    /**
     * Calcula a glicemia pós-prandial prevista.
     *
     * Fórmula:
     *   impacto = carbs * sensibilidade
     *   impacto_final = impacto * (GI / 100)
     *   glicemia_prevista = glicemia_pre + impacto_final
     *
     * @param patient     paciente (para obter a sensibilidade)
     * @param preGlucose  glicemia pré-refeição (pode ser null → usa valor padrão)
     * @param carbs       carboidratos da refeição em gramas (pode ser null → 0)
     * @param gi          índice glicêmico estimado (pode ser null → 55)
     * @return glicemia prevista em mg/dL
     */
    public float predictPostPrandial(Patient patient,
                                     Float preGlucose,
                                     Float carbs,
                                     Float gi) {

        float baseline = (preGlucose != null) ? preGlucose : DEFAULT_BASELINE_GLUCOSE;
        float carbsSafe = (carbs != null) ? carbs : 0.0f;
        float giSafe = (gi != null) ? gi : DEFAULT_GI;

        float sensitivity = (patient != null)
                ? patient.getEffectiveCarbSensitivity()
                : Patient.DEFAULT_CARB_SENSITIVITY;

        float impact = carbsSafe * sensitivity;
        float impactFinal = impact * (giSafe / 100.0f);

        return baseline + impactFinal;
    }

    /**
     * Recalcula a sensibilidade a partir de observações históricas.
     *
     * Para cada amostra:
     *   sensibilidade_i = (glicemia_pos - glicemia_pre) / carbs
     * E retorna a média dessas sensibilidades.
     *
     * @param samples lista de amostras com (glicemia_pre, glicemia_pos, carbs)
     * @return nova sensibilidade média ou null se não for possível calcular
     */
    public Float recomputeSensitivity(List<SensitivitySample> samples) {
        if (samples == null || samples.isEmpty()) {
            return null;
        }

        float sum = 0.0f;
        int count = 0;

        for (SensitivitySample sample : samples) {
            if (sample.carbs() <= 0.0f) {
                // evita divisão por zero ou valores inválidos
                continue;
            }
            float delta = sample.postGlucose() - sample.preGlucose();
            float sens = delta / sample.carbs();
            sum += sens;
            count++;
        }

        if (count == 0) {
            return null;
        }

        return sum / count;
    }

    /**
     * Pequeno DTO imutável para representar uma experiência histórica do paciente.
     */
    public static record SensitivitySample(
            float preGlucose,
            float postGlucose,
            float carbs
    ) {
    }
}