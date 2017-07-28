package com.wglover.plateplanner.classes;

public enum Unit {
    quantity("ea"),
    cups("cup"),
    teaspoons("tsp"),
    tablespoons("tbsp"),
    gallons("gal"),
    grams("g"),
    kilograms("kg"),
    ounces("oz"),
    pounds("lbs"),
    quarts("qt");

    public final String abbreviation;

    Unit(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
