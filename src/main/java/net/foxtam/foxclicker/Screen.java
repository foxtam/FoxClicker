package net.foxtam.foxclicker;

import java.awt.*;
import java.time.LocalTime;

public class Screen {

    public static final Screen INSTANCE = new Screen();

    private Screen() {
    }

    public Image getCapture(Rectangle rectangle) {
        return Image.from(
            Robo.INSTANCE.createScreenCapture(rectangle),
            "screenshot-" + LocalTime.now());
    }
}
