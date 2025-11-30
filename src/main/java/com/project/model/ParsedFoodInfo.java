package com.project.model;

public class ParsedFoodInfo {
    private Float carbs;
    private Float gi;

    public ParsedFoodInfo() {
    }

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
