package com.project.model;

/**
 * Categorias possíveis para uma medição de glicemia.
 *
 * <p>Cada categoria representa o contexto fisiológico em que a glicose foi
 * medida, permitindo análises mais precisas e segmentadas.</p>
 *
 * <ul>
 *     <li>{@link #FASTING} — jejum</li>
 *     <li>{@link #PRE_PRANDIAL} — antes das refeições</li>
 *     <li>{@link #POST_PRANDIAL} — após as refeições</li>
 *     <li>{@link #RANDOM} — aleatória, fora de um contexto específico</li>
 * </ul>
 */
public enum GlucoseCategory {
    FASTING,
    PRE_PRANDIAL,
    POST_PRANDIAL,
    RANDOM
}
