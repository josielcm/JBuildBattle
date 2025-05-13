package me.josielcm.jcm.game;

import lombok.Getter;

public enum GameTheme {
    CITY("Ciudad"),
    CASTLE("Castillo"),
    LANDSCAPE("Paisaje"),
    COLOR("Colorido"),
    NONE("Ninguno");

    @Getter
    private final String name;

    GameTheme(String name) {
        this.name = name;
    }
}
