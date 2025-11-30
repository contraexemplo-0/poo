package com.project.service;

import com.project.model.ParsedFoodInfo;

public interface FoodParserService {
    ParsedFoodInfo parse(String mealDescription);
}
