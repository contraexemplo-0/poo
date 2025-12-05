package com.project.model;

/**
 * Representa informações nutricionais básicas extraídas automaticamente de um texto
 * (por exemplo, via modelo de NLP).
 *
 * <p>O objetivo dessa classe é encapsular os valores estimados de carboidratos
 * e índice glicêmico de um alimento, permitindo que o sistema utilize tais dados
 * na previsão de glicemia pós-prandial.</p>
 */
public class ParsedFoodInfo {

    /** Quantidade estimada de carboidratos em gramas. */
    private Float carbs;

    /** Índice glicêmico estimado do alimento. */
    private Float gi;

    /**
     * Construtor padrão.
     */
    public ParsedFoodInfo() {
    }

    /**
     * Construtor completo.
     *
     * @param carbs quantidade estimada de carboidratos (g)
     * @param gi índice glicêmico estimado
     */
    public ParsedFoodInfo(Float carbs, Float gi) {
        this.carbs = carbs;
        this.gi = gi;
    }

    public Float getCarbs() {
        return carbs;
    }

    public void setCarbs(Float carbs) {
        this.carbs = carbs;
    }

    public Float getGi() {
        return gi;
    }

    public void setGi(Float gi) {
        this.gi = gi;
    }
}