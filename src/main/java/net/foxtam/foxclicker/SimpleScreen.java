package net.foxtam.foxclicker;

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
    public BufferedImage getScreenCapture(Rectangle rectangle) {
        return Robo.getInstance().createScreenCapture(rectangle);
    }
}
