package com.project.service;

import com.project.model.ParsedFoodInfo;

/**
 * Implementação fictícia utilizada enquanto a integração com o modelo LLM não está disponível.
 */
public class BasicFoodParserService implements FoodParserService {
    @Override
    public ParsedFoodInfo parse(String mealDescription) {
        // Placeholder: no momento não há interpretação real.
        return new ParsedFoodInfo(null, null);
    }
}
