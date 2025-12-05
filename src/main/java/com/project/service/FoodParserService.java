package com.project.service;

import com.project.model.ParsedFoodInfo;

/**
 * Serviço responsável por interpretar a descrição textual de uma refeição
 * e extrair informações nutricionais relevantes, como carboidratos e índice glicêmico.
 *
 * <p>No MVP, essa funcionalidade pode ser fornecida por uma implementação fictícia,
 * como {@link com.project.service.BasicFoodParserService}, mas a interface foi criada
 * para permitir substituição futura por um parser real baseado em modelos de linguagem
 * (LLMs) ou bases de dados nutricionais.</p>
 */
public interface FoodParserService {

    /**
     * Interpreta a descrição textual de uma refeição e retorna informações nutricionais
     * estruturadas.
     *
     * @param mealDescription texto descrevendo a refeição consumida
     * @return instância de {@link ParsedFoodInfo} contendo carboidratos/IG estimados
     */
    ParsedFoodInfo parse(String mealDescription);
}
