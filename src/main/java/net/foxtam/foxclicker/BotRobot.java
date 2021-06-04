package net.foxtam.foxclicker;

import net.foxtam.foxclicker.exceptions.AWTRuntimeException;

import java.awt.*;

public class BotRobot {
    public static final Robot INSTANCE;

    static {
        try {
            INSTANCE = new Robot();
        } catch (AWTException e) {
            throw new AWTRuntimeException(e);
        }
    }
}
