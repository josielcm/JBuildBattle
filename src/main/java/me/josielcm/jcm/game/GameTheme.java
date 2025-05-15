package me.josielcm.jcm.game;

import lombok.Getter;

public enum GameTheme {
    CITY("Ciudad", "<gradient:#ECECEC:#FBFBFB><b>Ciudad</b></gradient>"),
    CASTLE("Castillo", "<gradient:#B5B5B5:#C1C1C1><b>Castillo</b></gradient>"),
    LANDSCAPE("Paisaje", "<gradient:#74FFFF:#98FF7C><b>Paisaje</b></gradient>"),
    COLOR("Colorido", "<gradient:#FF6B6B:#FFAF70:#FFF275:#C7EE80:#8EEA8B:#6A9EFF:#7B4FFF:#8B00FF><b>Colorido</b></gradient>"),
    NONE("Ninguno", "<red>");

    @Getter
    private final String name;

    @Getter
    private final String nameColored;

    GameTheme(String name, String namedColored) {
        this.name = name;
        this.nameColored = namedColored;
    }
}
