package net.foxtam.foxclicker.screen;

import net.foxtam.foxclicker.Robo;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SimpleScreen implements Screen {

    private static SimpleScreen INSTANCE;

    public static synchronized SimpleScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SimpleScreen();
        }
        return INSTANCE;
    }

    @Override
    public BufferedImage getCapture(Rectangle rectangle) {
        return Robo.getInstance().createScreenCapture(rectangle);
    }
}
