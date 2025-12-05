package com.project.service;

import com.project.model.ParsedFoodInfo;

/**
 * Implementação básica e provisória do serviço de interpretação de alimentos.
 *
 * <p>Esta classe funciona como um <b>mock</b> ou <b>stub</b> enquanto a integração
 * com um modelo de linguagem (LLM) não está disponível. Atualmente, não realiza
 * nenhuma análise real da descrição textual da refeição.</p>
 *
 * <p>Seu objetivo é permitir que o restante do sistema (especialmente o fluxo de
 * registro de refeições e cálculo futuro de previsões) funcione sem quebrar.</p>
 */
public class BasicFoodParserService implements FoodParserService {

    /**
     * Retorna uma instância vazia de {@link ParsedFoodInfo}, pois esta implementação
     * não possui capacidade real de interpretação.
     *
     * @param mealDescription texto descrevendo a refeição
     * @return {@link ParsedFoodInfo} com valores {@code null}
     */
    @Override
    public ParsedFoodInfo parse(String mealDescription) {
        // Implementação placeholder / mock
        return new ParsedFoodInfo(null, null);
    }
}
