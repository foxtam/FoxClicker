package net.foxtam.foxclicker;

import lombok.SneakyThrows;

import java.awt.*;

public class Robo {
    private static Robot INSTANCE;

    private Robo() {
    }

    @SneakyThrows
    public static synchronized Robot getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Robot();
        }
        return INSTANCE;
    }
}
