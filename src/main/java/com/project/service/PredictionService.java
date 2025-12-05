package com.project.service;

import com.project.model.Patient;

import java.util.List;

/**
 * Serviço responsável por cálculos de previsão de glicemia pós-prandial
 * e por ajuste de sensibilidade do paciente com base em dados históricos.
 *
 * <p>Não acessa banco de dados. Opera exclusivamente sobre objetos em memória
 * e valores fornecidos pelas camadas superiores.</p>
 */
public class PredictionService {

    /** Valor padrão usado para glicemia pré-refeição quando não informado. */
    private static final float DEFAULT_BASELINE_GLUCOSE = 110.0f;

    /** Valor de índice glicêmico utilizado quando não informado. */
    private static final float DEFAULT_GI = 55.0f;

    /**
     * Calcula a glicemia pós-prandial prevista com base em:
     * <ul>
     *     <li>glicemia pré-refeição (observada ou padrão);</li>
     *     <li>quantidade ingerida de carboidratos;</li>
     *     <li>sensibilidade a carboidratos do paciente;</li>
     *     <li>índice glicêmico estimado.</li>
     * </ul>
     *
     * <p>A fórmula utilizada é:</p>
     *
     * <pre>
     * impacto        = carbs * sensibilidade
     * impacto_final  = impacto * (GI / 100)
     * glicemia_prevista = preGlucose + impacto_final
     * </pre>
     *
     * <p>Essa abordagem é uma simplificação comum adotada em protótipos e
     * aplicativos de controle glicêmico, permitindo estimativas rápidas.</p>
     *
     * @param patient     paciente (para obter a sensibilidade individual)
     * @param preGlucose  glicemia pré-refeição; se {@code null}, usa 110 mg/dL
     * @param carbs       carboidratos ingeridos; se {@code null}, assume 0
     * @param gi          índice glicêmico estimado; se {@code null}, assume 55
     * @return glicemia pós-prandial prevista em mg/dL
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
     * Recalcula a sensibilidade a carboidratos com base em amostras históricas.
     *
     * <p>Para cada amostra, aplica-se a fórmula:</p>
     *
     * <pre>
     * sensibilidade_i = (glicemia_pos - glicemia_pre) / carbs
     * </pre>
     *
     * <p>Somente amostras válidas (carbs > 0) são utilizadas. O resultado final
     * é a média das sensibilidades individuais.</p>
     *
     * @param samples lista de amostras contendo valores pré, pós e carboidratos
     * @return sensibilidade média ou {@code null} se nenhuma amostra for válida
     */
    public Float recomputeSensitivity(List<SensitivitySample> samples) {
        if (samples == null || samples.isEmpty()) {
            return null;
        }

        float sum = 0.0f;
        int count = 0;

        for (SensitivitySample sample : samples) {
            if (sample.carbs() <= 0.0f) {
                continue; // evita divisão por zero ou valores inválidos
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
     * Registro imutável representando uma amostra histórica de sensibilidade.
     *
     * <p>Inclui glicemia pré-refeição, glicemia pós-refeição e quantidade de
     * carboidratos ingeridos. Usado para recalcular a sensibilidade do paciente.</p>
     *
     * @param preGlucose  glicemia pré-refeição
     * @param postGlucose glicemia pós-refeição
     * @param carbs       carboidratos ingeridos em gramas
     */
    public static record SensitivitySample(
            float preGlucose,
            float postGlucose,
            float carbs
    ) {
    }
}